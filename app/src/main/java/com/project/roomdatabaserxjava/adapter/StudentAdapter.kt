package com.project.roomdatabaserxjava.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.project.roomdatabaserxjava.R
import com.project.roomdatabaserxjava.data.local.entity.StudentEntity

class StudentAdapter(private val editClickListener: (data: StudentEntity) -> Unit,
                     private val deleteClickListener: (data: StudentEntity) -> Unit) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<StudentEntity>() {
        override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_list_view, parent, false)

        return StudentViewHolder(view, editClickListener,deleteClickListener)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    fun setList(newItem: List<StudentEntity>) {
        asyncListDiffer.submitList(newItem)

    }

    class StudentViewHolder(
        itemView: View,
        private val editClickListener: (data: StudentEntity) -> Unit,
        private val deleteClickListener: (data: StudentEntity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val id = itemView.findViewById<TextView>(R.id.lbStudentId)
        private val name = itemView.findViewById<TextView>(R.id.lbStudentName)
        private val age = itemView.findViewById<TextView>(R.id.lbStudentAge)
        private val subject = itemView.findViewById<TextView>(R.id.lbStudentSubject)
        private val btnEdit = itemView.findViewById<ShapeableImageView>(R.id.btnEdit)
        private val btnDelete = itemView.findViewById<ShapeableImageView>(R.id.btnDelete)

        fun bind(data: StudentEntity) {

            id.text = data.id.toString()
            name.text = data.StudentName
            age.text = data.Age.toString()
            subject.text = data.Subject

            btnEdit.setOnClickListener {
                editClickListener(data)

            }
            btnDelete.setOnClickListener {
                deleteClickListener(data)

            }

        }

    }

}