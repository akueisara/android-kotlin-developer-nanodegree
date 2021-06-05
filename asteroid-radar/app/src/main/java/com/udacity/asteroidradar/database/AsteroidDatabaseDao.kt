package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.AstroPictureOfDay

@Dao
interface AsteroidDatabaseDao {

    @Query("SELECT * from asteroid_table ORDER by close_approach_date ASC")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(asteroids: ArrayList<Asteroid>)

    @Query("SELECT * from apod_table")
    fun getPictureOfTheDay(): LiveData<AstroPictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(picture: AstroPictureOfDay)
}