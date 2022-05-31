package com.ganqian.music.page.index

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.ganqian.aaa.*
import com.ganqian.compose.R
import com.ganqian.music.api.http
import com.ganqian.music.api.httpDou
import com.ganqian.music.component.Bg
import com.ganqian.music.component.Control
import com.ganqian.music.data.Song
import com.ganqian.music.ui.aliFontFamily
import com.ganqian.music.util.MusicDrawer
import com.ganqian.music.view.SongList
import com.ganqian.music.view.SongListFresh
import com.ganqian.music.viewModel.VmLogin
import com.ganqian.music.viewModel.VmPlayer
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PageIndex(vm: VmPlayer, vmLogin: VmLogin) {
    val pagerState = rememberPagerState()
    LaunchedEffect(key1 = Unit, block = {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            vm.pageCurrent = page
        }
    })
    Drawer(Left = {
        MusicDrawer(vm,vmLogin)
    }, Middle = {
        Box(Modifier.clip(RoundedCornerShape(20.dp))) {
            Bg(vm = vm)
            Column {
                StatusBar()
                TopBar(vm = vm, pagerState)
                HorizontalPager(
                    count = 2,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    if (page == 0) {
                        Net(vm = vm, vmLogin = vmLogin)
                    } else {
                        Dou(vm,vmLogin)
                    }
                }
                Control(vm = vm)
            }
        }
        
    })
    
    
}

//首页顶部栏
@OptIn(ExperimentalPagerApi::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
@Composable
fun TopBar(vm: VmPlayer, pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .width(50.dp)
                .clickable { },
            text = stringResource(id = R.string.menu),
            textAlign = TextAlign.Center,
            fontFamily = aliFontFamily,
            fontSize = 25.sp
        )
        Row {
            Text(
                modifier = Modifier
                    .width(40.dp)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                            vm.pageCurrent = 0
                        }
                    },
                text = stringResource(id = R.string.net),
                textAlign = TextAlign.Center,
                fontFamily = aliFontFamily,
                fontSize = 20.sp,
                color = if (vm.pageCurrent == 0) Color.White else Color(1f, 1f, 1f, 0.3f)
            )
            Text(
                modifier = Modifier
                    .width(40.dp)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                            vm.pageCurrent = 1
                        }
                    },
                text = stringResource(id = R.string.dou),
                textAlign = TextAlign.Center,
                fontFamily = aliFontFamily,
                fontSize = 20.sp,
                color = if (vm.pageCurrent == 1) Color.White else Color(1f, 1f, 1f, 0.3f)
            )
        }
        Text(
            modifier = Modifier
                .width(50.dp)
                .clickable { vm.navController.navigate("search") },
            text = stringResource(id = R.string.search),
            textAlign = TextAlign.Center,
            fontFamily = aliFontFamily,
            fontSize = 25.sp
        )
    }
}

//网易云
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Net(vm: VmPlayer, vmLogin: VmLogin) {
    Column {
        if (vmLogin.stateLoginNet==3) {
            LaunchedEffect(1) {
                vm.listsNet = http.getSongLists(vmLogin.user.id)
                vm.listNet = http.getSongs(vm.listsNet[vm.indexSongList].id)
//                vm.listNet.forEach {
//                    vm.listWant.add(it)
//                }
            }
            Menu(vm)
            SongListFresh(vm, vm.listNet, mode = "net", suspend {
                vm.listNet = http.getSongs(vm.listsNet[vm.indexSongList].id, true)
            })
        } else {
            LoginByCaptcha(vmLogin)
        }
    }
}

//net菜单
@Composable
fun Menu(vm: VmPlayer) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                "FM",
                modifier = Modifier.clickable {
                    vm.mode = "fm"
                    vm.isShowPlay = true
                    GlobalScope.launch {
                        vm.song = Song()
                        vm.listOther = ArrayList()
                        vm.listOther = http.getFM()
                        vm.start(vm.listOther[0])
                    }
                },
                fontWeight = FontWeight.W600,
                fontSize = 15.sp
            )
            Text(
                "日推",
                modifier = Modifier.clickable {
                    vm.navController.navigate("geDanDetail/dayAdvice")
                },
                fontWeight = FontWeight.W600,
                fontSize = 15.sp
            )
            Text(
                "榜单",
                modifier = Modifier.clickable { vm.navController.navigate("topList") },
                fontWeight = FontWeight.W600,
                fontSize = 15.sp
            )
            Text(
                "歌单",
                modifier = Modifier.clickable { vm.navController.navigate("gedanSquare") },
                fontWeight = FontWeight.W600,
                fontSize = 15.sp
            )
            Text(
                "最近",
                modifier = Modifier.clickable {
                    vm.navController.navigate("geDanDetail/history")
                },
                fontWeight = FontWeight.W600,
                fontSize = 15.sp
            )
        }
        LazyRow {
            items(vm.listsNet.size) {
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .height(100.dp)
                        .clickable {
                            GlobalScope.launch {
                                vm.indexSongList = it
                                vm.listNet = http.getSongs(vm.listsNet[it].id)
                            }
        
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp),
                        painter = rememberImagePainter(
                            data = vm.listsNet[it].img,
                            builder = {
                                transformations(RoundedCornersTransformation(20f))
                            },
                        ),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val fontWeight =
                        if (it == vm.indexSongList) FontWeight.W900 else FontWeight.W400
                    Text(
                        text = vm.listsNet[it].name,
                        maxLines = 1,
                        fontSize = 14.sp,
                        fontWeight = fontWeight
                    )
                }
            }
        }
    }
}

