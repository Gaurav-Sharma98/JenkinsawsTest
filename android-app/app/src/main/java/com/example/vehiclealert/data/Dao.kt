package com.example.vehiclealert.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(owner: Owner): Long

    @Query("SELECT * FROM owners WHERE id = :id")
    fun observeById(id: Long): Flow<Owner?>

    @Query("SELECT * FROM owners WHERE id = :id")
    suspend fun getById(id: Long): Owner?
}

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle): Long

    @Query("SELECT * FROM vehicles WHERE plateNumber = :plate LIMIT 1")
    suspend fun findByPlate(plate: String): Vehicle?

    @Query("SELECT * FROM vehicles WHERE ownerId = :ownerId")
    fun observeByOwner(ownerId: Long): Flow<List<Vehicle>>
}
