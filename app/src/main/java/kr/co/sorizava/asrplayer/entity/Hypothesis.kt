package kr.co.sorizava.asrplayer.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Hypothesis {

    @SerializedName("likelihood")
    @Expose
    private var likelihood: Double? = null

    @SerializedName("transcript")
    @Expose
    private var transcript: String? = null

    @SerializedName("word-alignment")
    @Expose
    private var wordAlignment: List<WordAlignment?>? = null

    fun getLikelihood(): Double? {
        return likelihood
    }

    fun setLikelihood(likelihood: Double?) {
        this.likelihood = likelihood
    }

    fun getTranscript(): String? {
        return transcript
    }

    fun setTranscript(transcript: String?) {
        this.transcript = transcript
    }

    fun getWordAlignment(): List<WordAlignment?>? {
        return wordAlignment
    }

    fun setWordAlignment(wordAlignment: List<WordAlignment?>?) {
        this.wordAlignment = wordAlignment
    }
}