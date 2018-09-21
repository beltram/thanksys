package com.thanksys.test.resource

import com.thanksys.test.service.DocumentService
import org.springframework.http.HttpStatus.PARTIAL_CONTENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/documents")
class DocumentResource(private val documentService: DocumentService) {

    @GetMapping
    @ResponseStatus(PARTIAL_CONTENT)
    fun findAll() = documentService.findAll()
}