package kr.co.sorizava.asrplayer.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateDeserializer : JsonDeserializer<Date> {

    private val DATE_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?, //안씀
        context: JsonDeserializationContext? //안씀
    ): Date? {
        var dateDate: Date? = null
        var date = json.asString
        if (date != null && date.length > 0) {
            //Log.d("DATE", "dserialize, date:" + date);
            val dotIndex = date.indexOf('.')
            if (dotIndex > 0) {
                date = date.substring(0, dotIndex)
            }
            //Log.d("DATE", "dserialize, date:" + date);
            val formatter = SimpleDateFormat(DATE_UTC_FORMAT)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            try {
                dateDate = formatter.parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return dateDate
    }
}