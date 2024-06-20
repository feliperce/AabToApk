package feature.extractor.mapper

import data.local.entity.KeystoreEntity

fun KeystoreEntity.toKeystoreDto() =
    KeystoreDto(
        id = id,
        name = name,
        path = path,
        password = password,
        keyAlias = keyAlias,
        keyPassword = keyPassword
    )

fun List<KeystoreEntity>.toKeystoreDtoList() =
    map {
        it.toKeystoreDto()
    }

fun KeystoreDto.toKeystoreEntity() =
    KeystoreEntity(
        id = id,
        name = name,
        path = path,
        password = password,
        keyAlias = keyAlias,
        keyPassword = keyPassword
    )

fun List<KeystoreDto>.toKeystoreEntityList() =
    map {
        it.toKeystoreEntity()
    }