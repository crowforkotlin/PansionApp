package com.nevrozq.pansion.database.ratingTable

import com.nevrozq.pansion.database.forms.Forms
import com.nevrozq.pansion.database.groups.Groups
import com.nevrozq.pansion.database.ratingEntities.Marks
import com.nevrozq.pansion.database.ratingEntities.RatingEntityDTO
import com.nevrozq.pansion.database.ratingEntities.Stups
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentLines.StudentLinesDTO
import com.nevrozq.pansion.database.subjects.Subjects
import com.nevrozq.pansion.database.users.Users
import com.nevrozq.pansion.utils.toList
import com.nevrozq.pansion.utils.toStr
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import report.RCreateReportReceive
import report.RUpdateReportReceive
import report.ServerRatingUnit

open class RatingTable : Table() {
    //    val id = this.integer("id").autoIncrement().uniqueIndex()
    private val login = varchar("login", 30)
    private val name = varchar("name", 30)
    private val surname = varchar("surname", 50)
    private val praname = this.varchar("praname", 30).nullable()
    private val avatarId = integer("avatarId")
    private val stups = this.integer("stups")
    private val top = this.integer("top")
    private val groupName = this.varchar("groupName", 50)
    private val avg = this.varchar("avg", 4)
    private val formShortTitle = varchar("formShortTitle", 11)
    private val formNum = integer("formNum")
    private val subjectId = integer("subjectId")

    fun insert(i: RatingTableDTO) {
        this@RatingTable.insert {
            it[login] = i.login
            it[name] = i.name
            it[surname] = i.surname
            it[praname] = i.praname
            it[avatarId] = i.avatarId
            it[stups] = i.stups
            it[top] = i.top
            it[groupName] = i.groupName
            it[formNum] = i.formNum
            it[formShortTitle] = i.formShortTitle
            it[avg] = i.avg
            it[subjectId] = i.subjectId
        }

    }

    fun fetchAllRatings(): List<RatingTableDTO> {
        return transaction {
            this@RatingTable.selectAll().map {
                RatingTableDTO(
                    login = it[login],
                    name = it[name],
                    surname = it[surname],
                    praname = it[praname],
                    avatarId = it[avatarId],
                    stups = it[stups],
                    top = it[top],
                    groupName = it[groupName],
                    formNum = it[formNum],
                    formShortTitle = it[formShortTitle],
                    avg = it[avg],
                    subjectId = it[subjectId]
                )
            }
        }
    }
    fun fetchRatingOf(login: String): RatingTableDTO? {
        return transaction {
            this@RatingTable.select{this@RatingTable.login eq login}.map {
                RatingTableDTO(
                    login = it[this@RatingTable.login],
                    name = it[name],
                    surname = it[surname],
                    praname = it[praname],
                    avatarId = it[avatarId],
                    stups = it[stups],
                    top = it[top],
                    groupName = it[groupName],
                    formNum = it[formNum],
                    formShortTitle = it[formShortTitle],
                    avg = it[avg],
                    subjectId = it[subjectId]
                )
            }.firstOrNull()
        }
    }

    fun saveRatings(list: List<RatingTableDTO>) {
        return transaction {
            this@RatingTable.deleteAll()
            list.forEach { i ->
                this@RatingTable.insert {
                    it[login] = i.login
                    it[name] = i.name
                    it[surname] = i.surname
                    it[praname] = i.praname
                    it[avatarId] = i.avatarId
                    it[stups] = i.stups
                    it[top] = i.top
                    it[groupName] = i.groupName
                    it[formNum] = i.formNum
                    it[formShortTitle] = i.formShortTitle
                    it[avg] = i.avg
                    it[subjectId] = i.subjectId
                }
            }
        }
    }

}