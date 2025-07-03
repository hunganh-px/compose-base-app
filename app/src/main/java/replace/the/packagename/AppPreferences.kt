package replace.the.packagename

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter


object AppPreferences {
    val NAME = BuildConfig.APPLICATION_ID
    val MODE = Context.MODE_PRIVATE
    val moshi: Moshi = Moshi.Builder().build()

    lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            NAME,
            MODE
        )
    }


    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T> save(key: String, any: T) {
        preferences.edit {
            when (any) {
                is String -> this.putString(key, any)
                is Float -> this.putFloat(key, any)
                is Int -> this.putInt(key, any)
                is Long -> this.putLong(key, any)
                is Boolean -> this.putBoolean(key, any)
                else -> this.putString(key, moshi.adapter<T>().toJson(any))
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T> saveImmediately(key: String, any: T): Boolean {
        val editor = preferences.edit()
        when (any) {
            is String -> editor?.putString(key, any)
            is Float -> editor?.putFloat(key, any)
            is Int -> editor?.putInt(key, any)
            is Long -> editor?.putLong(key, any)
            is Boolean -> editor?.putBoolean(key, any)
            else -> editor?.putString(key, moshi.adapter<T>().toJson(any))
        }
        return editor?.commit() == true
    }


    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T> get(key: String): T? {
        when (T::class) {
            Float::class -> return preferences.getFloat(key, 0f) as T
            Int::class -> return preferences.getInt(key, 0) as T
            Long::class -> return preferences.getLong(key, 0) as T
            String::class -> return preferences.getString(key, "") as T
            Boolean::class -> return preferences.getBoolean(key, false) as T
            else -> {
                val any = preferences.getString(key, "")
                return moshi.adapter<T>().fromJson(any ?: "")
            }
        }
    }

}