package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.model.AstroPictureOfDay
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URL)
    .build()


interface NeoWSApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidsAsync(
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
        @Query("api_key") apiKey: String
    ): String

    @GET("/planetary/apod")
    fun getPictureOfDayAsync(@Query("api_key") apiKey: String): Deferred<AstroPictureOfDay>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object NeoWSApi {
    val retrofitService : NeoWSApiService by lazy { retrofit.create(NeoWSApiService::class.java) }
}