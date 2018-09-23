package com.thanksys.test.mapper

import com.thanksys.test.model.Document
import com.thanksys.test.model.DocumentDto
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test

class DocumentMapperTest {

    @Test
    fun `to dto should map`() {
        val entity = Document("name", 1, ObjectId())
        val dto = entity.toDto()
        assertThat(dto.id).isEqualTo(entity.id.toString())
        assertThat(dto.name).isEqualTo(entity.name)
        assertThat(dto.rank).isEqualTo(entity.rank)
    }

    @Test
    fun `to entity should map`() {
        val dto = DocumentDto("name", 1, ObjectId().toString())
        val entity = dto.toEntity()
        assertThat(entity.id.toString()).isEqualTo(dto.id)
        assertThat(entity.name).isEqualTo(dto.name)
        assertThat(entity.rank).isEqualTo(dto.rank)
    }

    @Test
    fun `to entity with null id should map`() {
        val dto = DocumentDto("name", 1)
        val entity = dto.toEntity()
        assertThat(entity.id).isNull()
        assertThat(entity.name).isEqualTo(dto.name)
        assertThat(entity.rank).isEqualTo(dto.rank)
    }
}