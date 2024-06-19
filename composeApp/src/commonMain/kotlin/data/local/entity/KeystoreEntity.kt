package data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Keystore")
data class KeystoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    val path: String,
    val password: String,
    @ColumnInfo(name = "key_alias") val keyAlias: String,
    @ColumnInfo(name = "key_password") val keyPassword: String
)
