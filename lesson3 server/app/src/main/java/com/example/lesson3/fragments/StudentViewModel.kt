package com.example.lesson3.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson3.data.Group
import com.example.lesson3.data.Student
import com.example.lesson3.repository.AppRepository
import java.util.Date

class StudentViewModel : ViewModel() {
    var studentList: MutableLiveData<List<Student>> = MutableLiveData()

    private var _student: Student?= null
    val student
        get()= _student

    var group: Group? = null

    fun set_Group(group: Group) {
        this.group = group
        AppRepository.getInstance().listOfStudent.observeForever{
            studentList.postValue(AppRepository.getInstance().getGroupStudents(group.id))
        }
        AppRepository.getInstance().student.observeForever{
            _student=it
        }
    }

    fun deleteStudent() {
        if(student!=null)
            AppRepository.getInstance().deleteStudent(student!!)
    }

    fun setCurrentStudent(student: Student){
        AppRepository.getInstance().setCurrentStudent(student)
    }

}