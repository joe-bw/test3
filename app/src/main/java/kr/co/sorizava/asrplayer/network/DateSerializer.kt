package kr.co.sorizava.asrplayer.network

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kr.co.sorizava.asrplayer.ZerothDefine
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DateSerializer : JsonSerializer<Date>{

    override fun serialize(
        src: Date?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val formatter = SimpleDateFormat(ZerothDefine.DATE_UTC_FORMAT)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return JsonPrimitive(formatter.format(src))
    }
}