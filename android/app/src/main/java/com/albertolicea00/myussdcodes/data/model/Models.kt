package com.albertolicea00.myussdcodes.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VariableType {
    @SerialName("text")
    TEXT,

    @SerialName("number")
    NUMBER,

    @SerialName("phone")
    PHONE
}

/** An input the app asks for right before dialing (fills a `{placeholder}` in the code). */
@Serializable
data class CodeVariable(
    val key: String,
    val label: String,
    val type: VariableType = VariableType.TEXT,
    val hint: String = ""
)

@Serializable
data class UssdCode(
    val id: String,
    val name: String,
    val description: String = "",
    val code: String,
    val category: String = "General",
    val tags: List<String> = emptyList(),
    val variables: List<CodeVariable> = emptyList(),
    val dangerous: Boolean = false,
    val source: String = "",
    val notes: String = "",
    val custom: Boolean = false,
    val groupId: String? = null
) {
    fun matches(query: String): Boolean {
        val q = query.trim()
        if (q.isEmpty()) return true
        return name.contains(q, ignoreCase = true) ||
            code.contains(q, ignoreCase = true) ||
            description.contains(q, ignoreCase = true) ||
            category.contains(q, ignoreCase = true) ||
            tags.any { it.contains(q, ignoreCase = true) }
    }
}

/** A user-created group ("super personalized" collections of codes). */
@Serializable
data class CodeGroup(
    val id: String,
    val name: String,
    val icon: String = "📁"
)

/** A collection as published in the catalog repository. */
@Serializable
data class CodeCollection(
    val id: String,
    val name: String,
    val description: String = "",
    val version: Int = 1,
    val country: String = "",
    val carrier: String = "",
    val language: String = "",
    val codes: List<UssdCode> = emptyList()
)

/** Everything the app persists locally. */
@Serializable
data class AppData(
    val codes: List<UssdCode> = emptyList(),
    val groups: List<CodeGroup> = emptyList(),
    val importedCollections: List<String> = emptyList()
)
