package com.nammaraste.health.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nammaraste.health.data.local.entity.Route

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes WHERE isActive = 1 ORDER BY name ASC")
    fun getAllRoutes(): LiveData<List<Route>>

    @Query("SELECT * FROM routes WHERE id = :routeId AND isActive = 1")
    fun getRouteById(routeId: Int): LiveData<Route>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: Route)

    @Update
    suspend fun updateRoute(route: Route)

    @Query("UPDATE routes SET isActive = 0 WHERE id = :routeId")
    suspend fun softDeleteRoute(routeId: Int)
}
