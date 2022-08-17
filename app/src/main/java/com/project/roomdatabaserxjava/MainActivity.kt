package com.project.roomdatabaserxjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.project.roomdatabaserxjava.adapter.StudentAdapter
import com.project.roomdatabaserxjava.data.local.entity.StudentEntity
import com.project.roomdatabaserxjava.ui.StudentViewModel
import androidx.appcompat.widget.SearchView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var viewModel: StudentViewModel
    private lateinit var fabAddStudent: ExtendedFloatingActionButton
    private lateinit var mAdapter: StudentAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mSearchView: SearchView

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    private val editClickListener: (data: StudentEntity) -> Unit = {
        showEditStudentDialog(it)
    }

    private val deleteClickListener: (data: StudentEntity) -> Unit = {
        showDeleteStudentDialog(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mProgressBar = findViewById(R.id.pbStudent)
        mRecyclerView = findViewById(R.id.rvStudents)
        mAdapter = StudentAdapter(editClickListener, deleteClickListener)

        //fab
        fabAddStudent = findViewById(R.id.fab_add)

        //view model
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )
            .get(StudentViewModel::class.java)

        //observe the live data
        observers()

        //clock listener
        fabAddStudent.setOnClickListener {
            showAddStudentDialog()
        }
        //RecyclerView
        mRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = mAdapter
        }
    }

    private fun observers() {

        viewModel.isLoading.observe(this) {
            mProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isError.observe(this) {
            it?.let { msg ->
                showErrorStudentDialog(msg)
            }

        }

        viewModel.isSuccess.observe(this) {
            if (it) {
                showSuccessStudentDialog()
            }
        }

        viewModel.isDeleted.observe(this) {
            if (it) {
                showDeletedStudentDialog()
            }
        }

        viewModel.studentList.observe(this) {
            mAdapter.setList(it)
            //scroll to the position = 0
            mRecyclerView.post {
                mRecyclerView.scrollToPosition(0)
            }
        }


    }

    //show add student dialog
    private fun showAddStudentDialog() {
        val dialog = MaterialDialog(this)
            .cornerRadius(8f)
            .cancelable(false)
            .customView(R.layout.student_view_dialog)

        val customView = dialog.getCustomView()

        dialog.positiveButton(R.string.dialog_save) {
            //set the live data value
            val name = customView.findViewById<TextInputEditText>(R.id.studentName)
            val age = customView.findViewById<TextInputEditText>(R.id.studentAge)
            val subject = customView.findViewById<TextInputEditText>(R.id.studentSubject)
            viewModel.studentName.value = name.text.toString()
            val tempAge = age.text.toString()
            viewModel.studentAge.value = tempAge.toInt()
            viewModel.studentSubject.value = subject.text.toString()
            //insert to db
            viewModel.insert()

        }

        dialog.negativeButton {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showEditStudentDialog(data: StudentEntity) {
        val dialog = MaterialDialog(this)
            .cornerRadius(8f)
            .cancelable(false)
            .customView(R.layout.student_view_dialog)

        val customView = dialog.getCustomView()
        //get the view
        val name = customView.findViewById<TextInputEditText>(R.id.studentName)
        val age = customView.findViewById<TextInputEditText>(R.id.studentAge)
        val subject = customView.findViewById<TextInputEditText>(R.id.studentSubject)
        //set the text value
        name.setText(data.StudentName)
        age.setText(data.Age.toString())
        subject.setText(data.Subject)
        viewModel.studentId.value = data.id
        dialog.positiveButton(R.string.dialog_save) {
            //set the live data value
            viewModel.studentName.value = name.text.toString()
            val tempAge = age.text.toString()
            viewModel.studentAge.value = tempAge.toInt()
            viewModel.studentSubject.value = subject.text.toString()
            //update to db
            viewModel.update()

        }
        dialog.negativeButton {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDeleteStudentDialog(data: StudentEntity) {
        val dialog = MaterialDialog(this)
            .cornerRadius(8f)
            .cancelable(false)
            .title(R.string.dialog_delete_title)
            .message(null, "${getString(R.string.dialog_delete_msg)} : ${data.StudentName}")

        viewModel.studentId.value = data.id

        dialog.positiveButton(R.string.dialog_delete) {
            //set the live data value
            viewModel.studentName.value = data.StudentName
            val tempAge = data.Age.toString()
            viewModel.studentAge.value = tempAge.toInt()
            viewModel.studentSubject.value = data.Subject
            //update to db
            viewModel.delete()

        }
        dialog.negativeButton {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showSuccessStudentDialog() {
        val dialog = MaterialDialog(this)
            .cornerRadius(8f)
            .cancelable(false)
            .title(R.string.dialog_success_title)
            .message(R.string.dialog_success_msg)



        dialog.positiveButton {
            it.dismiss()

        }

        dialog.show()
    }

    private fun showDeletedStudentDialog() {
        val dialog = MaterialDialog(this)
            .cornerRadius(8f)
            .cancelable(false)
            .title(R.string.dialog_deleted_title)
            .message(R.string.dialog_deleted_msg)



        dialog.positiveButton {
            it.dismiss()

        }

        dialog.show()
    }


    private fun showErrorStudentDialog(msg: String) {
        val dialog = MaterialDialog(this)
            .cornerRadius(8f)
            .cancelable(false)
            .title(R.string.dialog_error_title)
            .message(null, msg)



        dialog.positiveButton {
            it.dismiss()

        }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        // init search view
        mSearchView = menu?.findItem(R.id.app_bar_search)?.actionView as SearchView
        //call the search funtion
        search()
        return super.onCreateOptionsMenu(menu)
    }

    private fun search() {
        compositeDisposable.add(
            Observable.create<String> { emitter ->
                mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (!emitter.isDisposed) {
                            emitter.onNext(newText)
                        }
                        return false
                    }
                })
            }
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "Search: $it")
                        viewModel.searchStudent(it)
                    },
                    {
                        Log.e(TAG, "Error: $it")
                    },
                    {
                        Log.d(TAG, "Complete")
                    }
                )
        )

    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

}