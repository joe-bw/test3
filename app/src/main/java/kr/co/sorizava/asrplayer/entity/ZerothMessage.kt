package kr.co.sorizava.asrplayer.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ZerothMessage {


    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("segment")
    @Expose
    var segment: Int? = null

    @SerializedName("result")
    @Expose
    var result: ZerothResult? = null

    @SerializedName("segment-start")
    @Expose
    var segmentStart: Double? = null

    @SerializedName("segment-length")
    @Expose
    var segmentLength: Double? = null

    @SerializedName("total-length")
    @Expose
    var totalLength: Double? = null

    @SerializedName("bayes-risk")
    @Expose
    var bayesRisk: Double? = null

    //20220722 cbw 화자분리를 위해 수정
    @SerializedName("speaker")
    @Expose
    private var speakerNum: Int? = null

    @JvmName("getStatus1")
    fun getStatus(): Int? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Int?) {
        this.status = status
    }

    @JvmName("getSegment1")
    fun getSegment(): Int? {
        return segment
    }

    @JvmName("setSegment1")
    fun setSegment(segment: Int?) {
        this.segment = segment
    }

    @JvmName("getResult1")
    fun getResult(): ZerothResult? {
        return result
    }

    @JvmName("setResult1")
    fun setResult(result: ZerothResult?) {
        this.result = result
    }

    @JvmName("getSegmentStart1")
    fun getSegmentStart(): Double? {
        return segmentStart
    }

    @JvmName("setSegmentStart1")
    fun setSegmentStart(segmentStart: Double?) {
        this.segmentStart = segmentStart
    }

    @JvmName("getSegmentLength1")
    fun getSegmentLength(): Double? {
        return segmentLength
    }

    @JvmName("setSegmentLength1")
    fun setSegmentLength(segmentLength: Double?) {
        this.segmentLength = segmentLength
    }

    @JvmName("getTotalLength1")
    fun getTotalLength(): Double? {
        return totalLength
    }

    @JvmName("setTotalLength1")
    fun setTotalLength(totalLength: Double?) {
        this.totalLength = totalLength
    }

    @JvmName("getBayesRisk1")
    fun getBayesRisk(): Double? {
        return bayesRisk
    }

    @JvmName("setBayesRisk1")
    fun setBayesRisk(bayesRisk: Double?) {
        this.bayesRisk = bayesRisk
    }

    @JvmName("speakerNum1")
    fun setSpeakerNum(speakerNum: Int?) {
        this.speakerNum = speakerNum
    }

    @JvmName("speakerNum1")
    fun getSpeakerNum(): Int? {
        return this.speakerNum
    }
}
