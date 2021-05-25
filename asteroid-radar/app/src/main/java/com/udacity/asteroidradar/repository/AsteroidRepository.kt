package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NeoWSApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    suspend fun getAsteroids() {
        withContext(Dispatchers.IO) {
            val responseString = NeoWSApi.retrofitService.getAsteroidsAsync(null, null,"wxthVHrXBVZFK6x7nweX6eKsfPfV98V6BIFwYlTe")
            _asteroids.postValue(parseAsteroidsJsonResult(JSONObject(responseString)))
        }
    }
}