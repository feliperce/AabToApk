package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
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
    suspend fun update(scheduleEntity: KeystoreEntity)

}
