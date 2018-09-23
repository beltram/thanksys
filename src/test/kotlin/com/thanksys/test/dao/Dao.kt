package com.thanksys.test.dao

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate

/**
 * Generic Dao
 */
class Dao<E : Any> constructor(
        private val clazz: Class<E>,
        private val mongoTemplate: MongoTemplate,
        private val defaultEntities: List<E> = listOf()) {

    fun init() {
        deleteAll()
        defaultEntities.forEach { create(it) }
    }

    fun cleanUp() {
        deleteAll()
    }

    fun findAll(): List<E> = mongoTemplate.findAll(clazz)

    fun findAny(): E = findAll().stream().findAny().orElse(null)

    fun findById(id: ObjectId): E? = mongoTemplate.findById(id, clazz)

    private fun create(entity: E) = entity.apply { mongoTemplate.save(this) }

    fun deleteAll() {
        mongoTemplate.remove(clazz).findAndRemove()
    }
}