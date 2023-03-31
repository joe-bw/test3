package kr.co.sorizava.asrplayer.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ZerothResult {

    @SerializedName("hypotheses")
    @Expose
    private var hypotheses: List<Hypothesis?>? = null

    @SerializedName("final")
    @Expose
    private var _final: Boolean? = null

    fun getHypotheses(): List<Hypothesis?>? {
        return hypotheses
    }

    fun setHypotheses(hypotheses: List<Hypothesis?>?) {
        this.hypotheses = hypotheses
    }

    fun getFinal(): Boolean? {
        return _final
    }

    fun setFinal(_final: Boolean?) {
        this._final = _final
    }
}