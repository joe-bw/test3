package kr.co.sorizava.asrplayer.media

import kotlin.experimental.and
import kotlin.experimental.or


class Resampler {





    private var printCount: Int = 0
    fun reSample(
        sourceData: ByteArray,
        length: Int,
        bitsPerSample: Int,
        sourceRate: Int,
        targetRate: Int,
    ): ByteArray {

        printCount++
        // make the bytes to amplitudes first
        val bytePerSample = bitsPerSample / 8
        val numSamples = length / bytePerSample
        val amplitudes = ShortArray(numSamples) // 16 bit, use a short to store
        var pointer = 0
        for (i in 0 until numSamples) {
            var amplitude: Short = 0


            for (byteNumber in 0 until bytePerSample) {
                // little endian
                amplitude =
                    amplitude or ((sourceData[pointer++].toUShort() and 0xFF.toUShort()).toUInt() shl (byteNumber * 8)).toShort()
            }
            amplitudes[i] = amplitude
        }


        // do interpolation
        val reSample = LinearInterpolation()
        val targetSample = reSample.interpolate(sourceRate, targetRate, amplitudes)
        val targetLength = targetSample.size
        // end do interpolation

        // TODO: Remove the high frequency signals with a digital filter, leaving a signal containing only half-sample-rated frequency information, but still sampled at a rate of target sample rate. Usually FIR is used

        // end resample the amplitudes

        // convert the amplitude to bytes
        val bytes: ByteArray
        if (bytePerSample == 1) {
            bytes = ByteArray(targetLength)
            for (i in 0 until targetLength) {
                bytes[i] = targetSample[i].toByte()
            }
        } else {
            // suppose bytePerSample==2
            bytes = ByteArray(targetLength * 2)
            for (i in targetSample.indices) {
                // little endian
                bytes[i * 2] = (targetSample[i] and 0xff).toByte()
                bytes[i * 2 + 1] = ((targetSample[i].toInt() shr 8) and 0xff).toByte()
            }
        }

        // end convert the amplitude to bytes
        return bytes
    }
}