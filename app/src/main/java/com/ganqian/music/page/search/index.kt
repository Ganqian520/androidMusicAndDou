package com.ganqian.music.page.search

import androidx.lifecycle.viewmodel.compose.viewModel

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ganqian.aaa.StatusBar
import com.ganqian.aaa.aautil
import com.ganqian.compose.R
import com.ganqian.music.api.http
import com.ganqian.music.component.Control
import com.ganqian.music.ui.aliFontFamily
import com.ganqian.music.view.SongList
import com.ganqian.music.viewModel.VmPlayer
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun PageSearch(vm: VmPlayer) {
    val vmSearch: VmSearch = viewModel(factory = VmSearchFactory(vm))
    LaunchedEffect(key1 = Unit, block = {
        vmSearch.lanuch()
    })
    DisposableEffect(key1 = Unit, effect = {
        onDispose { vmSearch.dispose() }
    })
    Column {
        StatusBar()
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)) {
            TopBarSearch(vmSearch)
            when (vmSearch.state) {
                1 -> Content1(vmSearch)
                2 -> Content2(vmSearch)
                3 -> Content3(vm, vmSearch)
            }
        }
        Control(vm = vm)
    }
}

@Composable
fun TopBarSearch(vmSearch: VmSearch) {
    val focusManager = LocalFocusManager.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .width(40.dp)
                .clickable { },
            text = stringResource(id = R.string.back),
            fontFamily = aliFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
        TextField(
            value = vmSearch.keywords,
            onValueChange = {
                vmSearch.keywords = it
                if (it != "") {
                    vmSearch.state = 2
                    vmSearch.getPrepareKeywords()
                } else {
                    vmSearch.state = 1
                }
            },
            modifier = Modifier.padding(0.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    vmSearch.searchNet()
                }
            )
        )
    }
}

//热搜，记录
@Composable
fun Content1(vmSearch: VmSearch) {
    Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "历史",
            fontSize = 14.sp,
            fontWeight = FontWeight.W800
        )
    }
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 10.dp)
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            ),
        mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly,
        crossAxisSpacing = 10.dp,
        mainAxisSpacing = 10.dp
    ) {
        for (v in vmSearch.history) {
            Surface(
                color = Color(0f, 0f, 0f, 0.3f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    v,
                    modifier = Modifier
                        .padding(7.dp)
                        .clickable {
                            vmSearch.keywords = v
                            vmSearch.searchNet()
                        },
                    fontSize = 14.sp
                )
            }
            
        }
    }
    Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "热搜",
            fontSize = 14.sp,
            fontWeight = FontWeight.W800
        )
    }
    FlowRow(
        Modifier.fillMaxWidth(),
        mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly,
    ) {
        for ((i, v) in vmSearch.hots.withIndex()) {
            Row(
                Modifier
                    .height(40.dp)
                    .width(150.dp)
                    .clickable {
                        vmSearch.keywords = vmSearch.hots[i]
                        vmSearch.searchNet()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${i + 1}",
                    modifier = Modifier.width(50.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                
                Text(
                    v,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 14.sp
                )
            }
        }
    }
}

//搜索预览
@Composable
fun Content2(vmSearch: VmSearch) {
    Spacer(modifier = Modifier.height(15.dp))
    LazyColumn(content = {
        items(count = vmSearch.prepares.size) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable {
                        vmSearch.keywords = vmSearch.prepares[it]
                        vmSearch.searchNet()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.search),
                    fontFamily = aliFontFamily,
                    modifier = Modifier.width(50.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
                Text(
                    vmSearch.prepares[it],
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    })
}

//搜索结果
@OptIn(ExperimentalPagerApi::class)
@Composable
fun Content3(vm: VmPlayer, vmSearch: VmSearch) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.clickable { },
            text = stringResource(id = R.string.net),
            fontFamily = aliFontFamily,
        )
        Text(
            modifier = Modifier.clickable { },
            text = stringResource(id = R.string.net),
            fontFamily = aliFontFamily,
        )
        Text(
            modifier = Modifier.clickable { },
            text = stringResource(id = R.string.net),
            fontFamily = aliFontFamily,
        )
    }
    SongList(vm = vm, list = vm.listOther,mode="other")
}