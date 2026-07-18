package com.albertolicea00.myussdcodes.ui

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.albertolicea00.myussdcodes.AppViewModel
import com.albertolicea00.myussdcodes.R
import com.albertolicea00.myussdcodes.ui.screens.AllCodesScreen
import com.albertolicea00.myussdcodes.ui.screens.CodeEditorScreen
import com.albertolicea00.myussdcodes.ui.screens.SectionDetailScreen
import com.albertolicea00.myussdcodes.ui.screens.SectionsScreen
import com.albertolicea00.myussdcodes.ui.screens.SettingsScreen

object Routes {
    const val SECTIONS = "sections"
    const val ALL_CODES = "all"
    const val SETTINGS = "settings"
    const val SECTION_DETAIL = "section/{type}/{key}"
    const val EDITOR = "editor?codeId={codeId}"

    fun sectionDetail(type: String, key: String) = "section/$type/${Uri.encode(key)}"
    fun editor(codeId: String? = null) = if (codeId == null) "editor" else "editor?codeId=$codeId"
}

private data class Tab(val route: String, val icon: ImageVector, val labelRes: Int)

@Composable
fun AppRoot(viewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = listOf(
        Tab(Routes.SECTIONS, Icons.Outlined.Apps, R.string.tab_sections),
        Tab(Routes.ALL_CODES, Icons.Outlined.Search, R.string.tab_all_codes),
        Tab(Routes.SETTINGS, Icons.Outlined.Settings, R.string.tab_settings)
    )
    val isTabRoute = tabs.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (isTabRoute) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == Routes.SECTIONS || currentRoute == Routes.ALL_CODES) {
                FloatingActionButton(onClick = { navController.navigate(Routes.editor()) }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_code))
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SECTIONS,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.SECTIONS) {
                SectionsScreen(
                    viewModel = viewModel,
                    onOpenSection = { type, key ->
                        navController.navigate(Routes.sectionDetail(type, key))
                    }
                )
            }
            composable(Routes.ALL_CODES) {
                AllCodesScreen(
                    viewModel = viewModel,
                    onEdit = { navController.navigate(Routes.editor(it.id)) }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(viewModel = viewModel)
            }
            composable(Routes.SECTION_DETAIL) { entry ->
                SectionDetailScreen(
                    viewModel = viewModel,
                    type = entry.arguments?.getString("type").orEmpty(),
                    key = entry.arguments?.getString("key").orEmpty(),
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.editor(it.id)) }
                )
            }
            composable(
                route = Routes.EDITOR,
                arguments = listOf(
                    navArgument("codeId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                CodeEditorScreen(
                    viewModel = viewModel,
                    codeId = entry.arguments?.getString("codeId"),
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}
