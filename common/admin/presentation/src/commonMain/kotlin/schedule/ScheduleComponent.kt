package schedule

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.mpChose.mpChoseComponent
import components.networkInterface.NetworkInterface
import di.Inject

class ScheduleComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "scheduleComponentNInterface"
    )
    val mpCreateItem = mpChoseComponent(
        componentContext,
        storeFactory,
        name = "mpChoseCreateScheduleItem"
    )
    val mpEditItem = mpChoseComponent(
        componentContext,
        storeFactory,
        name = "mpChoseEditScheduleItem"
    )

    val listCreateTeacher = ListComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "listCreateTeacher",
        onItemClick = {
            onCreateTeacherClick(it.id)
        }
    )

    private fun onCreateTeacherClick(login: String) {
        val state = model.value
        val key = if (state.isDefault) state.defaultDate.toString() else state.currentDate.second
        if (login !in (state.activeTeachers[key] ?: emptyList())
        ) {
            onEvent(
                ScheduleStore.Intent.CreateTeacher(login)
            )
        }
        listCreateTeacher.onEvent(ListDialogStore.Intent.HideDialog)
    }


    private val adminRepository: AdminRepository = Inject.instance()


    private val scheduleStore =
        instanceKeeper.getStore {
            ScheduleStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nInterface = nInterface,
                mpCreateItem = mpCreateItem,
                mpEditItem = mpEditItem,
                listCreateTeacher = listCreateTeacher
            ).create()
        }

    private val backCallback = BackCallback {
        onOutput(Output.BackToAdmin)
    }


    init {
        backHandler.register(backCallback)
        onEvent(ScheduleStore.Intent.Init)
    }

    val model = scheduleStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: ScheduleStore.Intent) {
        scheduleStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object BackToAdmin : Output()
    }
}