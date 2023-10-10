package com.example.project5
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    // String Live Data that is Mutable
    private val _sharedString = MutableLiveData<String>()
    val sharedString: LiveData<String> get() = _sharedString

    // Function to set the string.
    fun setSharedString(value: String) {
        _sharedString.value = value
    }
}