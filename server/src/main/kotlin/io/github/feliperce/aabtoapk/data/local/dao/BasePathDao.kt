package io.github.feliperce.aabtoapk.data.local.dao

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import io.github.feliperce.aabtoapk.data.local.entity.BasePathEntity
import io.github.feliperce.aabtoapk.data.mapper.toBasePathDto
import org.jetbrains.exposed.sql.transactions.transaction

class BasePathDao {
    fun insert(
        basePathDto: BasePathDto
    ): BasePathDto {
        return transaction {
            BasePathEntity.new {
                name = basePathDto.name
                createdDate = basePathDto.createdDate
                dateToRemove = basePathDto.dateToRemove
            }.toBasePathDto()
        }
    }

    fun getByName(
        name: String
    ): BasePathDto? {
        return transaction {
            val basePathEntity = BasePathEntity.find { ExtractorDb.BasePaths.name eq name }.firstOrNull()
            basePathEntity?.toBasePathDto()
        }
    }
}