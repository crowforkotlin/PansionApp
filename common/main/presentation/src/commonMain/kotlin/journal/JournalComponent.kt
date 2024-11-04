package journal

import AuthRepository
import MainRepository
import ReportData
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import di.Inject
import journal.init.TeacherGroup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import report.ReportHeader
import server.getDate
import server.getSixTime

//data class JournalComponentData(
//    val header: ReportHeader
//)

class JournalComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    private val authRepository: AuthRepository = Inject.instance()
    private val mainRepository: MainRepository = Inject.instance()
    val nInterface = NetworkInterface(componentContext, storeFactory, "journalCComponent")
    val nOpenReportInterface = NetworkInterface(componentContext, storeFactory, "nOpenReportComponent")
    val groupListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "groupListInMainJournal",
        onItemClick = {
            onEvent(
                JournalStore.Intent.OnGroupClicked(
                    it.id.toInt(), getSixTime(),
                    date = null, lessonId = null
                )
            )
        })

    val fGroupListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "filterGroupListInMainJournal",
        onItemClick = {
            onEvent(JournalStore.Intent.FilterGroup(it.id.toInt()))
        })
    val fDateListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "filterDateListInMainJournal",
        onItemClick = {
            onEvent(JournalStore.Intent.FilterDate(it.id))
        })
    val fTeachersListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "filterTeacherListInMainJournal",
        onItemClick = {
            onEvent(JournalStore.Intent.FilterTeacher(it.id))
        })
    val fStatusListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "filterStatusListInMainJournal",
        onItemClick = {
            onEvent(JournalStore.Intent.FilterStatus(it.id.toBoolean()))
        })


    val studentsInGroupCAlertDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "studentsInGroupListCAlertDialogInMainJournal",
        onAcceptClick = {
            createReport()
        },
        onDeclineClick = {
            //some magic..
            groupListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
            hideStudentAlarm()
        }
    )

    private fun hideStudentAlarm() {
        studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
    }

    private fun getReportHeader(
        teacherGroups: List<TeacherGroup> = state.value.teacherGroups
    ): ReportHeader {
        val group =
            teacherGroups.first { it.cutedGroup.groupId == state.value.currentGroupId }
        val time = if (state.value.time != "") state.value.time else getSixTime()
        onEvent(JournalStore.Intent.ResetTime)
        return ReportHeader(
            reportId = model.value.creatingReportId,
            subjectName = group.subjectName,
            subjectId = group.subjectId, //не нужное
            groupName = group.cutedGroup.groupName,
            groupId = state.value.currentGroupId,
            teacherName = "${authRepository.fetchSurname()} ${authRepository.fetchName()[0]}. ${authRepository.fetchPraname()[0]}.",
            teacherLogin = authRepository.fetchLogin(),
            date = getDate(),
            time = time,
            status = false,
            module = state.value.currentModule,
            theme = ""
        )
    }

    fun createReport(header: ReportHeader = getReportHeader()) {
        hideStudentAlarm()

        val data = ReportData(
            header = header,
//            topic = "",
            description = "",
            editTime = "",
            ids = 0,
            isMentorWas = false,
            isEditable = true,
            customColumns = emptyList()
        )
        openReport(data)
    }

    fun openReport(reportData: ReportData) {
        onOutput(
            Output.NavigateToLessonReport(
                reportData
            )
        )
    }

    private val journalStore =
        instanceKeeper.getStore {
            JournalStoreFactory(
                storeFactory = storeFactory,
                mainRepository = mainRepository,
                groupListComponent = groupListComponent,
                studentsInGroupCAlertDialogComponent = studentsInGroupCAlertDialogComponent,
                nInterface = nInterface,
                nOpenReportInterface = nOpenReportInterface,
//                authRepository = authRepository
                fTeachersListComponent = fTeachersListComponent,
                fGroupListComponent = fGroupListComponent,
                fDateListComponent = fDateListComponent,
                fStatusListComponent = fStatusListComponent,
                authRepository = authRepository
            ).create()
        }

    val model = journalStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<JournalStore.State> = journalStore.stateFlow


    init {
        fStatusListComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(
                    ListItem(
                        id = "True",
                        text = "Закончен"
                    ),
                    ListItem(
                        id = "False",
                        text = "В процессе"
                    )
                )
            )
        )
//        onEvent(JournalStore.Intent.Init )
    }

    fun onEvent(event: JournalStore.Intent) {
        journalStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToSettings : Output()
        data class NavigateToLessonReport(val reportData: ReportData) : Output()
    }
}