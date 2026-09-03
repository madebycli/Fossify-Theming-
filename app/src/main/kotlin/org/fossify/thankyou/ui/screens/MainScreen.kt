@file:OptIn(ExperimentalMaterial3Api::class)

package org.fossify.thankyou.ui.screens

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.fossify.commons.compose.alert_dialog.rememberAlertDialogState
import org.fossify.commons.compose.extensions.BooleanPreviewParameterProvider
import org.fossify.commons.compose.extensions.MyDevices
import org.fossify.commons.compose.lists.SimpleScaffold
import org.fossify.commons.compose.lists.simpleTopAppBarColors
import org.fossify.commons.compose.lists.topAppBarInsets
import org.fossify.commons.compose.lists.topAppBarPaddings
import org.fossify.commons.compose.settings.SettingsGroupTitle
import org.fossify.commons.compose.theme.SimpleTheme
import org.fossify.commons.dialogs.ColorPickerAlertDialog
import org.fossify.thankyou.R
import org.fossify.thankyou.models.FossifyApp
import org.fossify.thankyou.models.ThemeColorRole
import org.fossify.thankyou.models.ThemeProfile
import org.fossify.thankyou.models.ThemeSettings
import org.fossify.thankyou.ui.components.FossifyApp
import org.fossify.thankyou.ui.theme.ThemingThemeSurface

@Composable
internal fun MainScreen(
    allApps: List<FossifyApp>,
    fakeApps: List<FossifyApp>,
    settings: ThemeSettings,
    profiles: List<ThemeProfile>,
    systemColors: Map<ThemeColorRole, Int>,
    onSettingsChanged: (ThemeSettings) -> Unit,
    onApplyNow: () -> Unit,
    onSaveProfile: (String) -> Unit,
    onLoadProfile: (ThemeProfile) -> Unit,
    onDeleteProfile: (ThemeProfile) -> Unit,
    openSettings: () -> Unit,
    launchApp: (packageName: String) -> Unit,
    uninstallApp: (packageName: String) -> Unit,
) {
    SimpleScaffold(
        customTopBar = {
                scrolledColor: Color,
                _: MutableInteractionSource,
                scrollBehavior: TopAppBarScrollBehavior,
                statusBarColor: Int,
                colorTransitionFraction: Float,
                contrastColor: Color ->
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_brand_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = scrolledColor,
                    )
                },
                actions = {
                    IconButton(onClick = openSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(org.fossify.commons.R.string.settings),
                            tint = scrolledColor,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = simpleTopAppBarColors(
                    statusBarColor,
                    colorTransitionFraction,
                    contrastColor,
                ),
                modifier = Modifier.topAppBarPaddings(),
                windowInsets = topAppBarInsets(),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item(key = "theme_dashboard") {
                ThemeDashboard(
                    settings = settings,
                    profiles = profiles,
                    systemColors = systemColors,
                    onSettingsChanged = onSettingsChanged,
                    onApplyNow = onApplyNow,
                    onSaveProfile = onSaveProfile,
                    onLoadProfile = onLoadProfile,
                    onDeleteProfile = onDeleteProfile,
                )
            }

            fossifyApps(
                titleId = R.string.potentially_unsafe_apps,
                apps = fakeApps,
                launchApp = launchApp,
                uninstallApp = uninstallApp,
            )

            fossifyApps(
                titleId = R.string.installed_fossify_apps,
                apps = allApps,
                launchApp = launchApp,
                uninstallApp = uninstallApp,
            )
        }
    }
}