//登录网易云
@Composable
fun LoginByCaptcha(vmLogin: VmLogin) {
    Column(
        modifier = Modifier.width(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("网易云登录")
        TextField(
            value = vmLogin.phoneNet,
            onValueChange = { vmLogin.phoneNet= it },
            placeholder = { Text("手机号：") },
            modifier = Modifier.padding(0.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
            ),
        )
        TextField(
            value = vmLogin.passwordNet,
            onValueChange = { vmLogin.passwordNet= it },
            placeholder = { Text("密码：") },
            modifier = Modifier.padding(0.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
            ),
            visualTransformation = PasswordVisualTransformation('*')
        )
        TextButton(onClick = { vmLogin.loginNet() }) {
            Text(when(vmLogin.stateLoginNet){
                0 -> "登录"
                1 -> "登录中..."
                2 -> "登录失败，请重试"
                else -> ""
            })
        }
    }
}


//抖音
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Dou(vm: VmPlayer,vmLogin: VmLogin) {
    fun filter_(tag: Int,list:ArrayList<Song>): ArrayList<Song> {
        return when (tag) {
            0 -> list
            1 -> list.filter {
                return@filter it.tag == 1
            } as ArrayList<Song>
            else -> list.filter {
                return@filter it.tag == 2
            } as ArrayList<Song>
        }
    }
    if(vmLogin.stateLoginGQ==3){
        LaunchedEffect(key1 = Unit, block = {
            vm.listDouAll = httpDou.getMusics()
            vm.listDou = filter_(vm.douTag,vm.listDouAll)
        })
        Column(
            Modifier.fillMaxSize()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    "全部",
                    modifier = Modifier.clickable { vm.douTag = 0 },
                    fontWeight = if (vm.douTag == 0) FontWeight.W900 else FontWeight.Normal,
                    fontSize = 15.sp,
                )
                Text(
                    "无人声",
                    modifier = Modifier.clickable { vm.douTag = 1 },
                    fontWeight = if (vm.douTag == 1) FontWeight.W900 else FontWeight.Normal,
                    fontSize = 15.sp,
                )
                Text(
                    "有人声",
                    modifier = Modifier.clickable { vm.douTag = 2 },
                    fontWeight = if (vm.douTag == 2) FontWeight.W900 else FontWeight.Normal,
                    fontSize = 15.sp,
                )
            }
            vm.listDou = filter_(vm.douTag,vm.listDouAll)
            SongListFresh(vm = vm, list = vm.listDou, mode = "dou", fresh = {
                vm.listDou = httpDou.getMusics()
            })
        }
    }else{
        LoginGQ(vmLogin = vmLogin)
    }
}
//登录抖音
@Composable
fun LoginGQ(vmLogin: VmLogin){
    Column(Modifier.width((screen.w.p2d-20).dp),horizontalAlignment = Alignment.CenterHorizontally) {
        Text("本系统登录")
        TextField(
            value = vmLogin.phoneGQ,
            onValueChange = { vmLogin.phoneGQ = it },
            placeholder = { Text("手机号：") },
            modifier = Modifier.padding(0.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
            ),
        )
        TextField(
            value = vmLogin.passwordGQ,
            onValueChange = { vmLogin.passwordGQ = it },
            placeholder = { Text("密码：") },
            modifier = Modifier.padding(0.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
            ),
            visualTransformation = PasswordVisualTransformation('*')
        )
        TextButton(onClick = { vmLogin.loginGQ() }) {
            Text(when(vmLogin.stateLoginGQ){
                0 -> "登录"
                1 -> "登录中..."
                2 -> "登录失败，请重试"
                else -> ""
            })
        }
    }
}
