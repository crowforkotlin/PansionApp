package ktor

import RequestPaths
import admin.groups.forms.RCreateFormGroupReceive
import admin.groups.forms.RFetchCutedGroupsResponse
import admin.users.RClearUserPasswordReceive
import admin.groups.forms.outside.CreateFormReceive
import admin.groups.subjects.RCreateGroupReceive
import admin.groups.students.RBindStudentToFormReceive
import admin.users.REditUserReceive
import admin.groups.forms.outside.RFetchFormsResponse
import admin.groups.subjects.topBar.RFetchAllSubjectsResponse
import admin.groups.forms.outside.RFetchMentorsResponse
import admin.groups.subjects.RFetchTeachersResponse
import admin.groups.subjects.RFetchGroupsReceive
import admin.groups.forms.RFetchFormGroupsReceive
import admin.groups.forms.RFetchFormGroupsResponse
import admin.groups.students.deep.RFetchStudentGroupsReceive
import admin.groups.students.deep.RFetchStudentGroupsResponse
import admin.groups.students.RFetchStudentsInFormReceive
import admin.groups.students.RFetchStudentsInFormResponse
import admin.groups.subjects.RFetchGroupsResponse
import admin.groups.subjects.topBar.RCreateSubjectReceive
import admin.users.RRegisterUserReceive
import admin.users.RCreateUserResponse
import admin.users.RFetchAllUsersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

class KtorAdminRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun performRegistrationUser(request: RRegisterUserReceive): RCreateUserResponse {
        val response = httpClient.post {
            bearer()
            url {

                path("server/user/register")
                setBody(request)
            }
        }

        return response.body()
    }

    suspend fun performFetchAllUsers(): RFetchAllUsersResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.UserManage.FetchAllUsers)
            }
        }.body()
    }

    suspend fun clearUserPassword(request: RClearUserPasswordReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.UserManage.ClearPasswordAdmin)
                setBody(request)
            }
        }.body()

    }

    suspend fun performEditUser(request: REditUserReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.UserManage.EditUser)
                setBody(request)
            }
        }.body()

    }


    suspend fun performFetchAllSubjects(): RFetchAllSubjectsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchAllSubjects)
            }
        }.body()

    }
    suspend fun performStudentsInForm(request: RFetchStudentsInFormReceive): RFetchStudentsInFormResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchStudentsInForm)
                setBody(request)
            }
        }.body()

    }

    suspend fun performFormGroups(request: RFetchFormGroupsReceive): RFetchFormGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchFormGroups)
                setBody(request)
            }
        }.body()

    }

    suspend fun createNewSubject(request: RCreateSubjectReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateSubject)
                setBody(request)
            }
        }.body()
    }

    suspend fun createFormGroup(request: RCreateFormGroupReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateFormGroup)
                setBody(request)
            }
        }.body()
    }

    suspend fun bindStudentToForm(request: RBindStudentToFormReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.BindStudentToForm)
                setBody(request)
            }
        }.body()
    }

    suspend fun createGroup(request: RCreateGroupReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateGroup)
                setBody(request)
            }
        }.body()
    }

    suspend fun createForm(request: CreateFormReceive) {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.CreateForm)
                setBody(request)
            }
        }.body()
    }

    suspend fun performFetchGroups(request: RFetchGroupsReceive): RFetchGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchGroups)
                setBody(request)
            }
        }.body()
    }
    suspend fun performFetchStudentGroups(request: RFetchStudentGroupsReceive): RFetchStudentGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchStudentsInGroup)
                setBody(request)
            }
        }.body()
    }

    suspend fun performFetchAllForms(): RFetchFormsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchAllForms)
            }
        }.body()
    }

    suspend fun performFetchTeachersForGroup(): RFetchTeachersResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchTeachersForGroup)
            }
        }.body()
    }

    suspend fun performFetchMentorsForGroups(): RFetchMentorsResponse {
        return httpClient.post {
            bearer()
            url {
                path(RequestPaths.Lessons.FetchMentorsForGroup)
            }
        }.body()
    }

    suspend fun performFetchCutedGroups(request: RFetchGroupsReceive): RFetchCutedGroupsResponse {
        return httpClient.post {
            bearer()
            url {
                contentType(ContentType.Application.Json)
                path(RequestPaths.Lessons.FetchCutedGroups)
                setBody(request)
            }
        }.body()
    }
//    suspend fun performCheckActivation(request: CheckActivationReceive) : CheckActivationResponse {
//        return httpClient.post {
//            url {
//                path("check/auth")
//                setBody(request)
//            }
//        }.body()
//    }
}