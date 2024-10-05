@file:OptIn(ExperimentalFoundationApi::class)

import admin.groups.forms.CutedForm
import admin.users.User
import admin.users.UserInit
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AnimatedCommonButton
import components.AppBar
import components.CustomTextButton
import components.CustomTextField
import components.LoadingAnimation
import components.networkInterface.NetworkState
import components.ScrollBaredBox
import components.cBottomSheet.CBottomSheetStore
import decomposeComponents.CAlertDialogContent
import decomposeComponents.CBottomSheetContent
import decomposeComponents.listDialogComponent.customConnection
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import pullRefresh.PullRefreshIndicator
import pullRefresh.pullRefresh
import pullRefresh.rememberPullRefreshState
import server.Moderation
import server.Roles
import server.twoNums
import users.UsersComponent
import users.UsersStore
import view.LocalViewManager
import view.LockScreenOrientation
import view.rememberImeState

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
)
@ExperimentalLayoutApi
@Composable
fun UsersContent(
    component: UsersComponent
) {
    LockScreenOrientation(-1)
    val model by component.model.subscribeAsState()
    val nModel by component.nModel.subscribeAsState()
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    val refreshState = rememberPullRefreshState(
        (nModel.state == NetworkState.Loading) && model.users != null,
        { component.onEvent(UsersStore.Intent.FetchUsers) })

    Scaffold(

        modifier = Modifier.fillMaxSize().onKeyEvent {
            if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
                component.onEvent(UsersStore.Intent.FetchUsers)
            }
            false
        },
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(UsersComponent.Output.Back) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(
                            rememberScrollState()
                        )
                    ) {
                        Text(
                            "Пользователи",

                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Checkbox(
                            checked = model.fTeachers,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.FTeachers(it)
                                )
                            }
                        )
                        Text("Учителя")
                        Checkbox(
                            checked = model.fStudents,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.FStudents(it)
                                )
                            }
                        )
                        Text("Ученики")
                        Checkbox(
                            checked = model.fOther,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.FOther(it)
                                )
                            }
                        )
                        Text("Другое")
                        Checkbox(
                            checked = model.fParents,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.FParents(it)
                                )
                            }
                        )
                        Text("Родители")
                        Checkbox(
                            checked = model.fNoAdmin,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.FNoAdmin(it)
                                )
                            }
                        )
                        Text("Не админ")
                        Checkbox(
                            checked = model.fInActive,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.FInActive(it)
                                )
                            }
                        )
                        Text("Inactive")
                    }
                },
                actionRow = {
                    if (model.users != null) {
                        IconButton(
                            onClick = { component.onEvent(UsersStore.Intent.FetchUsers) }
                        ) {
                            Icon(
                                Icons.Filled.Refresh, null
                            )
                        }
                        IconButton(
                            onClick = {
                                component.cUserBottomSheet.onEvent(CBottomSheetStore.Intent.ShowSheet)
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Add, null
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->


        val columnNames = listOf(
            "Отчество", //0
            "Логин", //1
            "Пароль",  //2
            "Дата рождения", //3
            "Роль",  //4
            "Модерация",  //5
            "Родитель", //6
        )

        val widthsInit = remember {
            mapOf(
                columnNames[0] to 200.dp,
                columnNames[1] to 160.dp,
                columnNames[2] to 90.dp,
                columnNames[3] to 170.dp,
                columnNames[4] to 90.dp,
                columnNames[5] to 140.dp,
                columnNames[6] to 120.dp
            )
        }

        val roles = listOfNotNull(
            if (model.fOther) Roles.nothing else null,
            if (model.fTeachers) Roles.teacher else null,
            if (model.fStudents) Roles.student else null
        )

        Box(Modifier.fillMaxSize().padding(padding)) {
            Crossfade(targetState = nModel) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        model.users != null -> {
                            val users = model.users!!.filter {
                                val isInActive =
                                    if (!model.fInActive) it.user.fio.surname[0] != '.' else true
                                val moder =
                                    if (!model.fNoAdmin) it.user.moderation != Roles.nothing else true
                                val parent =
                                    if (!model.fParents) !it.user.isParent else true
                                it.user.role in roles
                                        && moder
                                        && isInActive
                                        && parent
                            }
                            TableScreen(
                                columnNames,
                                widthsInit = widthsInit,
                                users.map {
                                    Pair(
                                        "${if (it.isActive) "" else "."}${it.user.fio.surname} ${it.user.fio.name}",
                                        mapOf(
                                            columnNames[0] to (it.user.fio.praname ?: "--"),
                                            columnNames[1] to it.login,
                                            columnNames[2] to if (it.isProtected) "есть" else "нет",
                                            columnNames[3] to try {
                                                it.user.birthday!!.substring(0, 2) +
                                                        "." + it.user.birthday!!.substring(2, 4) +
                                                        "." + it.user.birthday!!.substring(4)
                                            } catch (_: Throwable) {
                                                "null"
                                            },
                                            columnNames[4] to when (it.user.role) {
                                                Roles.teacher -> "учитель"
                                                Roles.student -> "ученик"
                                                else -> "другое"
                                            },
                                            columnNames[5] to it.user.moderation,
                                            columnNames[6] to if (it.user.isParent) "да" else "нет"
                                        )
                                    )

                                },
                                true,
                                onEditClick = {
//password = model.users!![it].password,
                                    val userForEditing = User(
                                        login = users[it].login,
                                        user = UserInit(
                                            fio = FIO(
                                                name = users[it].user.fio.name,
                                                surname = users[it].user.fio.surname,
                                                praname = users[it].user.fio.praname
                                            ),
                                            birthday = users[it].user.birthday,
                                            role = users[it].user.role,
                                            moderation = users[it].user.moderation,
                                            isParent = users[it].user.isParent
                                        ),
                                        avatarId = users[it].avatarId,
                                        isProtected = users[it].isProtected,
                                        isActive = users[it].isActive
                                    )
                                    component.onEvent(
                                        UsersStore.Intent.OpenEditingSheet(
                                            userForEditing
                                        )
                                    )

                                },
                                modifier = Modifier.padding(8.dp).pullRefresh(refreshState)
                            )
                        }

                        else -> {
                            when (it.state) {
                                NetworkState.Loading -> {
                                    LoadingAnimation()
                                }

                                else -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            it.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(5.dp))
                                        if (it.error != "Доступ запрещён") {
                                            CustomTextButton("Попробовать ещё раз") {
                                                component.onEvent(UsersStore.Intent.FetchUsersInit)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

            }

            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = nModel.state == NetworkState.Loading && model.users != null,
                state = refreshState,
            )
            editUserSheet(
                component,
                model
            )
            createUserSheet(
                component,
                model
            )

        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
private fun editUserSheet(
    component: UsersComponent,
    model: UsersStore.State
) {


    val eNModel = component.eUserBottomSheet.nModel.subscribeAsState()
    val isEditingInProcess = (eNModel.value.state == NetworkState.Loading)
    val isActive = model.users?.firstOrNull { it.login == model.eLogin }?.isActive != false

//                LaunchedEffect(modalBottomSheetState.isVisible) {
//                    if (modalBottomSheetState.isVisible) {
//                        modalBottomSheetState.expand()
//                    }
//                }
    CBottomSheetContent(
        component = component.eUserBottomSheet,
        customLoadingScreen = true,
        customMaxHeight = 0.dp
    ) {
        val focusManager = LocalFocusManager.current
        val lazyList = rememberLazyListState()
//                    Surface(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .wrapContentHeight(),
//                        shape = MaterialTheme.shapes.large
//                    ) {
        var num = 0
        Column(
            Modifier.fillMaxWidth()
                .alpha(if (eNModel.value.state == NetworkState.Error) 0.4f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expandedRoles by remember { mutableStateOf(false) }

            Text(
                model.eLogin, fontWeight = FontWeight.Black,
                fontSize = 27.sp, textAlign = TextAlign.Center
            )
            val properties = listOf(
                model.eName,
                model.eSurname,
                model.ePraname,
                model.eRole,
            )
            num = properties.count { (it ?: "").isNotBlank() }
            if (model.eBirthday.length == 8) num++
            Text(
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    ) {
                        append("Редактировать пользователя ")
                    }
                    withStyle(
                        SpanStyle(
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("$num/5")
                    }
                }
            )
            Spacer(Modifier.height(5.dp))
            LazyColumn(
                Modifier.imePadding().fillMaxWidth()
                    .nestedScroll(lazyList.customConnection),
                state = lazyList,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {


                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = model.eSurname,
                        onValueChange = {
                            component.onEvent(UsersStore.Intent.ChangeESurname(it))
                        },
                        text = "Фамилия",
                        isEnabled = !isEditingInProcess,
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)

                        },
                        focusManager = focusManager,
                        isMoveUpLocked = true,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = model.eName,
                        onValueChange = {
                            component.onEvent(UsersStore.Intent.ChangeEName(it))
                        },
                        text = "Имя",
                        isEnabled = !isEditingInProcess,
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = false,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = model.ePraname ?: "",
                        onValueChange = {
                            component.onEvent(UsersStore.Intent.ChangeEPraname(it))
                        },
                        text = "Отчество",
                        isEnabled = !isEditingInProcess,
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = false,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(Modifier.height(7.dp))
                    CustomTextField(
                        value = model.eBirthday,
                        onValueChange = {
                            if (it.length <= 8 && (it.matches("\\d{1,8}".toRegex()) || it.isEmpty())) {
                                component.onEvent(UsersStore.Intent.ChangeEBirthday(it))
                            }
                        },
                        text = "Дата рождения",
                        isEnabled = !isEditingInProcess,
                        onEnterClicked = {
                            focusManager.moveFocus(FocusDirection.Down)
                            expandedRoles = true
                        },
                        focusManager = focusManager,
                        isMoveUpLocked = false,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                        supText = "ДД.ММ.ГГГГ",
                        isDateEntry = true,
                        trailingIcon = {
                            IconButton(
                                {
                                    component.onEvent(
                                        UsersStore.Intent.ChangeDateDialogShowing(
                                            true
                                        )
                                    )
                                },
                                enabled = !isEditingInProcess
                            ) {
                                Icon(
                                    Icons.Rounded.CalendarToday,
                                    null
                                )
                            }
                        }
                    )
                    if (model.isDateDialogShowing) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis =
                            if (model.eBirthday.length == 8) {
                                val day = model.eBirthday.substring(0, 2).toInt()
                                val month = model.eBirthday.substring(2, 4).toInt()
                                val year = model.eBirthday.substring(4).toInt()
                                LocalDate(
                                    year = year,
                                    monthNumber = month,
                                    dayOfMonth = day
                                ).atStartOfDayIn(
                                    TimeZone.UTC
                                ).toEpochMilliseconds()
                            } else null,
                            yearRange = IntRange(1940, model.currentYear - 5)
                        )
                        DatePickerDialog(
                            onDismissRequest = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeDateDialogShowing(
                                        false
                                    )
                                )
                            },
                            confirmButton = {
                                CustomTextButton(
                                    "Ок",
                                    modifier = Modifier.padding(
                                        end = 30.dp,
                                        start = 20.dp,
                                        bottom = 10.dp
                                    ),
                                    color = if (datePickerState.selectedDateMillis != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.3f
                                    )
                                ) {
                                    if (datePickerState.selectedDateMillis != null) {
                                        val date =
                                            Instant.fromEpochMilliseconds(
                                                datePickerState.selectedDateMillis!!
                                            )
                                                .toLocalDateTime(TimeZone.of("UTC+3"))
                                        component.onEvent(
                                            UsersStore.Intent.ChangeEBirthday(
                                                "${date.dayOfMonth.twoNums()}${date.monthNumber.twoNums()}${date.year}"
                                            )
                                        )
                                        component.onEvent(
                                            UsersStore.Intent.ChangeDateDialogShowing(
                                                false
                                            )
                                        )
                                    }
                                }
                            },
                            dismissButton = {
                                CustomTextButton(
                                    "Отмена",
                                    modifier = Modifier.padding(bottom = 10.dp)
                                ) {
                                    component.onEvent(
                                        UsersStore.Intent.ChangeDateDialogShowing(
                                            false
                                        )
                                    )
                                }
                            }
                        ) {
                            DatePicker(
                                state = datePickerState,
                                showModeToggle = false,
                                title = {
                                    Text(
                                        "Выберите дату",
                                        modifier = Modifier.padding(
                                            top = 15.dp,
                                            start = 20.dp
                                        )
                                    )
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(7.dp))

                    val rolesList = mapOf(
                        Roles.student to "Ученик",
                        Roles.teacher to "Учитель",
                        Roles.nothing to "Другое"
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedRoles,
                        onExpandedChange = {
                            expandedRoles = !expandedRoles
                        }
                    ) {
                        // textfield
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
                            readOnly = true,
                            value = when (model.eRole) {
                                Roles.teacher -> "Учитель"
                                Roles.student -> "Ученик"
                                Roles.nothing -> "Другое"
                                else -> ""
                            },
                            placeholder = { Text("Выберите") },
                            onValueChange = {},
                            label = { Text("Роль") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandedRoles
                                )
                            },
                            shape = RoundedCornerShape(15.dp),
                            enabled = !isEditingInProcess
                        )
                        // menu
                        ExposedDropdownMenu(
                            expanded = expandedRoles,
                            onDismissRequest = {
                                expandedRoles = false
                            },
                        ) {
                            // menu items
                            rolesList.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption.value) },
                                    onClick = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeERole(
                                                selectionOption.key
                                            )
                                        )
                                        expandedRoles = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                    Row(
                        Modifier.width(TextFieldDefaults.MinWidth)
                            .padding(horizontal = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Модератор",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Switch(
                            checked = model.eIsModerator,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeEIsModerator(
                                        it
                                    )
                                )
                            },
                            enabled = !isEditingInProcess
                        )
                    }
                    Spacer(Modifier.height(7.dp))
                    Row(
                        Modifier.width(TextFieldDefaults.MinWidth)
                            .padding(horizontal = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Наставник",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Switch(
                            checked = model.eIsMentor,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeEIsMentor(
                                        it
                                    )
                                )
                            },
                            enabled = !isEditingInProcess
                        )
                    }
                    Spacer(Modifier.height(7.dp))
                    Row(
                        Modifier.width(TextFieldDefaults.MinWidth)
                            .padding(horizontal = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Родитель",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Switch(
                            checked = model.eIsParent,
                            onCheckedChange = {
                                component.onEvent(
                                    UsersStore.Intent.ChangeEIsParent(
                                        it
                                    )
                                )
                            },
                            enabled = !isEditingInProcess
                        )
                    }
                    Spacer(Modifier.height(7.dp))

                    if (isActive) {
                        AnimatedVisibility(
                            !model.eIsMentor && !model.eIsParent && !model.eIsModerator
                        ) {
                            CustomTextButton(if (model.eRole == Roles.student) "Отчислить" else "Удалить") {
                                component.onEvent(UsersStore.Intent.DeleteAccountInit(model.eLogin))
                            }
                            Spacer(Modifier.height(7.dp))
                        }
                    } else {
                        CustomTextButton("Восстановить") {
                            component.onEvent(UsersStore.Intent.EditUser)
                        }
                        Spacer(Modifier.height(7.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        if (model.eIsPassword) {
                            CustomTextButton(
                                "Сбросить пароль",
                                color = if (!isEditingInProcess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.3f
                                )
                            ) {
                                component.onEvent(UsersStore.Intent.ClearPassword)
                            }
                            if (isActive) {
                                Spacer(Modifier.width(12.dp))
                            }
                        }
                        if (isActive) {
                            AnimatedCommonButton(
                                text = "Редактировать",
                                isEnabled = num == 5,
                                modifier = if (!model.eIsPassword) Modifier.width(
                                    TextFieldDefaults.MinWidth
                                ) else Modifier
                            ) {
                                component.onEvent(UsersStore.Intent.EditUser)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                }
                item { Spacer(Modifier.height(height = heightVal.dp/2)) }
            }


//            if (model.eError.isNotBlank()) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(model.eError)
//                    Spacer(Modifier.height(7.dp))
//                    CustomTextButton("Попробовать ещё раз") {
//                        component.onEvent(UsersStore.Intent.TryEditUserAgain)
//                    }
//                }
//            }
        }
    }
    CAlertDialogContent(
        component = component.eDeleteDialog,
        isCustomButtons = false,
        title = "Удалить ${model.eDeletingLogin}?"
    ) {
        val deleteNModel by component.eDeleteDialog.nModel.subscribeAsState()
        Crossfade(deleteNModel.state) {
            when (it) {
                NetworkState.Error -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(deleteNModel.error)
                    Spacer(Modifier.height(7.dp))
                    CustomTextButton("Попробовать ещё раз") {
                        deleteNModel.onFixErrorClick()
                    }
                }

                NetworkState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                else -> {}
            }
        }
    }
}


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class
)
@Composable
private fun createUserSheet(
    component: UsersComponent,
    model: UsersStore.State
) {
    val cNModel = component.cUserBottomSheet.nModel.subscribeAsState()
    val isCreatingInProcess = (cNModel.value.state == NetworkState.Loading)
    val lazyList = rememberLazyListState()
    CBottomSheetContent(
        component = component.cUserBottomSheet,
        customLoadingScreen = true,
        customMaxHeight = 0.dp
    ) {
        val focusManager = LocalFocusManager.current
        var num = 0
        if (model.cLogin.isBlank() && cNModel.value.state != NetworkState.Error) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var expandedRoles by remember { mutableStateOf(false) }
                val properties = listOf(
                    model.cName,
                    model.cSurname,
                    model.cPraname,
                    model.cRole,
                )
                num = properties.count { (it ?: "").isNotBlank() }
                if (model.cBirthday.length == 8) num++
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        ) {
                            append("Создать нового пользователя ")
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("$num/5")
                        }
                    }
                )
                Spacer(Modifier.height(5.dp))
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .nestedScroll(lazyList.customConnection),
                    state = lazyList,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {


                        Spacer(Modifier.height(7.dp))
                        CustomTextField(
                            value = model.cSurname,
                            onValueChange = {
                                component.onEvent(UsersStore.Intent.ChangeCSurname(it))
                            },
                            text = "Фамилия",
                            isEnabled = !isCreatingInProcess,//model.isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Next)
                            },
                            focusManager = focusManager,
                            isMoveUpLocked = true,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password
                        )
                        Spacer(Modifier.height(7.dp))
                        CustomTextField(
                            value = model.cName,
                            onValueChange = {
                                component.onEvent(UsersStore.Intent.ChangeCName(it))
                            },
                            text = "Имя",
                            isEnabled = !isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Next)
                            },
                            focusManager = focusManager,
                            isMoveUpLocked = false,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password
                        )
                        Spacer(Modifier.height(7.dp))
                        CustomTextField(
                            value = model.cPraname ?: "",
                            onValueChange = {
                                component.onEvent(UsersStore.Intent.ChangeCPraname(it))
                            },
                            text = "Отчество",
                            isEnabled = !isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Next)
                            },
                            focusManager = focusManager,
                            isMoveUpLocked = false,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password
                        )
                        Spacer(Modifier.height(7.dp))
                        CustomTextField(
                            value = model.cBirthday,
                            onValueChange = {
                                if (it.length <= 8 && (it.matches("\\d{1,8}".toRegex()) || it.isEmpty())) {
                                    component.onEvent(UsersStore.Intent.ChangeCBirthday(it))
                                }
                            },
                            text = "Дата рождения",
                            isEnabled = !isCreatingInProcess,
                            onEnterClicked = {
                                focusManager.moveFocus(FocusDirection.Down)
                                expandedRoles = true
                            },
                            focusManager = focusManager,
                            isMoveUpLocked = false,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Number,
                            supText = "ДД.ММ.ГГГГ",
                            isDateEntry = true,
                            trailingIcon = {
                                IconButton(
                                    {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeDateDialogShowing(
                                                true
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                ) {
                                    Icon(
                                        Icons.Rounded.CalendarToday,
                                        null
                                    )
                                }
                            }
                        )
                        if (model.isDateDialogShowing) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis =
                                if (model.cBirthday.length == 8) {
                                    val day = model.cBirthday.substring(0, 2).toInt()
                                    val month = model.cBirthday.substring(2, 4).toInt()
                                    val year = model.cBirthday.substring(4).toInt()
                                    LocalDate(
                                        year = year,
                                        monthNumber = month,
                                        dayOfMonth = day
                                    ).atStartOfDayIn(
                                        TimeZone.UTC
                                    ).toEpochMilliseconds()
                                } else null,
                                yearRange = IntRange(1940, model.currentYear - 5)
                            )
                            DatePickerDialog(
                                onDismissRequest = {
                                    component.onEvent(
                                        UsersStore.Intent.ChangeDateDialogShowing(
                                            false
                                        )
                                    )
                                },
                                confirmButton = {
                                    CustomTextButton(
                                        "Ок",
                                        modifier = Modifier.padding(
                                            end = 30.dp,
                                            start = 20.dp,
                                            bottom = 10.dp
                                        ),
                                        color = if (datePickerState.selectedDateMillis != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.3f
                                        )
                                    ) {
                                        if (datePickerState.selectedDateMillis != null) {
                                            val date =
                                                Instant.fromEpochMilliseconds(
                                                    datePickerState.selectedDateMillis!!
                                                )
                                                    .toLocalDateTime(TimeZone.of("UTC+3"))
                                            component.onEvent(
                                                UsersStore.Intent.ChangeCBirthday(
                                                    "${date.dayOfMonth.twoNums()}${date.monthNumber.twoNums()}${date.year}"
                                                )
                                            )
                                            component.onEvent(
                                                UsersStore.Intent.ChangeDateDialogShowing(
                                                    false
                                                )
                                            )
                                        }
                                    }
                                },
                                dismissButton = {
                                    CustomTextButton(
                                        "Отмена",
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    ) {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeDateDialogShowing(
                                                false
                                            )
                                        )
                                    }
                                }
                            ) {
                                DatePicker(
                                    state = datePickerState,
                                    showModeToggle = false,
                                    title = {
                                        Text(
                                            "Выберите дату",
                                            modifier = Modifier.padding(
                                                top = 15.dp,
                                                start = 20.dp
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.height(7.dp))

                        val rolesList = mapOf(
                            Roles.student to "Ученик",
                            Roles.teacher to "Учитель",
                            Roles.nothing to "Другое"
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedRoles,
                            onExpandedChange = {
                                expandedRoles = !expandedRoles
                            }
                        ) {
                            // textfield
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
                                readOnly = true,
                                value = when (model.cRole) {
                                    Roles.teacher -> "Учитель"
                                    Roles.student -> "Ученик"
                                    Roles.nothing -> "Другое"
                                    else -> ""
                                },
                                placeholder = { Text("Выберите") },
                                onValueChange = {},
                                label = { Text("Роль") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedRoles
                                    )
                                },
                                shape = RoundedCornerShape(15.dp),
                                enabled = !isCreatingInProcess
                            )
                            // menu
                            ExposedDropdownMenu(
                                expanded = expandedRoles,
                                onDismissRequest = {
                                    expandedRoles = false
                                },
                            ) {
                                // menu items
                                rolesList.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption.value) },
                                        onClick = {
                                            component.onEvent(
                                                UsersStore.Intent.ChangeCRole(
                                                    selectionOption.key
                                                )
                                            )
                                            expandedRoles = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(7.dp))
                        if (model.cRole != Roles.student) {
                            Row(
                                Modifier.width(TextFieldDefaults.MinWidth)
                                    .padding(horizontal = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Модератор",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Switch(
                                    checked = model.cIsModerator,
                                    onCheckedChange = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeCIsModerator(
                                                it
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                )
                            }
                            Spacer(Modifier.height(7.dp))
                            Row(
                                Modifier.width(TextFieldDefaults.MinWidth)
                                    .padding(horizontal = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Наставник",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Switch(
                                    checked = model.cIsMentor,
                                    onCheckedChange = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeCIsMentor(
                                                it
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                )
                            }
                            Spacer(Modifier.height(7.dp))
                            Row(
                                Modifier.width(TextFieldDefaults.MinWidth)
                                    .padding(horizontal = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Родитель",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Switch(
                                    checked = model.cIsParent,
                                    onCheckedChange = {
                                        component.onEvent(
                                            UsersStore.Intent.ChangeCIsParent(
                                                it
                                            )
                                        )
                                    },
                                    enabled = !isCreatingInProcess
                                )
                            }
                        } else {
                            var expandedForms by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expandedForms,
                                onExpandedChange = {
                                    expandedForms = !expandedForms
                                }
                            ) {
                                val form = model.forms.firstOrNull { it.id == model.cFormId }

                                // textfield
                                OutlinedTextField(
                                    modifier = Modifier
                                        .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
                                    readOnly = true,
                                    value = if (form != null) "${form.classNum} ${form.title}" else "",
                                    placeholder = { Text("Выберите") },
                                    onValueChange = {},
                                    label = { Text("Класс") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedForms
                                        )
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    enabled = !isCreatingInProcess
                                )
                                // menu
                                ExposedDropdownMenu(
                                    expanded = expandedForms,
                                    onDismissRequest = {
                                        expandedForms = false
                                    },
                                ) {
                                    // menu items
                                    (emptyList<CutedForm>() + CutedForm(
                                        id = 0,
                                        title = "Никакой",
                                        0
                                    ) + model.forms).forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text("${selectionOption.classNum} ${selectionOption.title}") },
                                            onClick = {
                                                component.onEvent(
                                                    UsersStore.Intent.ChangeCFormId(
                                                        selectionOption.id
                                                    )
                                                )
                                                expandedForms = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(7.dp))
                            CustomTextField(
                                value = model.cParentFirstFIO,
                                onValueChange = {
                                    component.onEvent(UsersStore.Intent.ChangeCParentFirstFIO(it))
                                },
                                text = "ФИО матери",
                                isEnabled = !isCreatingInProcess,//model.isCreatingInProcess,
                                onEnterClicked = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                },
                                focusManager = focusManager,
                                isMoveUpLocked = false,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Password,
                                supText = "Фамилия Имя Отчество"
                            )
                            Spacer(Modifier.height(7.dp))
                            CustomTextField(
                                value = model.cParentSecondFIO,
                                onValueChange = {
                                    component.onEvent(UsersStore.Intent.ChangeCParentSecondFIO(it))
                                },
                                text = "ФИО отца",
                                isEnabled = !isCreatingInProcess,//model.isCreatingInProcess,
                                onEnterClicked = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                },
                                focusManager = focusManager,
                                isMoveUpLocked = false,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Password,
                                supText = "Фамилия Имя Отчество"
                            )
                            Spacer(Modifier.height(7.dp))
                        }

                        Spacer(Modifier.height(7.dp))
                        AnimatedCommonButton(
                            text = "Создать",
                            modifier = Modifier.width(TextFieldDefaults.MinWidth),
                            isEnabled = num == 5
                        ) {
                            component.onEvent(UsersStore.Intent.CreateUser)
                        }
                        Spacer(Modifier.height(10.dp))

                    }
                    item {Spacer(Modifier.height(height = heightVal.dp/2))}
                }
            }
        } else {
            Column(
                Modifier.padding(10.dp).fillMaxWidth()
                    .height(if (model.cParentLogins != null) 260.dp else 200.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                        if (cNModel.value.state == NetworkState.Error) {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text(cNModel.value.error)
//                                Spacer(Modifier.height(7.dp))
//                                CustomTextButton("Попробовать ещё раз") {
//                                    component.cUserBottomSheet.nInterface.fixError()
//                                }
//                            }
//                        } else {
                Text(
                    "${model.cSurname} ${model.cName} ${model.cPraname}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        model.cLogin,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(7.dp))
                    if (model.cParentLogins != null) {
                        listOf(
                            model.cParentFirstFIO,
                            model.cParentSecondFIO
                        ).forEachIndexed { i, s ->
                            if (s.isNotEmpty()) {
                                Text(
                                    s,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    model.cParentLogins?.getOrNull(i).toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        component.cUserBottomSheet.onEvent(CBottomSheetStore.Intent.HideSheet)
                        component.onEvent(UsersStore.Intent.ClearUser)
                    },
                    modifier = Modifier.width(TextFieldDefaults.MinWidth)
                ) {
                    Text("Готово")
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}


@Composable
fun TableScreen(
    columnNames: List<String>,
    widthsInit: Map<String, Dp>,
    rows: List<Pair<String, Map<String, String>>>,
    isEditable: Boolean = false,
    onEditClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val widths = mutableStateMapOf<String, Dp>()
    columnNames.forEach {
        widths[it] = widthsInit[it] ?: 20.dp
    }
    val density = LocalDensity.current
    val vScrollState = rememberLazyListState()
    val hScrollState = rememberScrollState()

    val lP = 50.dp

    val allHeight = remember { mutableStateOf(0.dp) }
    val allWidth = remember { mutableStateOf(0.dp) }
    ScrollBaredBox(
        vState = vScrollState, hState = hScrollState,
        height = allHeight, width = allWidth,
        modifier = modifier
    ) {
        Box(Modifier.horizontalScroll(hScrollState)) {
            Row() {//modifier = Modifier.horizontalScroll(hhScrollState)
//            Divider(Modifier.height(allHeight.value).width(1.dp))
                Spacer(Modifier.width(lP))
                columnNames.onEachIndexed { index, i ->
                    if (index != widths.size - 1) {
                        Spacer(Modifier.width(widths[i]!! - 0.5.dp))
                        Divider(
                            Modifier.height(allHeight.value).width(1.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = .4f)
                        )
                    }
                }


            }

            Column(
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    allHeight.value =
                        with(density) { layoutCoordinates.size.height.toDp() }
                }) {
//            Divider(Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp).height(1.dp))

                Row(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        allWidth.value =
                            with(density) { layoutCoordinates.size.width.toDp() + lP / 4 }
                    }, //.horizontalScroll(hhScrollState)
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.width(lP))
                    columnNames.forEach { columnName ->

                        val isChecked = remember { mutableStateOf(false) }


                        Box(
                            modifier = if (!isChecked.value) Modifier.width(IntrinsicSize.Min) else Modifier.width(
                                widths[columnName]!!
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = columnName,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(max = 200.dp)
                                    .onGloballyPositioned {
                                        val width = with(density) { it.size.height.toDp() }
                                        if (width > widths[columnName]!!) widths[columnName] =
                                            (width)
                                        else isChecked.value = true

                                    },
                                onTextLayout = {
                                    if (it.hasVisualOverflow) {
                                        widths[columnName] = widths[columnName]!! + 15.dp
                                    }
                                },
                                overflow = TextOverflow.Ellipsis,
                                softWrap = false
                            )
                        }
                    }
                }


                Divider(
                    Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp).height(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = .4f)
                )
                LazyColumn(
                    modifier = Modifier,
                    state = vScrollState,

                    ) {
                    itemsIndexed(items = rows) { index, row ->
                        Column {
                            Text(
                                text = row.first,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .offset(with(density) { hScrollState.value.toDp() })
                            )
                            Row {
                                Box(
                                    Modifier.width(lP),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    IconButton(
                                        onClick = { onEditClick(index) },
                                        modifier = Modifier.padding(top = 5.dp).size(15.dp)
                                    ) {
                                        Icon(Icons.Rounded.Edit, null)
                                    }
                                }
                                row.second.forEach { (key, value) ->
                                    val isChecked = remember { mutableStateOf(false) }
                                    Box(
                                        modifier = if (!isChecked.value) Modifier.width(
                                            IntrinsicSize.Min
                                        )
                                        else Modifier.width(widths[key]!!),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = value,
                                            modifier = Modifier
                                                .widthIn(max = 200.dp)
                                                .onGloballyPositioned {
                                                    val width =
                                                        with(density) { it.size.height.toDp() }
                                                    if (width > widths[key]!!) widths[key] =
                                                        width
                                                    else isChecked.value = true
                                                },
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(5.dp))
                            if (index != rows.lastIndex) {
                                Divider(
                                    Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp)
                                        .height(1.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = .4f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


