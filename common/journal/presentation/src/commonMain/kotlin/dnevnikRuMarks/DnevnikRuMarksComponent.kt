package dnevnikRuMarks

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow


class DnevnikRuMarksComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val studentLogin: String
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

    val nInterface =
        NetworkInterface(componentContext, storeFactory, "DnevnikRuMarksComponent")

    val journalRepository: JournalRepository = Inject.instance()

    val stupsDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "StupsDialogComponentIntDnevnikRuMarks",
        {}
    )

    private val dnevnikRuMarkStore =
        instanceKeeper.getStore(key = "dnevnikRuMark/$studentLogin") {
            DnevnikRuMarkStoreFactory(
                storeFactory = storeFactory,
                login = studentLogin,
                nInterface = nInterface,
                journalRepository = journalRepository,
                stupsDialogComponent = stupsDialogComponent
            ).create()
        }

    val model = dnevnikRuMarkStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<DnevnikRuMarkStore.State> = dnevnikRuMarkStore.stateFlow

    fun onEvent(event: DnevnikRuMarkStore.Intent) {
        dnevnikRuMarkStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }


    init {
        onEvent(DnevnikRuMarkStore.Intent.Init)

    }

    sealed class Output {
        data object Back : Output()

    }
}