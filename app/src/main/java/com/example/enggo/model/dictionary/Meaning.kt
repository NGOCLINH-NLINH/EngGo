package com.example.enggo.model.dictionary

import com.google.gson.annotations.SerializedName

data class Meaning(
    @SerializedName("def")
    val def: String = "",

    @SerializedName("example")
    val example: String? = null,

    @SerializedName("speech_part")
    val speechPart: String = "",

    @SerializedName("synonyms")
    val synonyms: List<String>? = null,

    @SerializedName("labels")
    val labels: List<Label>? = null,
) {
    constructor() : this("", null, "", null, null)
}

data class Label(
    @SerializedName("name")
    val name: String = "",
){
    constructor() : this("")
}