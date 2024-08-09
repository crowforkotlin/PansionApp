import com.arkivanov.mvikotlin.core.store.Reducer
import SettingsStore.State
import SettingsStore.Message

object SettingsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.ColorModeChanged -> copy(newColorMode = msg.colorMode)
            is Message.DevicesFetched -> copy(deviceList = msg.devices)
        }
    }
}