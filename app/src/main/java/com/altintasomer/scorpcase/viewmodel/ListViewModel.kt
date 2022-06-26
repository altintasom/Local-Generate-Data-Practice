package com.altintasomer.scorpcase.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altintasomer.scorpcase.model.DataSource
import com.altintasomer.scorpcase.model.FetchCompletionHandler
import com.altintasomer.scorpcase.model.FetchError
import com.altintasomer.scorpcase.model.Person
import com.altintasomer.scorpcase.utils.Event
import com.altintasomer.scorpcase.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

private const val TAG = "ListViewModel"

@HiltViewModel
class ListViewModel @Inject constructor(private val dataSource: DataSource) : ViewModel() {


    private val _personList = MutableLiveData<Event<Resource<List<Person>>>>()
    val personList: LiveData<Event<Resource<List<Person>>>> get() = _personList

    private val readyPersonList = arrayListOf<Person>()

    private var next: String? = null

    private var _isLoading = false
    val isLoading get() = _isLoading
    private var _isLastPage = false
    val isLastPage get() = _isLastPage

    var isScrolling = false

    private val fetchCompletionHandler: FetchCompletionHandler = { fetchResponse, fetchError ->
        _isLoading = false
        fetchResponse?.let { it ->
            if (it.next == null) _isLastPage = true
            it.next?.let { rNext ->
                next = rNext
            }

            it.people.forEach { rePerson ->
                if (!readyPersonList.map { it.id }.contains(rePerson.id)) {
                    readyPersonList.add(rePerson)
                }
            }
            _personList.postValue(Event(Resource.success(readyPersonList)))
        }
        fetchError?.let {
            _personList.postValue(Event(Resource.error(it.errorDescription)))
        }

    }

    init {
        initializeData()
    }

    fun getPerson() {
        viewModelScope.launch(Dispatchers.IO) {
            _personList.postValue(Event(Resource.loading()))
            _isLoading = true
            dataSource.fetch(next = next.toString(), fetchCompletionHandler)
        }
    }


    fun swipeRefresh() {
        if (!_isLoading){
            readyPersonList.clear()
            next = null
            _isLastPage = false
            initializeData()
        }
    }

    private fun initializeData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading = true
            _personList.postValue(Event(Resource.loading()))
            dataSource.fetch(next = null, fetchCompletionHandler)
        }
    }
}