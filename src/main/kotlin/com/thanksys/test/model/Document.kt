package com.thanksys.test.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Document(val id: ObjectId, val name: String, val rank: Int)