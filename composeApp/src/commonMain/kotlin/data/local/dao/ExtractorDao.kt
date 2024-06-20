package data.local.dao

import androidx.room.*
import data.local.entity.KeystoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtractorDao {

    @Transaction
    @Insert
    suspend fun insert(keystoreEntity: KeystoreEntity): Long

    @Transaction
    @Upsert
    suspend fun insertOrUpdate(keystoreEntity: KeystoreEntity): Long

    @Query("SELECT * FROM Keystore")
    fun getAll(): Flow<List<KeystoreEntity>>

    @Transaction
    @Update
    suspend fun update(keystoreEntity: KeystoreEntity)

    @Delete
    suspend fun delete(keystoreEntity: KeystoreEntity)

}
