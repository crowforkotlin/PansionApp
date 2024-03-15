package com.nevrozq.pansion.features.lessons

import RequestPaths
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLessonsRouting() {
    routing {
        val lessonsController = LessonsController()
        post(RequestPaths.Lessons.FetchAllSubjects) {
            lessonsController.fetchAllSubjects(call)
        }
        post(RequestPaths.Lessons.CreateSubject) {
            lessonsController.createSubject(call)
        }

        post(RequestPaths.Lessons.FetchGroups) {
            lessonsController.fetchGroups(call)
        }

        post(RequestPaths.Lessons.FetchCutedGroups) {
            lessonsController.fetchCutedGroups(call)
        }

        post(RequestPaths.Lessons.FetchStudentGroups) {
            lessonsController.fetchStudentGroups(call)
        }
        post(RequestPaths.Lessons.FetchTeacherGroups) {
            lessonsController.fetchTeacherGroups(call)
        }

        post(RequestPaths.Lessons.FetchStudentsInGroup) {
            lessonsController.fetchStudentsInGroup(call)
        }

        post(RequestPaths.Lessons.FetchStudentsInForm) {
            lessonsController.fetchStudentsInForm(call)
        }

        post(RequestPaths.Lessons.BindStudentToForm) {
            lessonsController.bindStudentToForm(call)
        }


        post(RequestPaths.Lessons.CreateGroup) {
            lessonsController.createGroup(call)
        }
        post(RequestPaths.Lessons.CreateForm) {
            lessonsController.createForm(call)
        }
        post(RequestPaths.Lessons.CreateFormGroup) {
            lessonsController.createFormGroup(call)
        }

        post(RequestPaths.Lessons.FetchFormGroups) {
            lessonsController.fetchFormGroups(call)
        }

        post(RequestPaths.Lessons.FetchTeachersForGroup) {
            lessonsController.fetchAllTeachersForGroups(call)
        }

        post(RequestPaths.Lessons.FetchMentorsForGroup) {
            lessonsController.fetchAllMentorsForGroups(call)
        }

        post(RequestPaths.Lessons.FetchAllForms) {
            lessonsController.fetchAllForms(call)
        }

    }
}