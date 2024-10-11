package dnevnikRuMarks

import com.arkivanov.mvikotlin.core.store.Reducer
import dnevnikRuMarks.DnevnikRuMarkStore.State
import dnevnikRuMarks.DnevnikRuMarkStore.Message

object DnevnikRuMarkReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.SubjectsUpdated -> {
                val newSubjects = subjects.toMutableMap()
                newSubjects[tabIndex ?: 0] = msg.subjects
                copy(subjects = newSubjects.toMap(HashMap()))
            }
            is Message.IsQuartersInited -> copy(isQuarters = msg.isQuarters, tabIndex = msg.tabIndex, tabsCount = msg.tabsCount)
            is Message.OnTabClicked -> copy(tabIndex = msg.index, isWeekDays = false, isPreviousWeekDays = false)
            is Message.OnStupsSubjectClicked -> copy(pickedSubjectId = msg.id)
            is Message.TableViewChanged -> copy(isTableView = msg.isTableView)
            Message.WeekOpened -> copy(isWeekDays = true, isPreviousWeekDays = false)
            Message.PreviousWeekOpened -> copy(isWeekDays = false, isPreviousWeekDays = true)
            is Message.MarksTableUpdated -> copy(
                tableSubjects = msg.tableSubjects,
                mDateMarks = msg.mDateMarks,
                mDates = msg.mDates
            )
        }
    }
}