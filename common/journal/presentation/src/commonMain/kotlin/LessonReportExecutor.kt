import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor

class LessonReportExecutor : CoroutineExecutor<LessonReportStore.Intent, Unit, LessonReportStore.State, LessonReportStore.Message, LessonReportStore.Label>() {
    override fun executeIntent(intent: LessonReportStore.Intent, getState: () -> LessonReportStore.State) {
        when (intent) {
            else -> TODO()
        }
    }
}
