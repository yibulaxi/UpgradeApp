package com.velkonost.upgrade.rest

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import java.io.InputStream
import java.util.*

/** Utility class for converting to and from json any object  */
class Json {
    val objectMapper = createObjectMapper()

    fun collectionType(
        collectionClass: Class<out Collection<*>?>?,
        elementClass: Class<*>?
    ): CollectionType {
        return objectMapper.typeFactory.constructCollectionType(collectionClass, elementClass)
    }

    /** Parse and extract a plain object by the key  */
    fun <T> toObject(data: String?, key: String?, clazz: Class<T>): T? {
        if (data == null) return null
        require(!clazz.isAssignableFrom(MutableCollection::class.java)) { "Unsupported Collection subclasses for that you must to use 'T toObject(String data, String key, JavaType typeToken)'" }
        return try {
            val jsonNode = objectMapper.readTree(data)
            if (jsonNode[key] == null) return null
            val keyJsonNode = jsonNode[key]
            objectMapper.readValue(keyJsonNode.asText(), clazz)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /** Parse and extract a generic collection by the key  */
    fun <T : Collection<*>?> toCollection(data: String?, key: String?, javaType: JavaType): T? {
        return if (data == null) null else try {
            val jsonNode = objectMapper.readTree(data)
            if (jsonNode[key] == null || jsonNode[key].isNull) {
                return getEmptyCollection(javaType)
            }
            val keyJsonNode = jsonNode[key]
            objectMapper.readValue(keyJsonNode.toString(), javaType)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /** Parse and extract a generic collection by the key  */
    fun <T : Collection<*>?> toCollection(data: String?, javaType: JavaType?): T? {
        return if (data == null) null else try {
            objectMapper.readValue(data, javaType)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun <T> toObject(data: String?, clazz: Class<T>?): T? {
        return if (data == null) null else try {
            objectMapper.readValue(data, clazz)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun <T> toObject(jsonNode: JsonNode?, clazz: Class<T>?): T? {
        return if (jsonNode == null) null else try {
            objectMapper.treeToValue(jsonNode, clazz)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun <T> toObject(`is`: InputStream?, clazz: Class<T>?): T? {
        return if (`is` == null) null else try {
            objectMapper.readValue(`is`, clazz)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun toString(obj: Any?): String? {
        return if (obj == null) null else try {
            objectMapper.writeValueAsString(obj)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun toTree(data: String?): JsonNode? {
        return if (data == null) null else try {
            objectMapper.readTree(data)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        val DEFAULT = Json()
        fun createObjectMapper(): ObjectMapper {
            val objectMapper = ObjectMapper()

            // Set all class field serializable (public, private, protected)
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
            //        objectMapper.setDateFormat(new ChainDateFormat()
//                .add(new ChainDateFormat())
//                .add("dd.MM.yyyy")
//                .add("dd.MM.yyyy HH:mm")
//        );
            return objectMapper
        }

        private fun <T : Collection<*>?> getEmptyCollection(typeToken: JavaType): T {
            if (typeToken.rawClass.isAssignableFrom(MutableList::class.java)) {
                return Collections.EMPTY_LIST as T
            } else if (typeToken.rawClass.isAssignableFrom(MutableSet::class.java)) {
                return Collections.EMPTY_SET as T
            } else if (typeToken.rawClass.isAssignableFrom(MutableCollection::class.java)) {
                return Collections.EMPTY_LIST as T
            } else if (typeToken.rawClass.isAssignableFrom(MutableMap::class.java)) {
                return Collections.EMPTY_MAP as T
            }
            throw RuntimeException("Unsupported collection type: " + typeToken.rawClass)
        }
    }
}
