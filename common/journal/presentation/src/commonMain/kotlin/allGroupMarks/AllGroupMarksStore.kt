package allGroupMarks

import ReportData
import com.arkivanov.mvikotlin.core.store.Store
import allGroupMarks.AllGroupMarksStore.Intent
import allGroupMarks.AllGroupMarksStore.Label
import allGroupMarks.AllGroupMarksStore.State
import lessonReport.LessonReportStore
import report.AllGroupMarksStudent
import report.UserMark

interface AllGroupMarksStore : Store<Intent, State, Label> {
    data class State(
        val groupId: Int,
        val subjectId: Int,
        val subjectName: String,
        val groupName: String,
        val students: List<AllGroupMarksStudent> = emptyList(),
        val detailedStupsLogin: String = "",
        val reportData: ReportData? = null,
        val login: String
    )

    sealed interface Intent {
        data object Init: Intent
        data class OpenDetailedStups(val studentLogin: String) : Intent

        data class OpenFullReport(val reportId: Int) : Intent

        data object DeleteReport: Intent
    }

    sealed interface Message {
        data class StudentsUpdated(val students: List<AllGroupMarksStudent>) : Message
        data class DetailedStupsOpened(val login: String) : Message
        data class FullReportOpened(val reportData: ReportData?) : Message
    }

    sealed interface Label

}
