package kr.co.sorizava.asrplayer.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WordAlignment{

    @SerializedName("start")
    @Expose
    var start: Double? = null

    @SerializedName("length")
    @Expose
    var length: Double? = null

    @SerializedName("word")
    @Expose
    var word: String? = null

    @SerializedName("confidence")
    @Expose
    var confidence: Double? = null

    @JvmName("getStart1")
    fun getStart(): Double? {
        return start
    }

    @JvmName("setStart1")
    fun setStart(start: Double?) {
        this.start = start
    }

    @JvmName("getLength1")
    fun getLength(): Double? {
        return length
    }

    @JvmName("setLength1")
    fun setLength(length: Double?) {
        this.length = length
    }

    @JvmName("getWord1")
    fun getWord(): String? {
        return word
    }

    @JvmName("setWord1")
    fun setWord(word: String?) {
        this.word = word
    }

    @JvmName("getConfidence1")
    fun getConfidence(): Double? {
        return confidence
    }

    @JvmName("setConfidence1")
    fun setConfidence(confidence: Double?) {
        this.confidence = confidence
    }
}