@Composable
private fun ThemeDashboard(
    settings: ThemeSettings,
    profiles: List<ThemeProfile>,
    systemColors: Map<ThemeColorRole, Int>,
    onSettingsChanged: (ThemeSettings) -> Unit,
    onApplyNow: () -> Unit,
    onSaveProfile: (String) -> Unit,
    onLoadProfile: (ThemeProfile) -> Unit,
    onDeleteProfile: (ThemeProfile) -> Unit,
) {
    var profileName by remember { mutableStateOf("") }
    var editingRole by remember { mutableStateOf<ThemeColorRole?>(null) }

    val colorPickerState = rememberAlertDialogState().apply {
        DialogMember {
            val role = editingRole ?: return@DialogMember
            ColorPickerAlertDialog(
                alertDialogState = this,
                color = settings.customColor(role),
                removeDimmedBackground = true,
                onActiveColorChange = {},
            ) { wasPositivePressed, color ->
                if (wasPositivePressed) {
                    onSettingsChanged(
                        settings
                            .withCustomColor(role, color)
                            .withSystem(role, false)
                    )
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.hybrid_theme_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.hybrid_theme_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ThemeColorRole.entries.forEach { role ->
                ColorRoleRow(
                    role = role,
                    usesSystem = settings.usesSystem(role),
                    customColor = settings.customColor(role),
                    systemColor = systemColors[role] ?: settings.customColor(role),
                    onUseSystem = {
                        onSettingsChanged(settings.withSystem(role, true))
                    },
                    onUseCustom = {
                        onSettingsChanged(settings.withSystem(role, false))
                        editingRole = role
                        colorPickerState.show()
                    },
                    onPickColor = {
                        editingRole = role
                        colorPickerState.show()
                    },
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.live_sync_title),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.live_sync_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = settings.liveSyncEnabled,
                    onCheckedChange = {
                        onSettingsChanged(settings.copy(liveSyncEnabled = it))
                    },
                )
            }

            Button(
                onClick = onApplyNow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.apply_now))
            }

            HorizontalDivider()

            Text(
                text = stringResource(R.string.profiles_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text(stringResource(R.string.profile_name)) },
                )
                FilledTonalButton(
                    onClick = {
                        if (profileName.isNotBlank()) {
                            onSaveProfile(profileName)
                            profileName = ""
                        }
                    },
                ) {
                    Text(stringResource(R.string.save_profile))
                }
            }

            if (profiles.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_profiles),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                profiles.forEach { profile ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AssistChip(
                            onClick = { onLoadProfile(profile) },
                            label = { Text(profile.name) },
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { onDeleteProfile(profile) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_profile),
                            )
                        }
                    }
                }
            }

            Text(
                text = stringResource(R.string.dark_mode_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ColorRoleRow(
    role: ThemeColorRole,
    usesSystem: Boolean,
    customColor: Int,
    systemColor: Int,
    onUseSystem: () -> Unit,
    onUseCustom: () -> Unit,
    onPickColor: () -> Unit,
) {
    val shownColor = if (usesSystem) systemColor else customColor
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(role.labelRes()),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (usesSystem) {
                        stringResource(R.string.system_color_value, shownColor.toHex())
                    } else {
                        shownColor.toHex()
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable(enabled = !usesSystem, onClick = onPickColor),
                shape = CircleShape,
                color = Color(shownColor),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {}
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = usesSystem,
                onClick = onUseSystem,
                label = { Text(stringResource(R.string.use_system_color)) },
            )
            FilterChip(
                selected = !usesSystem,
                onClick = onUseCustom,
                label = { Text(stringResource(R.string.use_custom_color)) },
            )
        }
    }
}

private fun ThemeColorRole.labelRes(): Int = when (this) {
    ThemeColorRole.PRIMARY -> R.string.color_primary_label
    ThemeColorRole.ACCENT -> R.string.color_accent_label
    ThemeColorRole.BACKGROUND -> R.string.color_background_label
    ThemeColorRole.TEXT -> R.string.color_text_label
    ThemeColorRole.APP_ICON -> R.string.color_app_icon_label
}

private fun Int.toHex(): String = "#%06X".format(this and 0xFFFFFF)

private fun LazyListScope.fossifyApps(
    titleId: Int,
    apps: List<FossifyApp>,
    launchApp: (packageName: String) -> Unit,
    uninstallApp: (packageName: String) -> Unit,
) {
    if (apps.isNotEmpty()) {
        item(key = titleId) {
            SettingsGroupTitle(
                modifier = Modifier.animateItem(),
                title = {
                    Box(modifier = Modifier.padding(top = SimpleTheme.dimens.padding.large)) {
                        Text(
                            text = stringResource(titleId),
                            color = SimpleTheme.colorScheme.primary,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
            )
        }

        items(
            count = apps.size,
            key = { apps[it].packageName },
        ) {
            FossifyApp(
                app = apps[it],
                launchApp = launchApp,
                uninstallApp = uninstallApp,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
@MyDevices
private fun MainScreenPreview(
    @PreviewParameter(BooleanPreviewParameterProvider::class) ignored: Boolean,
) {
    ThemingThemeSurface(settings = ThemeSettings()) {
        MainScreen(
            allApps = listOf(
                FossifyApp(
                    name = "Fossify Gallery",
                    icon = AppCompatResources.getDrawable(
                        LocalContext.current,
                        R.mipmap.ic_launcher,
                    ),
                    signerName = "Fossify",
                    packageName = "org.fossify.gallery",
                    versionName = "1.0",
                    installerName = "Fossify Store",
                    installerPackage = "org.fossify.store",
                    verified = true,
                )
            ),
            fakeApps = emptyList(),
            settings = ThemeSettings(),
            profiles = emptyList(),
            systemColors = ThemeColorRole.entries.associateWith { 0xFF234B60.toInt() },
            onSettingsChanged = {},
            onApplyNow = {},
            onSaveProfile = {},
            onLoadProfile = {},
            onDeleteProfile = {},
            openSettings = {},
            launchApp = {},
            uninstallApp = {},
        )
    }
}
