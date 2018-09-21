package com.thanksys.test.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.test

@SpringBootTest
@ExtendWith(SpringExtension::class)
class DocumentServiceTest(@Autowired private val documentService: DocumentService) {

    @Test
    fun `find all should find some documents`() {
        documentService.findAll().test()
    }
}

