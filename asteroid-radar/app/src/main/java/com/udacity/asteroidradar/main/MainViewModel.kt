package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid

class MainViewModel : ViewModel() {
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        _asteroids.value = mutableListOf(
            Asteroid(
                2465633,
                "465633 (2009 JR5)",
                "2015-09-08",
                20.36,
                0.5035469604,
                18.1279547773,
                0.3027478814,
                true
            ),
            Asteroid(
                3426410,
                "(2008 QV11)",
                "2015-09-08",
                21.0,
                0.3750075218,
                19.7498082027,
                0.2591243925,
                false
            ),
            Asteroid(
                3553060,
                "(2010 XT10)",
                "2015-09-08",
                26.5,
                0.0297879063,
                19.1530348886,
                0.4917435147,
                false
            )
        )
    }
}