import achievements.RFetchAchievementsForStudentReceive
import achievements.RFetchAchievementsResponse
import homework.RCheckHomeTaskReceive
import homework.RFetchHomeTasksReceive
import homework.RFetchHomeTasksResponse
import homework.RFetchReportHomeTasksReceive
import homework.RFetchReportHomeTasksResponse
import homework.RFetchTasksInitReceive
import homework.RFetchTasksInitResponse
import homework.RSaveReportHomeTasksReceive
import ktor.KtorJournalRemoteDataSource
import report.RFetchAllGroupMarksReceive
import report.RFetchAllGroupMarksResponse
import report.RFetchDetailedStupsReceive
import report.RFetchDetailedStupsResponse
import report.RFetchDnevnikRuMarksReceive
import report.RFetchDnevnikRuMarksResponse
import report.RFetchFullReportData
import report.RFetchReportStudentsReceive
import report.RFetchReportStudentsResponse
import report.RFetchSubjectQuarterMarksReceive
import report.RFetchSubjectQuarterMarksResponse
import report.RIsQuartersReceive
import report.RIsQuartersResponse
import report.RUpdateReportReceive

class JournalRepositoryImpl(
    private val remoteDataSource: KtorJournalRemoteDataSource,
//    private val cacheDataSource: SettingsAuthDataSource
) : JournalRepository {

    override suspend fun fetchAchievementsForStudent(r: RFetchAchievementsForStudentReceive): RFetchAchievementsResponse {
        return remoteDataSource.fetchAchievementsForStudent(r)
    }

    override suspend fun checkHomeTask(r: RCheckHomeTaskReceive) {
        return remoteDataSource.checkHomeTask(r)
    }

    override suspend fun fetchHomeTasksInit(r: RFetchTasksInitReceive): RFetchTasksInitResponse {
        return remoteDataSource.fetchHomeTasksInit(r)
    }

    override suspend fun fetchHomeTasks(r: RFetchHomeTasksReceive): RFetchHomeTasksResponse {
        return remoteDataSource.fetchHomeTasks(r)
    }

    override suspend fun saveReportHomeTasks(r: RSaveReportHomeTasksReceive): RFetchReportHomeTasksResponse {
        return remoteDataSource.saveReportHomeTasks(r)
    }

    override suspend fun fetchReportHomeTasks(r: RFetchReportHomeTasksReceive): RFetchReportHomeTasksResponse {
        return remoteDataSource.fetchReportHomeTasks(r)
    }

    override suspend fun updateWholeReport(r: RUpdateReportReceive) {
        remoteDataSource.updateWholeReport(r)
    }

    override suspend fun fetchReportStudents(r: RFetchReportStudentsReceive): RFetchReportStudentsResponse {
        return remoteDataSource.fetchReportStudents(r)
    }

    override suspend fun fetchDnevnikRuMarks(login: String, quartersNum: String, isQuarters: Boolean): RFetchDnevnikRuMarksResponse {
        return remoteDataSource.fetchDnevnikRuMarks(RFetchDnevnikRuMarksReceive(login, quartersNum, isQuarters))
    }

    override suspend fun fetchFullReportData(reportId: Int): ReportData {
        return remoteDataSource.fetchFullReportData(RFetchFullReportData(reportId = reportId))
    }

    override suspend fun fetchSubjectQuarterMarks(
        login: String,
        subjectId: Int,
        quartersNum: String
    ): RFetchSubjectQuarterMarksResponse {
        return remoteDataSource.fetchSubjectQuarterMarks(RFetchSubjectQuarterMarksReceive(
            subjectId = subjectId,
            login = login,
            quartersNum = quartersNum
        ))
    }

    override suspend fun fetchIsQuarter(login: String): RIsQuartersResponse {
        return remoteDataSource.fetchIsQuarter(RIsQuartersReceive(login))
    }

    override suspend fun fetchAllStups(login: String): RFetchDetailedStupsResponse {
        return remoteDataSource.fetchAllStups(RFetchDetailedStupsReceive(login))
    }

    override suspend fun fetchAllGroupMarks(
        groupId: Int,
        subjectId: Int
    ): RFetchAllGroupMarksResponse {
        return remoteDataSource.fetchAllGroupMarks(
            RFetchAllGroupMarksReceive(
                subjectId = subjectId,
                groupId = groupId
            )
        )
    }
}