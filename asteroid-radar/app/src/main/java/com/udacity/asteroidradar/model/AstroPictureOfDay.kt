package com.udacity.asteroidradar.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "apod_table")
data class AstroPictureOfDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val url: String,
    @Json(name = "media_type")
    @ColumnInfo(name = "media_type")
    val mediaType: String,
    val title: String
) : Parcelable