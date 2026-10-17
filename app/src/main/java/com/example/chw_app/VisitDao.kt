package com.example.chw_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertVisit(visit: VisitRecord)

    @Query("SELECT * FROM visit_records ORDER BY created_at_local DESC")
    fun getAllVisits(): Flow<List<VisitRecord>>

    @Query("SELECT * FROM visit_records WHERE sync_status = 'Pending'")
    suspend fun getPendingVisits(): List<VisitRecord>

    @Query("UPDATE visit_records SET sync_status = :status WHERE local_id = :localId")
    suspend fun updateSyncStatus(localId: String, status: String)

}
