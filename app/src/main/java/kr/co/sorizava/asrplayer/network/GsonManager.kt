package kr.co.sorizava.asrplayer.network

import android.net.Uri
import android.os.Bundle
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import kr.co.sorizava.asrplayer.network.DateDeserializer
import kr.co.sorizava.asrplayer.network.DateSerializer
import kr.co.sorizava.asrplayer.network.UriDeserializer
import kr.co.sorizava.asrplayer.network.UriSerializer
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*


class GsonManager {

    companion object
    {

        fun toJson(): String? {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
            return gson.toJson(this)
        }

        fun toJson(T: Any?): String {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
            return gson.toJson(T)
        }

        fun toArrayJson(T: ArrayList<*>?): String? {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
            return gson.toJson(T)
        }

        fun fromArrayJson(type: Type?, json: String?): Any? {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriDeserializer())
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .create()
            return gson.fromJson(json, type)
        }

        fun fromJSon(T: Class<*>?, json: String?): Any? {
            return try {
                val gson = GsonBuilder().serializeNulls()
                    .registerTypeAdapter(Uri::class.java, UriDeserializer())
                    .registerTypeAdapter(Date::class.java, DateDeserializer())
                    .create()
                gson.fromJson<Any>(json, T)
            } catch (e: Exception) {
                null
            }
        }

        fun fromJSon(T: Type?, json: String?): Any? {
            return try {
                val gson = GsonBuilder().serializeNulls()
                    .registerTypeAdapter(Uri::class.java, UriDeserializer())
                    .registerTypeAdapter(Date::class.java, DateDeserializer())
                    .create()
                gson.fromJson<Any>(json, T)
            } catch (e: Exception) {
                null
            }
        }


        fun toBundle(json: String?): Bundle? {
            val savedBundle = Bundle()
            savedBundle.putString("toBundle", json)
            return savedBundle
        }

        fun toMap(obj: Any?): Map<String, Any>? {
            val map: MutableMap<String, Any> = HashMap()
            if (obj == null) return map
            try {
                for (f in obj.javaClass.declaredFields) {
                    if (f.isAnnotationPresent(Expose::class.java)) {
                        f.isAccessible = true
                        if (f[obj] != null) map[f.name] = f[obj]
                    }
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return map
        }

        fun <T> objectFromHashMap(hashMap: Any?, tClass: Class<T>?): T? {
            val jsonString = toJson(hashMap)
            return fromJSon(tClass, jsonString) as T?
        }

        fun getJsonString(json: JSONObject, key: String?): String? {
            var outValue: String? = null
            // key가 존재하는지 체크한다.
            if (json.has(key)) {
                try {
                    outValue = json.getString(key)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return outValue
        }
    }
}