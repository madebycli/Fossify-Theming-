package org.fossify.thankyou.helpers

private val knownFossifyPackages = setOf(
    "org.fossify.calendar",
    "org.fossify.camera",
    "org.fossify.clock",
    "org.fossify.commons.samples",
    "org.fossify.contacts",
    "org.fossify.documents",
    "org.fossify.draw",
    "org.fossify.filemanager",
    "org.fossify.flashlight",
    "org.fossify.gallery",
    "org.fossify.home",
    "org.fossify.keyboard",
    "org.fossify.math",
    "org.fossify.messages",
    "org.fossify.musicplayer",
    "org.fossify.notes",
    "org.fossify.phone",
    "org.fossify.voicerecorder",
)

fun isKnownFossifyPackage(packageName: String): Boolean {
    return packageName.removeSuffix(".debug") in knownFossifyPackages
}
