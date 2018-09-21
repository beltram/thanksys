package com.thanksys.test.mapper

import com.thanksys.test.model.Document
import com.thanksys.test.model.DocumentDto
import org.bson.types.ObjectId

fun Document.toDto() = DocumentDto(id.toString(), name, rank)
fun DocumentDto.toEntity() = Document(ObjectId(id), name, rank)