package com.thanksys.test.repository

import com.thanksys.test.model.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface DocumentRepository : ReactiveMongoRepository<Document, ObjectId> {
    fun existsByRank(rank: Int): Mono<Boolean>
}