// shared/src/commonMain/kotlin/com/heklast/smartspender/navigation/BottomBar.kt
package com.heklast.smartspender.navigation

import androidx.compose.foundation.Image

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.smartspender.project.core.AppColors
import smartspender.composeapp.generated.resources.Res
import smartspender.composeapp.generated.resources.darkgreen_logo
import smartspender.composeapp.generated.resources.home
import smartspender.composeapp.generated.resources.profile
import smartspender.composeapp.generated.resources.analysis
import smartspender.composeapp.generated.resources.add

data class BottomItem(
    val route: Route,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun BottomBar(
    current: Route,
    onNavigate: (Route) -> Unit
) {
    val items = listOf(
        BottomItem(Route.ExpensesList, "Home", {  Image(
            painter = painterResource(Res.drawable.home),
            contentDescription = null
        ) }),
        BottomItem(Route.AddExpense, "Add expense", {  Image(
            painter = painterResource(Res.drawable.add),
            contentDescription = null
        ) }),
        BottomItem(Route.Statistics, "Analysis", {  Image(
            painter = painterResource(Res.drawable.analysis),
            contentDescription = null
        ) }),
        BottomItem(Route.Profile, "Profile", {  Image(
            painter = painterResource(Res.drawable.profile),
            contentDescription = null
        ) }),
        BottomItem(Route.About, "About", {  Image(
            painter = painterResource(Res.drawable.profile),
            contentDescription = null
        ) }),
    )

    NavigationBar(
        containerColor = AppColors.lightGreen
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = current == item.route,
                onClick = { if (current != item.route) onNavigate(item.route) },
                icon = item.icon,
                label = { Text(item.label) }
            )
        }
    }
}