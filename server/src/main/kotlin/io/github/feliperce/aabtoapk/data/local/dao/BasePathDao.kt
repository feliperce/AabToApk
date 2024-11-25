package io.github.feliperce.aabtoapk.data.local.dao

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import io.github.feliperce.aabtoapk.data.local.entity.BasePathEntity
import io.github.feliperce.aabtoapk.data.mapper.toBasePathDto
import io.github.feliperce.aabtoapk.data.mapper.toBasePathDtoList
import io.github.feliperce.aabtoapk.utils.date.getCurrentInstant
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class BasePathDao {
    fun insert(
        basePathDto: BasePathDto
    ): BasePathDto {
        return transaction {
            BasePathEntity.new {
                name = basePathDto.name
                path = basePathDto.path
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

    fun getAllByDateToRemove(): List<BasePathDto> {
        return transaction {
            val currentInstant = getCurrentInstant()

            val basePathEntity = BasePathEntity.find {
                ExtractorDb.BasePaths.dateToRemove lessEq currentInstant
            }

            basePathEntity.toBasePathDtoList()
        }
    }

    fun removeByName(
        name: String
    ): Int {
        return transaction {
            ExtractorDb.BasePaths.deleteWhere { ExtractorDb.BasePaths.name eq name }
        }
    }

    fun removeById(
        id: Int
    ): Int {
        return transaction {
            ExtractorDb.BasePaths.deleteWhere { ExtractorDb.BasePaths.id eq id }
        }
    }
}