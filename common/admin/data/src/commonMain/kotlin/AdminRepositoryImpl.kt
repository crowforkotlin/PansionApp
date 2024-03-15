import admin.groups.GroupInit
import admin.groups.forms.FormInit
import admin.groups.forms.RCreateFormGroupReceive
import admin.users.RClearUserPasswordReceive
import admin.groups.forms.outside.CreateFormReceive
import admin.groups.subjects.RCreateGroupReceive
import admin.groups.students.RBindStudentToFormReceive
import admin.groups.forms.RFetchCutedGroupsResponse
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
import admin.users.UserInit
import ktor.KtorAdminRemoteDataSource

class AdminRepositoryImpl(
    private val remoteDataSource: KtorAdminRemoteDataSource
) : AdminRepository {
    override suspend fun registerUser(user: UserInit): RCreateUserResponse {
        return remoteDataSource.performRegistrationUser(
            RRegisterUserReceive(
                userInit = UserInit(
                    fio = FIO(
                        name = user.fio.name,
                        surname = user.fio.surname,
                        praname = user.fio.praname
                    ),
                    birthday = user.birthday,
                    role = user.role,
                    moderation = user.moderation,
                    isParent = user.isParent
                )
            )
        )
    }

    override suspend fun fetchAllUsers(): RFetchAllUsersResponse {
        return remoteDataSource.performFetchAllUsers()
    }

    override suspend fun clearUserPassword(login: String) {
        remoteDataSource.clearUserPassword(RClearUserPasswordReceive(login))
    }

    override suspend fun editUser(login: String, user: UserInit) {
        remoteDataSource.performEditUser(
            REditUserReceive(
                login = login,
                user = UserInit(
                    fio = FIO(
                        name = user.fio.name,
                        surname = user.fio.surname,
                        praname = user.fio.praname
                    ),
                    birthday = user.birthday,
                    role = user.role,
                    moderation = user.moderation,
                    isParent = user.isParent
                )
            )
        )
    }

    override suspend fun fetchAllSubjects(): RFetchAllSubjectsResponse {
        return remoteDataSource.performFetchAllSubjects()
    }

    override suspend fun createSubject(name: String) {
        remoteDataSource.createNewSubject(
            RCreateSubjectReceive(
                name = name
            )
        )
    }

    override suspend fun fetchGroups(subjectId: Int): RFetchGroupsResponse {
        return remoteDataSource.performFetchGroups(
            RFetchGroupsReceive(
                subjectId = subjectId
            )
        )
    }

    override suspend fun fetchStudentGroups(login: String): RFetchStudentGroupsResponse {
        return remoteDataSource.performFetchStudentGroups(
            RFetchStudentGroupsReceive(
                studentLogin = login
            )
        )
    }

    override suspend fun fetchCutedGroups(id: Int): RFetchCutedGroupsResponse {
        return remoteDataSource.performFetchCutedGroups(
            RFetchGroupsReceive(
                subjectId = id
            )
        )
    }

    override suspend fun fetchFormGroups(id: Int): RFetchFormGroupsResponse {
        return remoteDataSource.performFormGroups(
            RFetchFormGroupsReceive(
                formId = id
            )
        )
    }

    override suspend fun fetchStudentsInForm(formId: Int): RFetchStudentsInFormResponse {
        return remoteDataSource.performStudentsInForm(
            RFetchStudentsInFormReceive(
                formId = formId
            )
        )
    }

    override suspend fun bindStudentToForm(
        login: String, formId: Int
    ) {
        remoteDataSource.bindStudentToForm(
            RBindStudentToFormReceive(
                studentLogin = login,
                formId = formId
            )
        )
    }

    override suspend fun createFormGroup(
        formId: Int,
        subjectId: Int,
        groupId: Int
    ) {
        remoteDataSource.createFormGroup(
            RCreateFormGroupReceive(
                formId = formId,
                subjectId = subjectId,
                groupId = groupId
            )
        )
    }

    override suspend fun createGroup(
        name: String,
        mentorLogin: String,
        subjectId: Int,
        difficult: String
    ) {
        remoteDataSource.createGroup(
            RCreateGroupReceive(
                group = GroupInit(
                    name = name,
                    teacherLogin = mentorLogin,
                    subjectId = subjectId,
                    difficult = difficult
                )
            )
        )
    }

    override suspend fun createForm(
        title: String,
        mentorLogin: String,
        classNum: Int,
        shortTitle: String
    ) {
        remoteDataSource.createForm(
            CreateFormReceive(
                form = FormInit(
                    title = title,
                    mentorLogin = mentorLogin,
                    classNum = classNum,
                    shortTitle = shortTitle
                )
            )
        )
    }

    override suspend fun fetchAllForms(): RFetchFormsResponse {
        return remoteDataSource.performFetchAllForms()
    }

    override suspend fun fetchAllTeachers(): RFetchTeachersResponse {
        return remoteDataSource.performFetchTeachersForGroup()
    }

    override suspend fun fetchAllMentors(): RFetchMentorsResponse {
        return remoteDataSource.performFetchMentorsForGroups()
    }
}