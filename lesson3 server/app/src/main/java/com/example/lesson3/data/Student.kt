package com.example.lesson3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "students",
        indices = [Index("id"), Index("group_id", "id")],
        foreignKeys = [
            ForeignKey(
                entity = Group::class,
                parentColumns = ["id"],
                childColumns = ["group_id"],
                onDelete = ForeignKey.CASCADE
            )
        ]
)

data class Student(
    @SerializedName("id") @PrimaryKey val id: Int=0, //UUID = UUID.randomUUID(),
    @SerializedName("lastName") var lastname: String="",
    @SerializedName("firstName") var firstname: String="",
    @SerializedName("middleName") var middlename: String="",
    @SerializedName("birthDate") @ColumnInfo(name= "birth_date") var birthDate: Date=Date(),
    @SerializedName("groupID") @ColumnInfo(name="group_id") var groupID: Int=0, //UUID?=null,
    @SerializedName("phone") var phone: String="",
    @SerializedName("sex") var sex : Int=0
){
    val shortName
        get()=lastname + (if(firstname.isNotEmpty())
        {" ${firstname[0]}."}else "") +
                (if(middlename.isNotEmpty()) {"${middlename[0]}."}else "")
}
