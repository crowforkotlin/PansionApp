package groups

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CLazyColumn
import components.CustomTextButton
import components.LoadingAnimation
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import components.cAlertDialog.CAlertDialogStore
import dev.chrisbanes.haze.hazeChild
import groups.subjects.SubjectsComponent
import groups.subjects.SubjectsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import view.LocalViewManager

@ExperimentalFoundationApi
@Composable
fun SubjectsContent(
    component: SubjectsComponent,
    coroutineScope: CoroutineScope,
    topPadding: Dp,
    isFabShowing: MutableState<Boolean>
) {
    val gModel = component.groupModel.subscribeAsState().value
    val model = component.model.subscribeAsState().value
    val nSModel = component.nSubjectsInterface.networkModel.subscribeAsState().value
    val viewManager = LocalViewManager.current
    Box() {
//    Spacer(Modifier.height(10.dp))
        Crossfade(nSModel.state) {
            when {
                it == NetworkState.Loading && model.groups.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        isFabShowing.value = false
                        LoadingAnimation()
                    }
                }

                it == NetworkState.Error -> {
                    DefaultGroupsErrorScreen(
                        isFabShowing,
                        component.nSubjectsInterface
                    )
                }

                else -> {
                    if (model.groups.isNotEmpty()) {
                        Spacer(Modifier.height(7.dp))
                        CLazyColumn(padding = PaddingValues(top = topPadding + 45.dp)) {
                            isFabShowing.value = true
                            items(model.groups) { group ->
                                val mentor =
                                    gModel.teachers.find { it.login == group.group.teacherLogin }
                                val mentorName =
                                    try {
                                        "${mentor!!.fio.surname} ${mentor.fio.name.first()}. ${(mentor.fio.praname ?: " ").first()}."
                                    } catch (_: Throwable) {
                                        ""
                                    }
                                ElevatedCard(
                                    Modifier.heightIn(TextFieldDefaults.MinHeight)
                                        .fillMaxWidth().padding(horizontal = 10.dp)
                                        .padding(bottom = 5.dp)
                                ) {
                                    Column(
                                        Modifier.padding(horizontal = 10.dp)
                                            .padding(bottom = 10.dp, top = 5.dp)
                                    ) {
                                        Text(
                                            group.group.name,
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(start = 5.dp),
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Row {
                                            Icon(
                                                Icons.Rounded.Person,
                                                null
                                            )
                                            Text(text = mentorName)

                                            Icon(
                                                Icons.Rounded.LocalFireDepartment,
                                                null
                                            )
                                            Text(group.group.difficult)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (gModel.subjects.isNotEmpty()) {
                                isFabShowing.value = true
                            }
                            Text("Здесь пустовато =)")
                        }
                    }
                }
            }
        }

        LazyRow(
            Modifier.fillMaxWidth()
                .then(
                    if(viewManager.hazeState != null && viewManager.hazeStyle != null) Modifier.hazeChild(state = viewManager.hazeState!!.value, style = viewManager.hazeStyle!!.value)
                    else Modifier
                )
                .padding(top = topPadding).padding(horizontal = 10.dp)
                .height(35.dp),
//            verticalAlignment = Alignment.CenterVertically
        ) {
            item {

                FilledTonalIconButton(
                    onClick = {
                        component.cSubjectDialog.onEvent(CAlertDialogStore.Intent.ShowDialog)
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            2.dp
                        )
                    ),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        null
                    )
                }

                Spacer(Modifier.width(5.dp))
            }
            items(gModel.subjects.filter { it.isActive }
                .reversed()) {
                val bringIntoViewRequester = BringIntoViewRequester()
                Box(
                    Modifier.bringIntoViewRequester(
                        bringIntoViewRequester
                    ).height(30.dp)
                ) {
                    SubjectItem(
                        title = it.name,
                        isChosen = it.id == model.chosenSubjectId
                    ) {
                        component.onEvent(SubjectsStore.Intent.ClickOnSubject(it.id))
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
                Spacer(Modifier.width(5.dp))
            }
        }
    }

}


@Composable
fun DefaultGroupsErrorScreen(
    isFabShowing: MutableState<Boolean>,
    nInterface: NetworkInterface
) {
//    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        isFabShowing.value = false
        Text(nInterface.networkModel.value.error)
        Spacer(Modifier.height(7.dp))
        CustomTextButton("Попробовать ещё раз") {
            nInterface.fixError()

        }
    }
}