package com.example.safeshe.guardians

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.safeshe.database.Guardian
import com.example.safeshe.database.GuardianDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuardianInfoViewModel(
    application: Application): AndroidViewModel(application) {

    private val db:GuardianDb = GuardianDb.getInstance(application)
    internal val allGuardians : LiveData<List<Guardian>> = db.guardianDatabaseDao().getAllGuardians()


    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _showSnackbarEvent = MutableLiveData<Boolean?>()
    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent

    fun insert(guardian: Guardian){
        uiScope.launch {
            withContext(Dispatchers.IO) {
                db.guardianDatabaseDao().insert(guardian)
            }
        }
    }

    fun onClear() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                db.guardianDatabaseDao().clear()
            }
            _showSnackbarEvent.value = true
        }
    }

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
