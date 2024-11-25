package io.github.feliperce.aabtoapk.data.local.entity

import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BasePathEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BasePathEntity>(ExtractorDb.BasePaths)

    var name by ExtractorDb.BasePaths.name
    var path by ExtractorDb.BasePaths.path
    var createdDate by ExtractorDb.BasePaths.createdDate
    var dateToRemove by ExtractorDb.BasePaths.dateToRemove
}
