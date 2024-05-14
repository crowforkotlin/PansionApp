package com.nevrozq.pansion.database.ratingEntities

import com.nevrozq.pansion.database.reportHeaders.ReportHeaders
import com.nevrozq.pansion.database.studentLines.StudentLines
import com.nevrozq.pansion.database.studentLines.StudentLinesDTO
import com.nevrozq.pansion.database.subjects.Subjects
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import server.daysShift
import server.getWeekDays
import server.twoNums

open class RatingEntity : Table() {
    val groupId = this.integer("groupId")
    val subjectId = this.reference("subjectId", Subjects.id)
    val reportId = this.integer("reportId")
    val login = this.varchar("login", 30)
    val content = this.varchar("content", 5)
    val reason = this.varchar("reason", 5)
    val id = this.integer("id")
    val part = this.varchar("part", 1)
    private val date = this.varchar("date", 10)
    val isGoToAvg = this.bool("isGoToAvg")

    fun delete(id: Int, reportId: Int) {
        try {
            transaction {
                this@RatingEntity.deleteWhere {
                    (this@RatingEntity.reportId eq reportId) and (this@RatingEntity.id eq id)
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun insert(r: RatingEntityDTO, isDelete: Boolean) {
        try {
            if (isDelete) {
                this.delete(id = r.id, reportId = r.reportId)
            }
            transaction {
                this@RatingEntity.insert {
                    it[groupId] = r.groupId
                    it[reportId] = r.reportId
                    it[login] = r.login
                    it[content] = r.content
                    it[reason] = r.reason
                    it[id] = r.id
                    it[part] = r.part
                    it[isGoToAvg] = r.isGoToAvg
                    it[subjectId] = r.subjectId
                    it[date] = r.date
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    fun fetchForAWeek(login: String) : List<RatingEntityDTO> {
        val dayStrings = getWeekDays()
        return fetchForPeriod(login, dayStrings)
    }

    fun fetchForPeriod(login: String, period: List<String>) : List<RatingEntityDTO>  {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.login eq login) and (this@RatingEntity.date inList period) }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForReportQuarters(reportId: Int, quartersNum: String) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.reportId eq reportId) and (this@RatingEntity.part inList quartersNum.map { it.toString() }) }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForReport(reportId: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { this@RatingEntity.reportId eq reportId }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchUserByDate(login: String, date: String) : List<RatingEntityDTO> {
        return transaction {
            try {

                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.login eq login) and (this@RatingEntity.isGoToAvg eq true) and (this@RatingEntity.date eq date) }.reversed()
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[this@RatingEntity.date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }


    fun fetchRecentForUser(login: String, limit: Int) : List<RatingEntityDTO> {
        return transaction {
            try {

                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.login eq login) and (this@RatingEntity.isGoToAvg eq true) }.reversed()

                val n = if(limit > ratingEntities.size) ratingEntities.size else limit
                ratingEntities.slice(0..n-1).map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForUser(login: String) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { this@RatingEntity.login eq login }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForUserQuarters(login: String, quartersNum: String) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.login eq login) and (this@RatingEntity.part inList quartersNum.map { it.toString() }) }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[subjectId],
                        date = it[date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForUserSubjectQuarter(login: String, subjectId: Int, quartersNum: String) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.login eq login) and (this@RatingEntity.subjectId eq subjectId)  and (this@RatingEntity.part inList quartersNum.map { it.toString() }) }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[this@RatingEntity.subjectId],
                        date = it[date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }

    fun fetchForUserSubject(login: String, subjectId: Int) : List<RatingEntityDTO> {
        return transaction {
            try {
                val ratingEntities =
                    this@RatingEntity.select { (this@RatingEntity.login eq login) and (this@RatingEntity.subjectId eq subjectId) }
                ratingEntities.map {
                    RatingEntityDTO(
                        groupId = it[groupId],
                        reportId = it[this@RatingEntity.reportId],
                        login = it[this@RatingEntity.login],
                        content = it[content],
                        reason = it[reason],
                        id = it[this@RatingEntity.id],
                        part = it[part],
                        isGoToAvg = it[isGoToAvg],
                        subjectId = it[this@RatingEntity.subjectId],
                        date = it[date]
                    )
                }
            } catch (e: Throwable) {
                println(e)
                listOf()
            }
        }
    }



}