package excel

import FIO
import admin.users.ToBeCreatedStudent
import admin.users.UserInit
import org.apache.poi.ss.usermodel.WorkbookFactory
import server.Moderation
import server.Roles
import users.UsersComponent
import users.UsersStore
import java.io.File
import java.io.FileInputStream



actual fun importStudents(path: String, component: UsersComponent) {
    val inputStream = FileInputStream(File(path))
    val workbook = WorkbookFactory.create(inputStream)
    val workSheet = workbook.getSheetAt(0)

    val toBeCreated: MutableList<ToBeCreatedStudent> = mutableListOf()
    workSheet.forEach { wb ->
        val r = wb.toList()
        val student = r[0].toString().split(" ")
        val bDay = r[1].toString().replace(".", "")
        val formId = r[2].toString().split(".")[0].toInt()
        val parent = r[3].toString()

        val userInit = UserInit(
            fio = FIO(
                name = student[1],
                surname = student[0],
                praname = student.getOrNull(2)
            ),
            birthday = bDay,
            role = Roles.student,
            moderation = Moderation.nothing,
            isParent = false
        )
        toBeCreated.add(
            ToBeCreatedStudent(
                user = userInit,
                parents = listOf(parent),
                formId = formId
            )
        )


    }
    component.onEvent(UsersStore.Intent.CreateUsers(toBeCreated))
}