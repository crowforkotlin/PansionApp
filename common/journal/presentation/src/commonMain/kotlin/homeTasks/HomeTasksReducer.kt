package homeTasks

import com.arkivanov.mvikotlin.core.store.Reducer
import homeTasks.HomeTasksStore.State
import homeTasks.HomeTasksStore.Message

object HomeTasksReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.TasksUpdated -> copy(homeTasks = msg.tasks)
        }
    }
}