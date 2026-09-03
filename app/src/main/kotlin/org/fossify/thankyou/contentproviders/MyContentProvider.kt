package org.fossify.thankyou.contentproviders

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_CREATE
import android.os.ParcelFileDescriptor.MODE_TRUNCATE
import android.os.ParcelFileDescriptor.MODE_WRITE_ONLY
import android.os.Process
import org.fossify.commons.extensions.isFontFile
import org.fossify.thankyou.helpers.MyContentProviderHelper
import java.io.File
import java.io.FileNotFoundException

class MyContentProvider : ContentProvider() {
    private lateinit var dbHelper: MyContentProviderHelper

    companion object {
        private const val AUTHORITY = "org.fossify.android.provider"
        private const val SETTINGS = 1
        private const val FONTS_FILE = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "settings", SETTINGS)
            addURI(AUTHORITY, "fonts/*", FONTS_FILE)
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?) = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        enforceFossifyCaller()
        return dbHelper.getGlobalConfigCursor()
    }

    override fun onCreate(): Boolean {
        dbHelper = MyContentProviderHelper.newInstance(context!!)
        return true
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        enforceFossifyCaller()
        return dbHelper.updatePreferences(contentValues!!)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri) = ""

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        enforceFossifyCaller()
        if (uriMatcher.match(uri) != FONTS_FILE) throw FileNotFoundException(uri.toString())
        val name = uri.lastPathSegment ?: throw FileNotFoundException(uri.toString())
        val safeName = File(name).name
        if (!safeName.isFontFile()) throw FileNotFoundException("Not a font file")

        val fontsDir = File(context!!.filesDir, "fonts").apply { mkdirs() }
        val file = File(fontsDir, safeName)

        return when {
            mode.contains("w") -> {
                ParcelFileDescriptor.open(
                    file,
                    MODE_CREATE or MODE_TRUNCATE or MODE_WRITE_ONLY
                )
            }

            else -> {
                if (!file.exists()) throw FileNotFoundException(uri.toString())
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            }
        }
    }

    private fun enforceFossifyCaller() {
        if (Binder.getCallingUid() == Process.myUid()) return

        val callingPackages = context
            ?.packageManager
            ?.getPackagesForUid(Binder.getCallingUid())
            .orEmpty()

        if (callingPackages.none { it.startsWith("org.fossify.") }) {
            throw SecurityException("Only Fossify apps can access the global theme provider")
        }
    }
}
