package com.thanksys.test.model

import javax.validation.constraints.NotNull

data class DocumentDto(@field:NotNull val name: String, @field:NotNull val rank: Int, val id: String? = null)