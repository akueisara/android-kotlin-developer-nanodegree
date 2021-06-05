package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AsteroidDatabase.getInstance(application)
    private val asteroidRepository = AsteroidRepository(database)

    val asteroidList = asteroidRepository.asteroids
    val astroPictureOfDay = asteroidRepository.astroPictureOfDay

    init {
        viewModelScope.launch {
            asteroidRepository.getAsteroids()
            asteroidRepository.getPictureOfDay()
        }
    }

    fun filterAsteroidsBy(filter: MainFragment.AsteroidFilter) {
        asteroidRepository.updateFilter(filter)
    }
}