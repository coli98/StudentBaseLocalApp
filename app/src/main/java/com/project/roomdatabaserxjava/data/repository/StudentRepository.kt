package com.project.roomdatabaserxjava.data.repository

import com.project.roomdatabaserxjava.data.local.db.DatabaseService
import com.project.roomdatabaserxjava.data.local.entity.StudentEntity

class StudentRepository(private val databaseService: DatabaseService) {

    fun insert(studentEntity: StudentEntity) = databaseService.studentDao().insert(studentEntity)

    fun update(studentEntity: StudentEntity) = databaseService.studentDao().update(studentEntity)

    fun delete(studentEntity: StudentEntity) = databaseService.studentDao().delete(studentEntity)

    fun getAllStudents() = databaseService.studentDao().getAllStudents()

    fun searchStudent(name:String) = databaseService.studentDao().searchStudent(name)

}