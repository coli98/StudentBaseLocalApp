package com.project.roomdatabaserxjava.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "student_name")
    val StudentName: String,
    @ColumnInfo(name = "age")
    val Age: Int,
    @ColumnInfo(name = "subject")
    val Subject: String
)
