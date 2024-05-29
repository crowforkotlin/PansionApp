@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

import allGroupMarks.AllGroupMarksComponent
import allGroupMarks.AllGroupMarksStore
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.BorderStup
import components.CLazyColumn
import components.CustomTextButton
import components.MarkContent
import components.StupsButtons
import components.networkInterface.NetworkState
import decomposeComponents.CAlertDialogContent
import kotlinx.coroutines.CoroutineScope
import lessonReport.LessonReportStore
import report.UserMark
import resources.GeologicaFont
import server.fetchReason
import server.roundTo
import view.LocalViewManager
import view.rememberImeState

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun AllGroupMarksContent(
    component: AllGroupMarksComponent
) {
    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    //PullToRefresh
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    BoxWithConstraints {

        val isFullView by mutableStateOf(this.maxWidth > 600.dp)
        println(isFullView)
        Scaffold(
            Modifier.fillMaxSize(),
//                .nestedScroll(scrollBehavior.nestedScrollConnection)
            topBar = {
                AppBar(
                    navigationRow = {
                        IconButton(
                            onClick = { component.onOutput(AllGroupMarksComponent.Output.BackToHome) }
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew, null
                            )
                        }
                    },
                    title = {
                        val bigTextSize = 20.sp// if (!isLarge) else 40.sp
                        val smallTextSize = 14.sp//if (!isLarge)  else 28.sp

                        Column(
                            Modifier.padding(horizontal = 3.dp)
                        ) {
                            Row {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append(model.subjectName)
                                        }
                                    },
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = bigTextSize,
                                    maxLines = 1,
                                    style = androidx.compose.material3.LocalTextStyle.current.copy(
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Bottom,
                                            trim = LineHeightStyle.Trim.LastLineBottom
                                        )
                                    )
                                )


                            }
                            Text(

                                text = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(model.groupName)
                                    }

                                },
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = androidx.compose.material3.LocalTextStyle.current.copy(
                                    fontSize = smallTextSize,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Top,
                                        trim = LineHeightStyle.Trim.FirstLineTop
                                    )
                                )
                            )

                        }
                    },
                    isHaze = true
                )
                //LessonReportTopBar(component, isFullView) //, scrollBehavior
            }
        ) { padding ->
            Column(Modifier.fillMaxSize()) {
                Crossfade(nModel.state) { state ->
                    when (state) {
                        NetworkState.None -> CLazyColumn(padding = padding) {
                            if(model.students.isNotEmpty()) {
                                items(model.students) { s ->
                                    AllGroupMarksStudentItem(
                                        title = s.shortFIO,
                                        groupId = model.groupId,
                                        marks = s.marks.sortedBy { it.date }.reversed(),
                                        stups = s.stups,
                                        coroutineScope = coroutineScope
                                    ) {
                                        component.onEvent(AllGroupMarksStore.Intent.OpenDetailedStups(s.login))
                                    }
                                }

                            } else {
                                item() {
                                    Text("Никто в этой группе не учится 0_0")
                                }
                            }
                        }

                        NetworkState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        NetworkState.Error -> {
                            Column(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(nModel.error)
                                Spacer(Modifier.height(7.dp))
                                CustomTextButton("Попробовать ещё раз") {
                                    nModel.onFixErrorClick()
                                }
                            }
                        }
                    }
                }
            }


            val detailedStupsStudent =
                model.students.firstOrNull { it.login == model.detailedStupsLogin }

            CAlertDialogContent(
                component = component.stupsDialogComponent,
                title = "Ступени: ${detailedStupsStudent?.shortFIO ?: "null"}",
                titleXOffset = 5.dp
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    model.students.firstOrNull { it.login == model.detailedStupsLogin }?.stups?.forEach {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
                                .padding(horizontal = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(it.date)
                            Text(fetchReason(it.reason))
                            BorderStup(it.content)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllGroupMarksStudentItem(
    title: String,
    groupId: Int,
    marks: List<UserMark>,
    stups: List<UserMark>,
//    stupsCount: Int,
    coroutineScope: CoroutineScope,
    onClick: () -> Unit
) {
//    val isFullView = remember { mutableStateOf(false) }

    val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).padding(top = 5.dp)

    val AVGMarks = marks.filter { it.isGoToAvg }
    val value = (AVGMarks.sumOf { it.content.toInt() }) / (AVGMarks.size).toFloat()

    ElevatedCard(
        Modifier.fillMaxWidth().padding(top = 10.dp)//.padding(horizontal = 10.dp)
            .animateContentSize().clip(CardDefaults.elevatedShape)
    ) {
//            .clickable {
//                isFullView.value = !isFullView.value
//            }) {
        Column(Modifier.padding(5.dp).padding(start = 5.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 25.sp)

                    StupsButtons(
                        stups = stups.map {
                            Pair(it.content.toInt(), it.reason)
                        },
                        { onClick() }, { onClick() }
                    )
                }
                Text(
                    text = if (value.isNaN()) {
                        "NaN"
                    } else {
                        value.roundTo(2).toString()
                    }, fontWeight = FontWeight.Bold, fontSize = 25.sp
                )
            }
//            if (!isFullView.value) {
//                LazyRow(rowModifier, userScrollEnabled = false) {
//                    items(marks) {
//                        cMark(it, coroutineScope = coroutineScope)
//                    }
//                }
//            } else {
            FlowRow(rowModifier) {
                marks.forEach {
                    Box(Modifier.alpha(if (it.groupId != groupId) .2f else 1f)) {
                        cMark(it, coroutineScope = coroutineScope)
                    }
                }
            }
//            }

        }
    }
}


