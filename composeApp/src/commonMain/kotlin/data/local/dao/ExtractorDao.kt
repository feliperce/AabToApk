package data.local.dao

import androidx.room.*
import data.local.entity.KeystoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtractorDao {

    @Transaction
    @Insert
    suspend fun insert(keystoreEntity: KeystoreEntity): Long

    // Upsert not working on JVM!!!
    /*@Transaction
    @Upsert
    suspend fun insertOrUpdate(keystoreEntity: KeystoreEntity)*/

    @Query("SELECT * FROM Keystore")
    fun getAll(): Flow<List<KeystoreEntity>>

    @Transaction
    @Update
    suspend fun update(keystoreEntity: KeystoreEntity)

    @Delete
    suspend fun delete(keystoreEntity: KeystoreEntity)

    @Transaction
    suspend fun insertOrUpdate(keystoreEntity: KeystoreEntity) {
        if (keystoreEntity.id == null) {
            insert(keystoreEntity)
        } else {
            update(keystoreEntity)
        }
    }
}
