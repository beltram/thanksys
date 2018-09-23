package com.thanksys.test.mapper

import com.thanksys.test.model.Document
import com.thanksys.test.model.DocumentDto
import org.bson.types.ObjectId

fun Document.toDto() = DocumentDto(name, rank, id.toString())
fun DocumentDto.toEntity() = Document(name, rank, id.toObjectId())

private fun String?.toObjectId() = this?.let { ObjectId(it) }