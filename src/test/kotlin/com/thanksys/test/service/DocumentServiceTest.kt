package com.thanksys.test.service

import com.thanksys.test.dao.Dao
import com.thanksys.test.model.Document
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.expectError
import reactor.test.test

@SpringBootTest
@ExtendWith(SpringExtension::class)
class DocumentServiceTest(@Autowired private val documentDao: Dao<Document>, @Autowired private val documentService: DocumentService) {

    @BeforeEach
    fun beforeEach() {
        documentDao.init()
    }

    @AfterEach
    fun afterEach() {
        documentDao.cleanUp()
    }

    @Test
    fun `find all should find some documents`() {
        documentService.findAll().test()
                .expectSubscription()
                .expectNextSequence(documentDao.findAll())
                .verifyComplete()
    }

    @Test
    fun `find all should find none`() {
        documentDao.deleteAll()
        documentService.findAll().test()
                .expectSubscription()
                .verifyComplete()
    }

    @Test
    fun `find by id should find one`() {
        val any = documentDao.findAny()
        documentService.findById(any.id!!).test()
                .expectSubscription()
                .consumeNextWith { assertThat(it).isEqualTo(any) }
                .verifyComplete()
    }

    @Test
    fun `find by id should fail cuz not exists`() {
        documentDao.deleteAll()
        documentService.findById(ObjectId()).test()
                .expectSubscription()
                .expectError(IllegalArgumentException::class)
                .verify()
    }

    @Test
    fun `create should create one`() {
        val documents = documentDao.findAll()
        val toCreate = Document("name", Int.MAX_VALUE)
        documentService.create(toCreate).test()
                .expectSubscription()
                .consumeNextWith {
                    assertThat(it.id).isNotNull()
                    assertThat(it).isEqualToIgnoringGivenFields(toCreate, Document::id.name)
                }
                .verifyComplete()
        val documentsAfterCreation = documentDao.findAll()
        assertThat(documentsAfterCreation.distinctBy { it.rank }.size).isEqualTo(documents.size + 1)
        assertThat(documentsAfterCreation.distinctBy { it.name }.size).isEqualTo(documents.size + 1)

    }

    @Test
    fun `create should fail cuz already exists`() {
        val toCreate = documentDao.findAny()
        documentService.create(toCreate).test()
                .expectSubscription()
                .expectError(IllegalArgumentException::class)
                .verify()
    }

    @Test
    fun `create should fail cuz rank uniqueness`() {
        val any = documentDao.findAny()
        val toCreate = Document("other", any.rank)
        documentService.create(toCreate).test()
                .expectSubscription()
                .expectError(IllegalArgumentException::class)
                .verify()
    }

    @Test
    fun `create should fail cuz name uniqueness`() {
        val any = documentDao.findAny()
        val toCreate = Document(any.name, Int.MAX_VALUE)
        documentService.create(toCreate).test()
                .expectSubscription()
                .expectError(IllegalArgumentException::class)
                .verify()
    }

    @Test
    fun `delete should delete one`() {
        val idToDelete = documentDao.findAny().id!!
        documentService.delete(idToDelete).test()
                .expectSubscription()
                .verifyComplete()
        assertThat(documentDao.findById(idToDelete)).isNull()
    }

    @Test
    fun `delete should fail cuz not exists`() {
        val idToDelete = documentDao.findAny().id!!
        documentDao.deleteAll()
        documentService.delete(idToDelete).test()
                .expectSubscription()
                .expectError(IllegalArgumentException::class)
                .verify()
    }

    @Test
    fun `update should update documents`() {
        val documents = documentDao.findAll()
        val documentsToUpdate = documents.map { it.copy(rank = it.rank + 1) }
        documentService.updateRanks(documentsToUpdate).test()
                .expectSubscription()
                .expectNextCount(documentsToUpdate.size.toLong())
                .verifyComplete()
        val updatedDocuments = documentDao.findAll()
        assertThat(updatedDocuments).containsExactlyInAnyOrder(*documentsToUpdate.toTypedArray())
        assertThat(updatedDocuments.distinctBy { it.rank }).hasSameSizeAs(documents)
    }

    @Test
    fun `update should fail cuz duplicates in db`() {
        val (first, second) = documentDao.findAll()
        documentService.updateRanks(listOf(second.copy(rank = first.rank))).test()
                .expectSubscription()
                .expectError(IllegalArgumentException::class)
                .verify()
    }

    @Test
    fun `update should fail cuz duplicates in updates`() {
        val documentsToUpdate = listOf("a" to 10, "b" to 10).map { (name, rank) -> Document(name, rank) }
        documentService.updateRanks(documentsToUpdate).test()
                .expectSubscription()
                .expectError(IllegalArgumentException::class)
                .verify()
    }

    @Test
    @Disabled("See fixme")
    fun `expected behaviour should succeed`() {
        documentService.findAll().blockFirst()

        // FIXME I don't get it this is supposed to fail as rank 2 already exists
        val newDocToCreate = Document("newDoc", 2)
        val newDoc: Document? = documentService.create(newDocToCreate).block()

        documentService.findAll().blockFirst()

        val new2ndDocToCreate = Document("new2ndDoc", 0)
        documentService.create(new2ndDocToCreate).block()
        documentService.findAll().blockFirst()

        val idToDelete = newDoc?.id!!
        documentService.delete(idToDelete).block()
        assertThat(documentDao.findById(idToDelete)).isNull()

        documentService.findAll().blockFirst()

        val documentsToUpdate = listOf("doc1" to 3, "doc2" to 4, "doc3" to 1, "new2ndDoc" to 2).map { (n, r) -> Document(n, r) }
        val oldDocuments = documentDao.findAll()
        documentService.updateRanks(documentsToUpdate).blockFirst()

        val updatedDocuments = documentDao.findAll()
        assertThat(updatedDocuments).containsExactlyInAnyOrder(*documentsToUpdate.toTypedArray())
        assertThat(updatedDocuments.distinctBy { it.rank }).hasSameSizeAs(oldDocuments)
        documentService.findAll().blockFirst()
    }
}

