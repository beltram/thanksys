package com.thanksys.test.dao

import com.thanksys.test.model.Document
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class DaoConfiguration {

    companion object {
        val documents = (1..3).map { Document("doc$it" ,it) }
    }

    @Bean
    fun documentDao(mongoTemplate: MongoTemplate): Dao<Document> = Dao(Document::class.java, mongoTemplate, documents)
}