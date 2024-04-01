package com.example.lesson3.API

import com.example.lesson3.data.Faculties
import com.example.lesson3.data.Groups
import com.example.lesson3.data.Students
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

enum class Actions(val code: Int) {
    APPEND_FACULTY(11),
    UPDATE_FACULTY(12),
    DELETE_FACULTY(13),
    APPEND_GROUP(21),
    UPDATE_GROUP(22),
    DELETE_GROUP(23),
    APPEND_STUDENT(31),
    UPDATE_STUDENT(32),
    DELETE_STUDENT(33)
}

interface ListAPI{
    @GET("?code=10")
    fun getFaculties(): Call<Faculties>

    @Headers("Content-Type: application/json")
    @POST("faculty")
    fun postFaculty(@Body postFaculty: PostFaculty): Call<PostResult>

    @GET("?code=20")
    fun getGroups(): Call<Groups>

    @Headers("Content-Type: application/json")
    @POST("group")
    fun postGroup(@Body postGroup: PostGroup): Call<PostResult>

    @GET("?code=30")
    fun getStudents(): Call<Students>

    @Headers("Content-Type: application/json")
    @POST("student")
    fun postStudent(@Body postStudent: PostStudent): Call<PostResult>
}
