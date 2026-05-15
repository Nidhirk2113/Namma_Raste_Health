package com.nammaraste.health.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nammaraste.health.data.local.entity.Road

@Dao
interface RoadDao {

    @Query("SELECT * FROM roads WHERE isActive = 1 ORDER BY name ASC")
    fun getAllRoads(): LiveData<List<Road>>

    @Query("""
        SELECT * FROM roads
        WHERE isActive = 1 AND (
            name LIKE '%' || :query || '%'
            OR district LIKE '%' || :query || '%'
            OR taluka LIKE '%' || :query || '%'
            OR contractorName LIKE '%' || :query || '%'
            OR conditionStatus LIKE '%' || :query || '%'
        )
        ORDER BY name ASC
    """)
    fun searchRoads(query: String): LiveData<List<Road>>

    @Query("SELECT * FROM roads WHERE id = :roadId AND isActive = 1")
    fun getRoadById(roadId: Int): LiveData<Road>

    @Query("SELECT COUNT(*) FROM roads WHERE isActive = 1")
    fun getTotalRoadCount(): LiveData<Int>

    @Query("UPDATE roads SET isActive = 0 WHERE id = :roadId")
    suspend fun softDeleteRoad(roadId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoad(road: Road)

    @Update
    suspend fun updateRoad(road: Road)

    @Delete
    suspend fun deleteRoad(road: Road)
}
