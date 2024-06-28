package com.nevrozq.pansion.database.ratingTable

import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.ratingEntities.ForAvg
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.RatingEntityDTO
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.studentGroups.StudentGroups
import com.nevrozq.pansion.database.studentsInForm.StudentsInForm
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.UserDTO
import com.nevrozq.pansion.database.users.Users
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import server.cut


private data class AddItem(
    val subjectId: Int,
    val stups: Int,
    val avg: String,
    val table: RatingTable
)

private data class RTables(
    val weekT: RatingTable,
    val moduleT: RatingTable,
    val yearT: RatingTable
)

private data class IterationsMode(
    val students: List<UserDTO>,
    val r: RTables
)

private fun MutableList<AddItem>.pAdd(
    subjectId: Int,
    table: RatingTable,
    stups: List<RatingEntityDTO>,
    avg: ForAvg
) {
    if (stups.sumOf { it.content.toInt() } > 0 && avg.count > 0) {
        this.add(
            AddItem(
                subjectId = subjectId,
                stups = stups.sumOf { it.content.toInt() },
                avg =  (avg.sum / avg.count.toFloat()).toString().cut(4),
                table = table
            )
        )
    }
}

private fun initItems(login: String, r: RTables): MutableList<AddItem> {
    val items: MutableList<AddItem> = mutableListOf()
    val allStupsWeekCount = Stups.fetchForAWeek(login)
    val allStupsYearCount = Stups.fetchForUser(login)
    val allWeekAvg = Marks.fetchWeekAVG(login)
    val allYearAvg = Marks.fetchYearAVG(login)


    items.pAdd(subjectId = -1, stups = allStupsWeekCount, avg = allWeekAvg, table = r.weekT)
    items.pAdd(subjectId = -1, stups = allStupsYearCount, avg = allYearAvg, table = r.yearT)
    return items
}

fun updateRatings() {
    transaction {
        for (i in listOf(
            RatingWeek0Table,
            RatingWeek1Table,
            RatingWeek2Table,
            RatingModule0Table,
            RatingModule1Table,
            RatingModule2Table,
            RatingYear0Table,
            RatingYear1Table,
            RatingYear2Table,
        )) {
            i.deleteAll()
        }
    }


    val forms = Forms.getAllForms().filter { it.isActive }
    val groups = Groups.getAllGroups().filter { it.isActive }
    val subjects = Subjects.fetchAllSubjects().filter { it.isActive }
    val studentsInGroup = StudentGroups.fetchAll()
    val studentsInForm = StudentsInForm.fetchAll()
    val students0 = Users.fetchAllStudents()
        .filter { s -> s.isActive &&
                studentsInForm.firstOrNull { it.login == s.login } != null &&
                studentsInGroup.firstOrNull { it.studentLogin == s.login } != null }

    val students1 = students0.filter { s ->
        forms.first {it.formId == studentsInForm.first { it.login == s.login }.formId }.classNum in (5..8)
    }

    val students2 = students0.filter { s ->
        forms.first {it.formId == studentsInForm.first { it.login == s.login }.formId }.classNum  in (9..11)
    }

    val iterationsMode = listOf(
        IterationsMode(
            students = students0,
            r = RTables(
                weekT = RatingWeek0Table,
                moduleT = RatingModule0Table,
                yearT = RatingYear0Table
            )
        ),
        IterationsMode(
            students = students1,
            r = RTables(
                weekT = RatingWeek1Table,
                moduleT = RatingModule1Table,
                yearT = RatingYear1Table
            )
        ),
        IterationsMode(
            students = students2,
            r = RTables(
                weekT = RatingWeek2Table,
                moduleT = RatingModule2Table,
                yearT = RatingYear2Table
            )
        ),
    )

    iterationsMode.forEach { x ->
        for (s in x.students) {
            val groupList = studentsInGroup.filter { it.studentLogin == s.login }
            val subjectList = subjects.filter {
                it.id in groupList.map { l -> l.subjectId }
            }
            val form =
                forms.first { studentsInForm.first { it.login == s.login }.formId == it.formId }

            val items = initItems(s.login, x.r)

            for (i in subjectList) {
                val stupsWeekCount =
                    Stups.fetchForAWeek(login = s.login).filter { it.subjectId == i.id }
                val stupsYearCount =
                    Stups.fetchForUser(login = s.login).filter { it.subjectId == i.id }

                val weekAvg = Marks.fetchWeekSubjectAVG(login = s.login, subjectId = i.id)
                val yearAvg = Marks.fetchYearSubjectAVG(login = s.login, subjectId = i.id)

                items.pAdd(
                    subjectId = i.id,
                    stups = stupsWeekCount,
                    avg = weekAvg,
                    table = x.r.weekT
                )
                items.pAdd(
                    subjectId = i.id,
                    stups = stupsYearCount,
                    avg = yearAvg,
                    table = x.r.yearT
                )
            }

            for (y in items) {
                y.table.insert(
                    RatingTableDTO(
                        login = s.login,
                        name = s.name,
                        surname = s.surname,
                        praname = s.praname,
                        avatarId = s.avatarId,
                        stups = y.stups,
                        avg = y.avg,
                        top = 0,
                        groupName = if(y.subjectId > 0) groups.first { it.id == groupList.first { it.subjectId == y.subjectId }.groupId }.name else "Общий рейтинг",
                        formNum = form.classNum,
                        subjectId = y.subjectId,
                        formShortTitle = form.shortTitle
                    )
                )
            }

        }
    }
    sortRatings()
}

private fun sortRatings() {
    for (i in listOf(
        RatingWeek0Table,
        RatingWeek1Table,
        RatingWeek2Table,
        RatingModule0Table,
        RatingModule1Table,
        RatingModule2Table,
        RatingYear0Table,
        RatingYear1Table,
        RatingYear2Table,
    )) {
        var top = 0
        var previousSubjectId = 0
        val items = i.fetchAllRatings().filter { it.stups > 0 && it.avg.toFloat() > 2 }.sortedBy { it.stups }.sortedBy { it.subjectId }.reversed().map { x ->
            if(previousSubjectId != x.subjectId) {
                previousSubjectId = x.subjectId
                top = 0
            }
            top++
            x.copy(top = top)

        }
        transaction {
            i.deleteAll()
            i.saveRatings(items)
        }
    }
}


// It's easier....
object RatingWeek0Table : RatingTable()
object RatingWeek1Table : RatingTable()
object RatingWeek2Table : RatingTable()

object RatingModule0Table : RatingTable()
object RatingModule1Table : RatingTable()
object RatingModule2Table : RatingTable()

object RatingYear0Table : RatingTable()
object RatingYear1Table : RatingTable()
object RatingYear2Table : RatingTable()