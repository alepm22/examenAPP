package com.example.myapplication.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movies")
class MovieEntity(
    @PrimaryKey()
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "title")
    var title: String,


    @ColumnInfo(name = "description")
    val description: String,


    @ColumnInfo(name = "posterPath")
    val posterPath: String)