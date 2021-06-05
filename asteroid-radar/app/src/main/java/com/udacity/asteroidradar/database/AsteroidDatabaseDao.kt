package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay

@Dao
interface AsteroidDatabaseDao {

    @Query("SELECT * from asteroid_table ORDER by close_approach_date ASC")
    fun getAllAsteroids(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date =:date ORDER BY close_approach_date ASC")
    fun getAsteroidsForToday(date: String): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date BETWEEN :startDate AND :endDate ORDER BY close_approach_date ASC")
    fun getWeeklyAsteroids(startDate: String, endDate: String): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(asteroids: ArrayList<Asteroid>)

    @Query("SELECT * from apod_table")
    fun getPictureOfTheDay(): LiveData<PictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(picture: PictureOfDay)
}