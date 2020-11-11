package com.yazidal.covid19.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Response(

    @field:SerializedName("Countries")
    val countries: List<CountriesItem>? = null,

    @field:SerializedName("Global")
    val global: Global? = null,

    @field:SerializedName("Date")
    val date: String? = null
) : Parcelable

@Parcelize
data class Global(

    @field:SerializedName("NewRecovered")
    val newRecovered: Int? = null,

    @field:SerializedName("NewDeaths")
    val newDeaths: Int? = null,

    @field:SerializedName("TotalRecovered")
    val totalRecovered: Int? = null,

    @field:SerializedName("TotalConfirmed")
    val totalConfirmed: Int? = null,

    @field:SerializedName("NewConfirmed")
    val newConfirmed: Int? = null,

    @field:SerializedName("TotalDeaths")
    val totalDeaths: Int? = null
) : Parcelable

data class Premium(
    val any: Any? = null
)

@Parcelize
data class CountriesItem(

    @field:SerializedName("NewRecovered")
    val newRecovered: Int? = null,

    @field:SerializedName("NewDeaths")
    val newDeaths: Int? = null,

    @field:SerializedName("TotalRecovered")
    val totalRecovered: Int? = null,

    @field:SerializedName("TotalConfirmed")
    val totalConfirmed: Int? = null,

    @field:SerializedName("Country")
    val country: String? = null,

//	@field:SerializedName("Premium")
//	val premium: Premium? = null,

    @field:SerializedName("CountryCode")
    val countryCode: String? = null,

    @field:SerializedName("Slug")
    val slug: String? = null,

    @field:SerializedName("NewConfirmed")
    val newConfirmed: Int? = null,

    @field:SerializedName("TotalDeaths")
    val totalDeaths: Int? = null,

    @field:SerializedName("Date")
    val date: String? = null
) : Parcelable

@Parcelize
data class InfoCountry(
    val Deaths: String?,
    val Confirmed: String?,
    val Recovered: String?,
    val Active: String?,
    val Date: String?
) : Parcelable
