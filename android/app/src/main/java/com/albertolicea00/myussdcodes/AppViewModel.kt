package com.albertolicea00.myussdcodes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albertolicea00.myussdcodes.data.CodeStore
import com.albertolicea00.myussdcodes.data.model.AppData
import com.albertolicea00.myussdcodes.data.model.CodeGroup
import com.albertolicea00.myussdcodes.data.model.UssdCode
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val store = CodeStore(application)

    private val _data = MutableStateFlow(AppData())
    val data: StateFlow<AppData> = _data.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = store.load()
            withContext(Dispatchers.Main) { _data.value = loaded }
        }
    }

    private fun update(transform: (AppData) -> AppData) {
        _data.value = transform(_data.value)
        val snapshot = _data.value
        viewModelScope.launch(Dispatchers.IO) { store.save(snapshot) }
    }

    // --- Codes ---

    fun upsertCode(code: UssdCode) = update { data ->
        data.copy(codes = data.codes.filterNot { it.id == code.id } + code)
    }

    fun deleteCode(id: String) = update { data ->
        data.copy(codes = data.codes.filterNot { it.id == id })
    }

    // --- Groups ---

    fun upsertGroup(group: CodeGroup) = update { data ->
        data.copy(groups = data.groups.filterNot { it.id == group.id } + group)
    }

    fun deleteGroup(id: String) = update { data ->
        data.copy(
            groups = data.groups.filterNot { it.id == id },
            codes = data.codes.map { if (it.groupId == id) it.copy(groupId = null) else it }
        )
    }

    // --- Import / reset ---

    /** Parses a collection JSON and merges it in (same-id codes are replaced). */
    fun importCollection(raw: String): Result<Int> = runCatching {
        val collection = store.parseCollection(raw)
        require(collection.codes.isNotEmpty()) { "Collection has no codes" }
        update { data ->
            data.copy(
                codes = data.codes.filterNot { existing ->
                    collection.codes.any { it.id == existing.id }
                } + collection.codes,
                importedCollections = (data.importedCollections + collection.id).distinct()
            )
        }
        collection.codes.size
    }

    fun importFromUrl(url: String, onResult: (Result<Int>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching { URL(url).readText() }
                .mapCatching { raw -> importCollection(raw).getOrThrow() }
            withContext(Dispatchers.Main) { onResult(result) }
        }
    }

    fun resetToSeed() {
        viewModelScope.launch(Dispatchers.IO) {
            val fresh = store.reset()
            withContext(Dispatchers.Main) { _data.value = fresh }
        }
    }
}
