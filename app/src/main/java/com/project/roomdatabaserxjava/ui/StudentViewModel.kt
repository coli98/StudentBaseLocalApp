package com.project.roomdatabaserxjava.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.project.roomdatabaserxjava.data.local.db.DatabaseService
import com.project.roomdatabaserxjava.data.local.entity.StudentEntity
import com.project.roomdatabaserxjava.data.repository.StudentRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "StudentViewModel"
    }

    private val studentRepository = StudentRepository(DatabaseService.getInstance(application))
    private val compositeDisposable = CompositeDisposable()

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isError: MutableLiveData<String> = MutableLiveData()
    val isSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val studentId: MutableLiveData<Long> = MutableLiveData()
    val studentName: MutableLiveData<String> = MutableLiveData<String>()
    val studentAge: MutableLiveData<Int> = MutableLiveData()
    val studentSubject: MutableLiveData<String> = MutableLiveData()
    val studentList: MutableLiveData<List<StudentEntity>> = MutableLiveData()

    init {
        getAllStudents()
    }

    fun insert() {
        //show progress bar
        isLoading.value = true

        compositeDisposable.add(
            studentRepository.insert(createInsertStudentEntity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "Insert: $it")
                        //hide progress bar
                        isLoading.value = false
                        //notify success
                        isSuccess.value = true
                    },
                    {
                        Log.d(TAG, it.toString())
                        isError.value = it.message
                        isLoading.value = false
                    }
                )
        )
    }

    fun update() {
        //show progress bar
        isLoading.value = true
        compositeDisposable.add(
            studentRepository.update(createStudentEntity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "Update: $it")
                        //hide progress bar
                        isLoading.value = false
                        //notify success
                        isSuccess.value = true
                    },
                    {
                        Log.d(TAG, it.toString())
                        isError.value = it.message
                        isLoading.value = false
                    }
                )
        )
    }

    fun delete() {
        //show progress bar
        isLoading.value = true
        compositeDisposable.add(
            studentRepository.delete(createStudentEntity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "Delete: $it")
                        //hide progress bar
                        isLoading.value = false
                        //notify success
                        isDeleted.value = true
                    },
                    {
                        Log.d(TAG, it.toString())
                        isError.value = it.message
                        isLoading.value = false
                        isDeleted.value = false
                    }
                )
        )
    }

    private fun getAllStudents() {
        //show progress bar
        isLoading.value = true
        compositeDisposable.add(
            studentRepository.getAllStudents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "Students: $it")
                        studentList.value = it
                        isLoading.value = false
                    },
                    {
                        Log.d(TAG, it.toString())
                        isError.value = it.message
                        isLoading.value = false
                    }
                )
        )
    }

    fun searchStudent(name:String) {
        //show progress bar
        isLoading.value = true
        compositeDisposable.add(
            studentRepository.searchStudent(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "Students: $it")
                        studentList.value = it
                        isLoading.value = false
                    },
                    {
                        Log.d(TAG, it.toString())
                        isError.value = it.message
                        isLoading.value = false
                    }
                )
        )
    }

    private fun createInsertStudentEntity(): StudentEntity {
        return StudentEntity(
            StudentName = studentName.value.toString(),
            Age = studentAge.value!!,
            Subject = studentSubject.value.toString()

        )
    }

    private fun createStudentEntity(): StudentEntity {
        return StudentEntity(
            id = studentId.value!!,
            StudentName = studentName.value.toString(),
            Age = studentAge.value!!,
            Subject = studentSubject.value.toString()

        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}