package kr.co.sorizava.asrplayer.media

/**
 * Construct new data points within the range of a discrete set of known data points by linear equation
 *
 * @author Jacquet Wong
 */


/**
 * Construct new data points within the range of a discrete set of known data points by linear equation
 *
 * @author Jacquet Wong
 */
class LinearInterpolation() {

    /**
     * Do interpolation on the samples according to the original and destinated sample rates
     *
     * @param oldSampleRate    sample rate of the original samples
     * @param newSampleRate    sample rate of the interpolated samples
     * @param samples    original samples
     * @return interpolated samples
     */
    fun interpolate(oldSampleRate: Int, newSampleRate: Int, samples: ShortArray): ShortArray {
        if (oldSampleRate == newSampleRate) {
            return samples
        }
        val newLength = Math.round(samples.size.toFloat() / oldSampleRate * newSampleRate)
        val lengthMultiplier = newLength.toFloat() / samples.size
        val interpolatedSamples = ShortArray(newLength)

        // interpolate the value by the linear equation y=mx+c
        for (i in 0 until newLength) {

            // get the nearest positions for the interpolated point
            val currentPosition = i / lengthMultiplier
            val nearestLeftPosition = currentPosition.toInt()
            var nearestRightPosition = nearestLeftPosition + 1
            if (nearestRightPosition >= samples.size) {
                nearestRightPosition = samples.size - 1
            }
            val slope =
                (samples[nearestRightPosition] - samples[nearestLeftPosition]).toFloat() // delta x is 1
            val positionFromLeft = currentPosition - nearestLeftPosition
            interpolatedSamples[i] =
                (slope * positionFromLeft + samples[nearestLeftPosition]).toInt()
                    .toShort() // y=mx+c
        }
        return interpolatedSamples
    }
}