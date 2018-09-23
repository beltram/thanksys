package com.thanksys.test.service

import com.thanksys.test.model.Document
import com.thanksys.test.repository.DocumentRepository
import mu.KLogging
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Service
class DocumentService(private val documentRepository: DocumentRepository, private val mongoTemplate: MongoTemplate) {

    companion object : KLogging()

    fun findAll() =
            documentRepository.findAll()
                    .doOnSubscribe { logger.debug { "Searching documents" } }

    fun findById(id: ObjectId) =
            documentRepository.findById(id).onNotFound(id)
                    .doOnSubscribe { logger.debug { "Searching document $id" } }
                    .doOnNext { logger.info { "Document ${it.id} found" } }

    fun create(document: Document) =
            documentRepository.existsByRank(document.rank)
                    .filter { it == false }
                    .flatMap { documentRepository.insert(document) }
                    .switchIfEmpty(document.alreadyExistException())
                    .onErrorResume { document.alreadyExistException() }
                    .doOnSubscribe { logger.debug { "Creating document $document" } }
                    .doOnNext { logger.info { "Document ${it.id} created" } }

    fun delete(id: ObjectId) =
            documentRepository.existsById(id)
                    .filter { it == true }
                    .onNotFound(id)
                    .flatMap { documentRepository.deleteById(id) }
                    .doOnSubscribe { logger.debug { "Deleting document $id" } }
                    .doOnNext { logger.info { "Document $id deleted" } }

    fun updateRanks(documents: List<Document>) =
            documents.hasDistinct(Document::rank).toMono()
                    .filter { it == true }
                    .switchIfEmpty(duplicateRankException())
                    .flatMapMany { documentRepository.findAll() }
                    .map { it.modifyRank(documents) }
                    .collectList()
                    .filter { it.hasDistinct(Document::rank) }
                    .flatMapMany { documentRepository.saveAll(documents) }
                    .switchIfEmpty(duplicateRankException())

    private fun Document.modifyRank(documents: List<Document>) = documents.find { it.id == id }?.let { copy(rank = it.rank) } ?: this

    private fun <E> List<E>.hasDistinct(field: (E) -> Any) = distinctBy(field).size == size

    private fun <E> Mono<E>.onNotFound(id: ObjectId) = switchIfEmpty(IllegalArgumentException("Document $id not found").toMono())

    private fun <E> duplicateRankException(): Mono<E> = IllegalArgumentException("Duplicate rank").toMono()

    private fun <E> Document.alreadyExistException(): Mono<E> = IllegalArgumentException("Document ${this} already exists").toMono()
}