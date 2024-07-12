package com.tv.ads

import android.text.TextUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.Crypto
import com.facebook.crypto.CryptoConfig
import com.facebook.crypto.Entity
import com.facebook.crypto.keychain.KeyChain
import io.fastkv.FastKV
object FastKVExt {
    private val fastKV by lazy {
        FastKV.Builder(Utils.getApp().filesDir.absolutePath,"hawk").build()
    }
    private var keyChain: KeyChain = SharedPrefsBackedKeyChain(Utils.getApp(), CryptoConfig.KEY_256)
    private var crypto: Crypto = AndroidConceal.get().createDefaultCrypto(keyChain)
    private fun getEncrptData(key: String?): Pair<Entity, ByteArray> {
        val entity = Entity.create(key)
        val origin = fastKV.getArray(key)
        return Pair(entity, origin)
    }
    @JvmStatic
    fun putInt(key: String?, value: Int?) {
        val entity = Entity.create(key)
        val text = crypto.encrypt(ByteUtils.Int2ByteArray(value ?: 0), entity)
        fastKV.putArray(key, text)
    }

    @JvmStatic
    fun getInt(key: String?, default: Int?): Int {
        val (entity, originBytes) = getEncrptData(key)
        if (originBytes.isEmpty()) {
            return default ?: 0
        }
        return ByteUtils.ByteArray2Int(crypto.decrypt(originBytes, entity))
    }


    @JvmStatic
    fun putString(key: String?, value: String?) {
        val entity = Entity.create(key)
        val text = crypto.encrypt(ConvertUtils.string2Bytes(value, "UTF-8"), entity)
        fastKV.putArray(key, text)
    }

    @JvmStatic
    fun getString(key: String?, default: String? = null): String? {
        val (entity, originBytes) = getEncrptData(key)
        if (originBytes.isEmpty()) {
            return default
        }
        val res = crypto.decrypt(originBytes, entity)
        return ConvertUtils.bytes2String(res)
    }

    @JvmStatic
    fun putBoolean(key: String?, value: Boolean? = false) {
        val entity = Entity.create(key)
        val text = crypto.encrypt(ByteUtils.Boolean2ByteArray(value ?: false), entity)
        fastKV.putArray(key, text)
    }

    @JvmStatic
    fun getBoolean(key: String?, default: Boolean?): Boolean {
        val (entity, origin) = getEncrptData(key)
        if (origin.isEmpty()) {
            return default ?: false
        }
        val text = crypto.decrypt(origin, entity)
        return ByteUtils.ByteArray2Boolean(text)
    }


    @JvmStatic
    fun putLong(key: String?, value: Long?) {
        val entity = Entity.create(key)
        val text = crypto.encrypt(ByteUtils.Long2ByteArray(value ?: 0), entity)
        fastKV.putArray(key, text)
    }

    @JvmStatic
    fun getLong(key: String?, default: Long?): Long {
        val (entity, origin) = getEncrptData(key)
        if (origin.isEmpty()) {
            return default ?: 0
        }
        return ByteUtils.ByteArray2Long(crypto.decrypt(origin, entity))
    }

    @JvmStatic
    fun putDouble(key: String?, value: Double?) {
        val entity = Entity.create(key)
        val text = crypto.encrypt(ByteUtils.double2Bytes(value ?: 0.0), entity)
        fastKV.putArray(key, text)
    }

    @JvmStatic
    fun getDouble(key: String?, default: Double?): Double {
        val (entity, origin) = getEncrptData(key)
        if (origin.isEmpty()) {
            return default ?: 0.0
        }
        return ByteUtils.bytes2Double(crypto.decrypt(origin, entity))
    }

    @JvmStatic
    fun putFloat(key: String?, value: Float?) {
        val entity = Entity.create(key)
        val text = crypto.encrypt(ByteUtils.Float2ByteArray(value ?: 0f), entity)
        fastKV.putArray(key, text)
    }

    @JvmStatic
    fun getFloat(key: String, default: Float? = 0f): Float {
        val (entity, origin) = getEncrptData(key)
        if (origin.isEmpty()) {
            return default ?: 0f
        }
        return ByteUtils.ByteArray2Float(crypto.decrypt(origin, entity))
    }

    @JvmStatic
    fun <T> putList(key: String?, value: List<T>?) {
        val data = GsonUtils.toJson(value)
        putString(key, data)
    }

    @JvmStatic
    inline fun <reified T> getList(key: String?): MutableList<T> {
        val str = getString(key)
        return GsonUtils.fromJson(
            str, GsonUtils.getListType(T::class.java)
        )
            ?: return mutableListOf()
    }


    @JvmStatic
    fun <T> putObj(key: String?, value: T?) {
        putString(key, GsonUtils.toJson(value))
    }

    @JvmStatic
    inline fun <reified T> getObj(key: String?, default: T): T {
        val tmpStr = getString(key)
        if (tmpStr?.isEmpty() == true) {
            return default
        }
        return GsonUtils.fromJson(tmpStr, T::class.java)
    }

    inline fun <reified T> getMapList(key: String?): HashMap<String?, ArrayList<T>>? {
        val tmpStr = getString(key, "")
        return if (TextUtils.isEmpty(tmpStr)) {
            HashMap()
        } else GsonUtils.fromJson(
            tmpStr, GsonUtils.getType(
                HashMap::class.java, String::class.java, ArrayList::class.java, T::class.java
            )
        )
    }

    inline fun <reified T> getMapData(key: String?): HashMap<String?, T>? {
        val tmpStr = getString(key, "")
        return if (TextUtils.isEmpty(tmpStr)) {
            HashMap()
        } else GsonUtils.fromJson(tmpStr, GsonUtils.getType(HashMap::class.java, String::class.java, T::class.java))
    }

    @JvmStatic
    fun remove(key: String?) {
        fastKV.remove(key)
    }

    @JvmStatic
    fun getAll(): MutableMap<String, Any>? {
        return fastKV.all
    }

    @JvmStatic
    fun putAll(map: Map<String, Any>) {
        fastKV.putAll(map)
    }


    @JvmStatic
    fun removeAll(exceptKey: String? = "") {
        if (exceptKey.isNullOrEmpty()) {
            fastKV.clear()
            return
        }
        val iterator = fastKV.all.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().key != exceptKey) {
                iterator.remove()
            }
        }
    }


    @JvmStatic
    fun put(key: String?, any: Any?) {
        when (any) {
            is String -> {
                putString(key, any)
            }

            is Int -> {
                putInt(key, any)
            }

            is Double -> {
                putDouble(key, any)
            }

            is Long -> {
                putLong(key, any)
            }

            is Float -> {
                putFloat(key, any)
            }

            is Boolean -> {
                putBoolean(key, any)
            }

            is List<*> -> {
                putList(key, any)
            }

            else -> {
                putObj(key, any)
            }
        }
    }


}