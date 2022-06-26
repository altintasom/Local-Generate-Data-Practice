package com.altintasomer.scorpcase.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.altintasomer.scorpcase.databinding.LayoutPersonItemBinding
import com.altintasomer.scorpcase.model.Person

private const val TAG = "PersonListAdapter"

class PersonListAdapter : RecyclerView.Adapter<PersonListAdapter.PersonListViewHolder>() {


    class PersonListViewHolder(val binding: LayoutPersonItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            Log.d(TAG, "areItemsTheSame: ${oldItem.id == newItem.id}")
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return newItem == oldItem
        }

    }


    private val differ = AsyncListDiffer(this, differCallback)


    @SuppressLint("NotifyDataSetChanged")
    fun updateList(personList: List<Person>?) {
        differ.submitList(personList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonListViewHolder {
        val view =
            LayoutPersonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PersonListViewHolder(view).apply {
            setIsRecyclable(false)
        }
    }

    override fun onBindViewHolder(holder: PersonListViewHolder, position: Int) {
        with(holder.binding) {
            person = differ.currentList[position]
            executePendingBindings()
        }
    }


    override fun getItemCount(): Int = differ.currentList.size

}
