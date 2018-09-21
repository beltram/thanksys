package com.thanksys.test.service

import com.thanksys.test.model.Document
import com.thanksys.test.repository.DocumentRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class DocumentService(private val documentRepository: DocumentRepository) {

    companion object : KLogging()

    fun findAll() = documentRepository.findAll()

    fun create(document: Document) = documentRepository.save(document)
            .doOnSubscribe { logger.debug { "Creating document ${document.id}" } }
            .doOnNext { logger.info { "Document ${it.id} created" } }
}