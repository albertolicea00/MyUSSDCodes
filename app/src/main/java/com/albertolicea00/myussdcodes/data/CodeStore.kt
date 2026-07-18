package com.albertolicea00.myussdcodes.data

import android.content.Context
import com.albertolicea00.myussdcodes.data.model.AppData
import com.albertolicea00.myussdcodes.data.model.CodeCollection
import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** JSON-file persistence plus the bundled seed catalog. */
class CodeStore(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    private val file: File
        get() = File(context.filesDir, FILE_NAME)

    fun load(): AppData {
        val existing = file.takeIf { it.exists() }
        if (existing != null) {
            runCatching { return json.decodeFromString<AppData>(existing.readText()) }
        }
        return seed()
    }

    fun save(data: AppData) {
        file.writeText(json.encodeToString(data))
    }

    /** Wipes local data and reloads the bundled seed collection. */
    fun reset(): AppData {
        file.delete()
        return seed()
    }

    fun parseCollection(raw: String): CodeCollection = json.decodeFromString(raw)

    private fun seed(): AppData {
        val collection = runCatching {
            context.assets.open(SEED_ASSET).bufferedReader().use { it.readText() }
                .let { parseCollection(it) }
        }.getOrNull() ?: return AppData()

        val data = AppData(
            codes = collection.codes,
            importedCollections = listOf(collection.id)
        )
        save(data)
        return data
    }

    private companion object {
        const val FILE_NAME = "app-data.json"
        const val SEED_ASSET = "collections/gsm-standard.json"
    }
}
