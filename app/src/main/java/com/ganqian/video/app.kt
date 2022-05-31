package com.ganqian.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ganqian.aaa.Bg
import com.ganqian.aaa.mlog
import com.ganqian.compose.R
import com.ganqian.video.api.http
import com.ganqian.video.page.editTag.ScreenEditTag
import com.ganqian.video.page.index.ScreenIndex
import com.ganqian.video.page.play.ScreenPlay
import com.ganqian.video.util.randomBush

@Composable
fun App(vm: Vm) {
    vm.navController = rememberNavController()
    Theme {
        Box{
            Bg()
            NavHost(navController = vm.navController, startDestination = "index") {
                composable("index", content = { ScreenIndex(vm) })
                composable("editTag", content = { ScreenEditTag(vm) })
                composable("play/{index}", content = {
                    val index = it.arguments!!.getInt("index")
                    ScreenPlay(index, vm)
                }, arguments = listOf(navArgument("index") { type = NavType.IntType }))
            }
        }
    }
}
