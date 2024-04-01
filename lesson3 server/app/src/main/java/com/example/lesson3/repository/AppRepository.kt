package com.example.lesson3.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.lesson3.API.Actions
import com.example.lesson3.API.ListAPI
import com.example.lesson3.API.ListConnection
import com.example.lesson3.API.PostFaculty
import com.example.lesson3.API.PostGroup
import com.example.lesson3.API.PostResult
import com.example.lesson3.API.PostStudent
import com.example.lesson3.MyApplication
import com.example.lesson3.data.Faculties
import com.example.lesson3.data.Faculty
import com.example.lesson3.data.Group
import com.example.lesson3.data.Groups
import com.example.lesson3.data.Student
import com.example.lesson3.data.Students
import com.example.lesson3.database.ListDatabase
import com.example.lesson3.myConsts.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppRepository {
    companion object{
        private var INSTANCE: AppRepository?=null

        fun getInstance(): AppRepository {
            if (INSTANCE==null){
                INSTANCE= AppRepository()
            }
            return INSTANCE?:
            throw IllegalStateException("репозиторий не инициализирован")
        }
    }

    var faculty: MutableLiveData<Faculty> = MutableLiveData()
    var group: MutableLiveData<Group> = MutableLiveData()
    var student: MutableLiveData<Student> = MutableLiveData()

    fun setCurrentFaculty(_faculty: Faculty){
        faculty.postValue(_faculty)
    }

    fun loadData(){
        fetchFaculties()
    }

    fun setCurrentGroup(_group: Group){
        group.postValue(_group)
    }

    val facultyGroups
        get()= listOfGroup.value?.filter { it.facultyID == (faculty.value?.id ?: 0) }?.sortedBy { it.name }?: listOf()


    fun setCurrentStudent(_student: Student){
        student.postValue(_student)
    }

    fun getGroupStudents(groupID: Int) =
        (listOfStudent.value?.filter { it.groupID == groupID }?.sortedBy { it.shortName }?: listOf())

    private val listDB by lazy { OfflineDBRepository(ListDatabase.getDatabase(MyApplication.context).listDAO()) }

    private val myCoroutineScope = CoroutineScope(Dispatchers.Main)

    val listOfFaculty: LiveData<List<Faculty>> = listDB.getFaculty().asLiveData()
    val listOfGroup: LiveData<List<Group>> = listDB.getAllGroups().asLiveData()
    val listOfStudent: LiveData<List<Student>> = listDB.getAllStudents().asLiveData()


    private var listAPI = ListConnection.getClient().create(ListAPI::class.java)

    fun fetchFaculties(){
        listAPI.getFaculties().enqueue(object: Callback<Faculties> {
            override fun onFailure(call: Call<Faculties>, t :Throwable){
                Log.d(TAG,"Ошибка получения списка факультетов", t)
            }
            override fun onResponse(
                call : Call<Faculties>,
                response: Response<Faculties>
            ){
                if (response.code()==200){
                    val faculties= response.body()
                    val items = faculties?.items?:emptyList()
                    Log.d(TAG,"Получен список факультетов $items")
                    myCoroutineScope.launch{
                        listDB.deleteAllFaculty()
                        for (f in items){
                            listDB.insertFaculty(f)
                        }
                    }
                    fetchGroups()
                }
            }
        })
    }

    private fun updateFaculties(postFaculty: PostFaculty){
        listAPI.postFaculty(postFaculty)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response: Response<PostResult>){
                    if (response.code()==200) fetchFaculties()
                }
                override fun onFailure(call:Call<PostResult>,t: Throwable){
                    Log.d(TAG,"Ошибка записи факультета",t)
                }
            })
    }

    fun addFaculty(faculty: Faculty){
        updateFaculties(PostFaculty(Actions.APPEND_FACULTY.code,faculty))
    }

    fun updateFaculty(faculty: Faculty){
        updateFaculties(PostFaculty(Actions.UPDATE_FACULTY.code,faculty))
    }

    fun deleteFaculty(faculty: Faculty){
        updateFaculties(PostFaculty(Actions.DELETE_FACULTY.code,faculty))
    }

    fun fetchGroups(){
        listAPI.getGroups().enqueue(object: Callback<Groups> {
            override fun onFailure(call: Call<Groups>, t: Throwable) {
                Log.d(TAG, "Ошибка получения списка групп", t)
            }

            override fun onResponse(
                call: Call<Groups>,
                response: Response<Groups>
            ) {
                if (response.code() == 200) {
                    val groups = response.body()
                    val items = groups?.items ?: emptyList()
                    Log.d(TAG, "Получен список групп $items")
                    myCoroutineScope.launch {
                        listDB.deleteAllGroups()
                        for (g in items) {
                            listDB.insertGroup(g)
                        }
                    }
                    fetchStudents()
                }
            }
        })
    }

    private fun updateGroups(postGroup: PostGroup){
        listAPI.postGroup(postGroup)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchGroups()
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка записи группы", t)
                }
            })
    }

    fun addGroup(group: Group){
        updateGroups(PostGroup(Actions.APPEND_GROUP.code,group))
    }

    fun updateGroup(group: Group){
        updateGroups(PostGroup(Actions.UPDATE_GROUP.code,group))
    }

    fun deleteGroup(group: Group){
        updateGroups(PostGroup(Actions.DELETE_GROUP.code,group))
    }

    fun fetchStudents(){
        listAPI.getStudents().enqueue(object : Callback<Students>{
            override fun onFailure(call:Call<Students>,t : Throwable){
                Log.d(TAG,"Ошибка получения списка студентов",t)
            }
            override fun onResponse(
                call:Call<Students>,
                response: Response<Students>
            ){
                if(response.code()==200){
                    val students = response.body()
                    val items = students?.items?: emptyList()
                    Log.d(TAG,"Получен список студентов $items")
                    myCoroutineScope.launch {
                        listDB.deleteAllStudents()
                        for (s in items){
                            listDB.insertStudent(s)
                        }
                    }
                }
            }
        })
    }

    private fun updateStudents(postStudent: PostStudent){
        listAPI.postStudent(postStudent)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchStudents()
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка записи студента", t.fillInStackTrace())
                }
            })
    }

    fun addStudent(student: Student){
        updateStudents(PostStudent(Actions.APPEND_STUDENT.code, student))
    }

    fun updateStudent(student: Student){
        updateStudents(PostStudent(Actions.UPDATE_STUDENT.code,student))
    }

    fun deleteStudent(student: Student){
        updateStudents(PostStudent(Actions.DELETE_STUDENT.code,student))
    }

}
