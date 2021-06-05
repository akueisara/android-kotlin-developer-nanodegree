package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NeoWSApi
import com.udacity.asteroidradar.api.NetworkUtils
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.main.MainFragment
import com.udacity.asteroidradar.model.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    private var asteroidFilter = MutableLiveData(MainFragment.AsteroidFilter.ALL)
    private val dates = getStartAndEndDates()

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.switchMap(asteroidFilter) { menuFilter ->
            when (menuFilter) {
                MainFragment.AsteroidFilter.ALL -> database.asteroidDatabaseDao.getAllAsteroids()
                MainFragment.AsteroidFilter.WEEK -> database.asteroidDatabaseDao.getWeeklyAsteroids(dates.first, dates.second)
                MainFragment.AsteroidFilter.TODAY -> database.asteroidDatabaseDao.getAsteroidsForToday(dates.first)
                else -> database.asteroidDatabaseDao.getAllAsteroids()
            }

        }

    val astroPictureOfDay: LiveData<PictureOfDay> = database.asteroidDatabaseDao.getPictureOfTheDay()

    suspend fun getAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val responseString = NeoWSApi.retrofitService.getAsteroidsAsync(
                    dates.first,
                    dates.second,
                    Constants.API_KEY
                )
                val asteroids = NetworkUtils.parseAsteroidsJsonResult(JSONObject(responseString))
                database.asteroidDatabaseDao.insert(asteroids)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                val pictureOfTheDay =
                    NeoWSApi.retrofitService.getPictureOfDayAsync(Constants.API_KEY)
                        .await()
                database.asteroidDatabaseDao.insert(pictureOfTheDay)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateFilter(filter: MainFragment.AsteroidFilter) {
        asteroidFilter.value = filter
    }

    private fun getStartAndEndDates(): Pair<String, String> {
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val startDate = dateFormat.format(currentTime)
        calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        val endDate = dateFormat.format(calendar.time)
        return Pair(startDate, endDate)
    }
}