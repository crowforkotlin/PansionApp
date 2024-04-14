package com.nevrozq.pansion.database.ratingEntities

import admin.groups.Group
import admin.groups.GroupInit
import admin.groups.forms.CutedGroup
import com.nevrozq.pansion.database.subjects.Subjects
import journal.init.TeacherGroup
//val groupId = this.integer("groupId")
//    val reportId = this.integer("reportId")
//    val login = this.varchar("login", 30)
//    val content = this.varchar("content", 5)
//    val reason = this.varchar("reason", 5)

data class RatingEntityDTO(
    val groupId: Int,
    val subjectId: Int,
    val reportId: Int,
    val login: String,
    val content: String,
    val reason: String,
    val id: Int,
    val part: String,
    val isGoToAvg: Boolean,
    val date: String
)

//fun GroupDTO.mapToGroup() =
//    Group(
//        id = this.id,
//        group = GroupInit(
//            name = this.name,
//            teacherLogin = this.teacherLogin,
//            subjectId = this.subjectId,
//            difficult = this.difficult
//        ),
//        isActive = this.isActive
//    )
//
//fun GroupDTO.mapToCutedGroup() =
//    CutedGroup(
//        groupId = this.id,
//        groupName = this.name,
//        isActive = this.isActive
//    )
//fun GroupDTO.mapToTeacherGroup() =
//    TeacherGroup(
//        cutedGroup = CutedGroup(
//            groupId = this.id,
//            groupName = this.name,
//            isActive = this.isActive
//        ),
//        subjectId = this.subjectId,
//        subjectName = Subjects.fetchAllSubjects().find { it.id == this.subjectId }?.name ?: "Урок"
//    )