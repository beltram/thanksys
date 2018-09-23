package com.thanksys.test.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Document(@field:Indexed(unique = true) val name: String,
                    val rank: Int,
                    @field:Id val id: ObjectId? = null)