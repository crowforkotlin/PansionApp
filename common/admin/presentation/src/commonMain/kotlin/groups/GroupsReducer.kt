package groups

import com.arkivanov.mvikotlin.core.store.Reducer
import groups.GroupsStore.State
import groups.GroupsStore.Message

object GroupsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.ListInited -> copy(
                subjects = msg.subjects,
                teachers = msg.teachers,
                forms = msg.forms
            )
            is Message.ViewChanged -> copy(view = msg.view)
            is Message.FormsListChanged -> copy(forms = msg.forms)
            is Message.SubjectListChanged -> copy(subjects = msg.subjects,)
        }
    }
}