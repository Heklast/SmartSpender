// shared/src/commonMain/kotlin/com/heklast/smartspender/navigation/BottomBar.kt
package com.heklast.smartspender.navigation

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.smartspender.project.core.AppColors
import smartspender.composeapp.generated.resources.Res
import smartspender.composeapp.generated.resources.add
import smartspender.composeapp.generated.resources.analysis
import smartspender.composeapp.generated.resources.home
import smartspender.composeapp.generated.resources.profile
import com.heklast.smartspender.responsive.rememberWindowSize
import com.heklast.smartspender.responsive.WidthClass

data class BottomItem(
    val route: Route,
    val label: String,
    val iconRes: DrawableResource
)

@Composable
fun BottomBar(
    current: Route,
    onNavigate: (Route) -> Unit
) {
    val win = rememberWindowSize()

    val iconSize = when (win.width) {
        WidthClass.Compact  -> 24.dp
        WidthClass.Medium   -> 28.dp
        WidthClass.Expanded -> 32.dp
    }
    val fontSize = when (win.width) {
        WidthClass.Compact  -> 10.sp
        WidthClass.Medium   -> 12.sp
        WidthClass.Expanded -> 14.sp
    }

    val items = listOf(
        BottomItem(Route.ExpensesList, "Home",     Res.drawable.home),
        BottomItem(Route.AddExpense,   "Add",      Res.drawable.add),
        BottomItem(Route.Statistics,   "Analysis", Res.drawable.analysis),
        BottomItem(Route.Profile,      "Profile",  Res.drawable.profile),
        BottomItem(Route.About,        "About",    Res.drawable.profile),
    )

    if (win.width == WidthClass.Expanded) {
        // Optional rail for tablets
        NavigationRail(
            containerColor = AppColors.lightGreen,
            contentColor = AppColors.black,
            modifier = Modifier.width(92.dp)
        ) {
            items.forEach { item ->
                NavigationRailItem(
                    selected = current == item.route,
                    onClick = { if (current != item.route) onNavigate(item.route) },
                    icon = {
                        Icon(
                            painter = painterResource(item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(iconSize),
                            tint = AppColors.black
                        )
                    },
                    label = { Text(item.label, fontSize = fontSize) }
                )
            }
        }
    } else {
        // IMPORTANT: don't force the bar shorter than ~80.dp
        NavigationBar(
            containerColor = AppColors.lightGreen,
            modifier = Modifier.defaultMinSize(minHeight = 80.dp) // or just remove the modifier
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = current == item.route,
                    onClick = { if (current != item.route) onNavigate(item.route) },
                    icon = {
                        Icon(
                            painter = painterResource(item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(iconSize),
                            tint = AppColors.black
                        )
                    },
                    label = { Text(item.label, fontSize = fontSize, color = AppColors.black) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppColors.black,
                        selectedTextColor = AppColors.black,
                        unselectedIconColor = AppColors.black.copy(alpha = 0.7f),
                        unselectedTextColor = AppColors.black.copy(alpha = 0.7f),
                        indicatorColor = AppColors.mint.copy(alpha = 0.25f)
                    )
                )
            }
        }
    }
}