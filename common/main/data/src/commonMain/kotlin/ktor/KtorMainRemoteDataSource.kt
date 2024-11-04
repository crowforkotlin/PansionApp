package ktor

import RequestPaths
import checkOnNoOk
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import journal.init.RFetchMentorGroupIdsResponse
import journal.init.RFetchStudentsInGroupReceive
import journal.init.RFetchStudentsInGroupResponse
import journal.init.RFetchTeacherGroupsResponse
import main.RChangeToUv
import main.RDeleteMainNotificationsReceive
import main.RFetchChildrenMainNotificationsResponse
import main.RFetchChildrenResponse
import main.RFetchMainAVGReceive
import main.RFetchMainAVGResponse
import main.RFetchMainHomeTasksCountReceive
import main.RFetchMainHomeTasksCountResponse
import main.RFetchMainNotificationsReceive
import main.RFetchMainNotificationsResponse
import main.RFetchSchoolDataReceive
import main.RFetchSchoolDataResponse
import main.school.*
import mentoring.RFetchJournalBySubjectsReceive
import mentoring.RFetchJournalBySubjectsResponse
import mentoring.RFetchMentoringStudentsResponse
import mentoring.preAttendance.RFetchPreAttendanceDayReceive
import mentoring.preAttendance.RFetchPreAttendanceDayResponse
import mentoring.preAttendance.RSavePreAttendanceDayReceive
import rating.RFetchScheduleSubjectsResponse
import rating.RFetchSubjectRatingReceive
import rating.RFetchSubjectRatingResponse
import registration.CloseRequestQRReceive
import registration.OpenRequestQRReceive
import registration.SolveRequestReceive
import report.RCreateReportReceive
import report.RCreateReportResponse
import report.RFetchHeadersResponse
import report.RFetchRecentGradesReceive
import report.RFetchRecentGradesResponse
import report.RFetchReportDataReceive
import report.RFetchReportDataResponse
import schedule.RFetchPersonScheduleReceive
import schedule.RPersonScheduleList

class KtorMainRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun updateTodayDuty(r: RUpdateTodayDuty) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.UpdateDuty)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }
    suspend fun startNewDayDuty(r: RStartNewDayDuty) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.StartNewDayDuty)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }
    suspend fun fetchDuty(r: RFetchDutyReceive) : RFetchDutyResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchDuty)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchMinistrySettings(r: RFetchMinistryStudentsReceive) : RFetchMinistrySettingsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchMinistrySettings)
                setBody(r)
            }
        }.body()
    }
    suspend fun createMinistryStudent(r: RCreateMinistryStudentReceive) : RFetchMinistrySettingsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.CreateMinistryStudent)
                setBody(r)
            }
        }.body()
    }

    suspend fun changeToUv(r: RChangeToUv) {
        httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Main.ChangeToUv)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun fetchSchoolData(r: RFetchSchoolDataReceive) : RFetchSchoolDataResponse {
        return httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Main.FetchSchoolData)
            }
        }.body()
    }

    suspend fun fetchJournalBySubjects(r: RFetchJournalBySubjectsReceive) : RFetchJournalBySubjectsResponse {
        return httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Mentoring.FetchJournalBySubjects)
            }
        }.body()
    }

    suspend fun openRegistrationQR(r: OpenRequestQRReceive) {
        httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Registration.OpenQR)
            }
        }.status.value.checkOnNoOk()
    }
    suspend fun solveRegistrationRequest(r: SolveRequestReceive) {
        httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Registration.SolveRequest)
            }
        }.status.value.checkOnNoOk()
    }
    suspend fun closeRegistrationQR(r: CloseRequestQRReceive) {
        httpClient.post {
            url {
                bearer()
                setBody(r)
                path(RequestPaths.Registration.CloseQR)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun fetchMentorGroupIds(): RFetchMentorGroupIdsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchMentorGroupIds)
            }
        }.body()
    }

    suspend fun fetchMainNotifications(r: RFetchMainNotificationsReceive) : RFetchMainNotificationsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchNotifications)
                setBody(r)
            }
        }.body()
    }
    suspend fun fetchChildrenMainNotifications() : RFetchChildrenMainNotificationsResponse{
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchChildrenNotifications)
            }
        }.body()
    }

    suspend fun fetchChildren() : RFetchChildrenResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchChildren)
            }
        }.body()
    }
    suspend fun deleteMainNotification(r: RDeleteMainNotificationsReceive) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.CheckNotification)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }


    suspend fun savePreAttendanceDay(r: RSavePreAttendanceDayReceive) {
        httpClient.post {
            url {
                bearer()
                path(RequestPaths.Mentoring.SavePreAttendanceDay)
                setBody(r)
            }
        }.status.value.checkOnNoOk()
    }

    suspend fun fetchPreAttendanceDay(r: RFetchPreAttendanceDayReceive) : RFetchPreAttendanceDayResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Mentoring.FetchPreAttendanceDay)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchMentorStudents(): RFetchMentoringStudentsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Mentoring.FetchMentoringStudents)
            }
        }.body()
    }


    suspend fun fetchScheduleSubjects(): RFetchScheduleSubjectsResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchScheduleSubjects)
            }
        }.body()
    }

    suspend fun fetchSubjectRating(r: RFetchSubjectRatingReceive): RFetchSubjectRatingResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Main.FetchSubjectRating)
                setBody(r)
            }
        }.body()
    }


    suspend fun fetchPersonSchedule(r: RFetchPersonScheduleReceive): RPersonScheduleList {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Lessons.FetchPersonSchedule)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchRecentGrades(r: RFetchRecentGradesReceive): RFetchRecentGradesResponse {
        return httpClient.post {
            url {
                bearer()
                path(RequestPaths.Reports.FetchRecentGrades)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchTeacherGroups(): RFetchTeacherGroupsResponse {
        val response = httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchTeacherGroups)
            }
        }
        return response.body()
    }

    suspend fun fetchMainAvg(request: RFetchMainAVGReceive): RFetchMainAVGResponse {
        val response = httpClient.post {
            bearer()
            url {
                contentType(ContentType.Application.Json)
                path(RequestPaths.Main.FetchMainAVG)
                setBody(request)
            }
        }
        return response.body()
    }
    suspend fun fetchMainHomeTasksCount(r: RFetchMainHomeTasksCountReceive): RFetchMainHomeTasksCountResponse {
        return httpClient.post {
            url {bearer()
                path(RequestPaths.Main.FetchHomeTasksCount)
                setBody(r)
            }
        }.body()
    }

    suspend fun fetchStudentInGroup(request: RFetchStudentsInGroupReceive): RFetchStudentsInGroupResponse {
        val response = httpClient.post {
            bearer()
            url {
                contentType(ContentType.Application.Json)
                path(RequestPaths.Lessons.FetchStudentsInGroup)
                setBody(request)
            }
        }

        return response.body()
    }

    suspend fun fetchReportHeaders(): RFetchHeadersResponse {
        val response = httpClient.post {
            bearer()
            url {
//                contentType(ContentType.Application.Json)
                path(RequestPaths.Reports.FetchReportHeaders)
            }
        }
        return response.body()
    }

    suspend fun createReport(r: RCreateReportReceive): RCreateReportResponse {
        val response = httpClient.post {
            bearer()
            url {
//                contentType(ContentType.Application.Json)
                path(RequestPaths.Reports.CreateReport)
                setBody(r)
            }
        }
        return response.body()
    }

    suspend fun fetchReportData(r: RFetchReportDataReceive): RFetchReportDataResponse {
        val response = httpClient.post {
            bearer()
            url {
//                contentType(ContentType.Application.Json)
                path(RequestPaths.Reports.FetchReportData)
                setBody(r)
            }
        }
        return response.body()
    }
}