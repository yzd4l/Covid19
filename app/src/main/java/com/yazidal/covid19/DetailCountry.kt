package com.yazidal.covid19

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.yazidal.covid19.model.CountriesItem
import com.yazidal.covid19.model.InfoCountry
import com.yazidal.covid19.network.InfoService
import kotlinx.android.synthetic.main.activity_detail_country.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DetailCountry : AppCompatActivity() {
    companion object {
        const val EXTRA_COUNTRY = "EXTRA COUNTRY"
        lateinit var simpanDataCountry: String
        lateinit var simpanDataFlag: String
    }

    private val sharedPrefile = "kotlinsharedpreference" //untuk membuat nama variable
    private lateinit var sharePreference: SharedPreferences //untuk memproses data
    private val dayCase = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_country)

        sharePreference = this.getSharedPreferences(sharedPrefile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharePreference.edit()
//dapatkan data dari parcelize
        val data = intent.getParcelableExtra<CountriesItem>(EXTRA_COUNTRY)
        val formatter: NumberFormat = DecimalFormat("#,###")
//jika data null maka tidak menggunakan tanda tanya
        data.let {
            txt_countryName.text = data!!.country
            latest_update.text = data.date
            latest_totalCurrentConfirmed.text = formatter.format(data.totalConfirmed?.toDouble())
            latest_currentRecovered.text = formatter.format(data.totalRecovered?.toDouble())
            latest_totalCurrentDeaths.text = formatter.format(data.totalDeaths?.toDouble())
            latest_newConfirmed.text = formatter.format(data.newConfirmed?.toDouble())
            latest_newRecovered.text = formatter.format(data.newRecovered?.toDouble())
            latest_newDeaths.text = formatter.format(data.newDeaths?.toDouble())

            editor.putString(data.country, data.country) // untuk meletakan dan menyimpan data
            editor.apply() // untuk menyinmpan data yang di taro
            editor.commit() // untuk menerapkan sharedpreference

            val simpanCountry = sharePreference.getString(data.country, data.country)
            val simpanFlag = sharePreference.getString(data.countryCode, data.countryCode)
            simpanDataCountry = simpanCountry.toString()
            simpanDataFlag = simpanFlag.toString() + "/flat/64.png"

            if (simpanFlag != null) {
                Glide.with(this).load("https://www.countryflags.io/$simpanFlag")
                    .into(img_countryFlag)
            } else {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
            }

            Glide.with(this)
                .load("https://www.countryflags.io/" + data.countryCode + "/flat/64.png")
                .into(img_countryFlag)
        }

     getChart()
    }

    private fun getChart() {
        val okhttp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/dayone/country/")
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(InfoService::class.java)
        api.getINfoService(simpanDataCountry).enqueue(object : Callback<List<InfoCountry>> {

            override fun onFailure(call: Call<List<InfoCountry>>, t: Throwable) {
                Toast.makeText(this@DetailCountry, "EROR", Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("SimpleDateFormat")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<InfoCountry>>,
                response: Response<List<InfoCountry>>
            ) {
                val getListDataCovid : List<InfoCountry> = response.body()!!
                if (response.isSuccessful){
                    val barEntrys: ArrayList<BarEntry> = ArrayList()
                    val barEntrys1: ArrayList<BarEntry> = ArrayList()
                    val barEntrys2: ArrayList<BarEntry> = ArrayList()
                    val barEntrys3: ArrayList<BarEntry> = ArrayList()
                    var y = 0

                    while (y < getListDataCovid.size){
                        for (x in getListDataCovid){
                            val barEntry = BarEntry(y.toFloat(), x.Confirmed?.toFloat()?: 0f)
                            val barEntry1 = BarEntry(y.toFloat(), x.Active?.toFloat()?: 0f)
                            val barEntry2 = BarEntry(y.toFloat(), x.Deaths?.toFloat()?: 0f)
                            val barEntry3 = BarEntry(y.toFloat(), x.Recovered?.toFloat()?: 0f)

                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'")
                            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
                            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val date:Date? = inputFormat.parse(x.Date!!)
                            val formattDate: String = outputFormat.format(date!!)
                            dayCase.add(formattDate)

                            barEntrys.add(barEntry)
                            barEntrys1.add(barEntry1)
                            barEntrys2.add(barEntry2)
                            barEntrys3.add(barEntry3)
                            y++

                        }
                        val xAxis: XAxis = barChart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(dayCase)
                        barChart.axisLeft.axisMinimum = 0f
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.granularity = 1f
                        xAxis.setCenterAxisLabels(true)
                        xAxis.isGranularityEnabled = true
                        val barDataSet = BarDataSet(barEntrys, "Confirmed")
                        val barDataSet2 = BarDataSet(barEntrys1, "Deaths")
                        val barDataSet3 = BarDataSet(barEntrys2, "Recovered")
                        val barDataSet4 = BarDataSet(barEntrys3, "Active")
                        //setColorBAr
                        barDataSet.setColors(Color.parseColor("#F44336"))
                        barDataSet2.setColors(Color.parseColor("#FFEB3B"))
                        barDataSet3.setColors(Color.parseColor("#03DAC5"))
                        barDataSet4.setColors(Color.parseColor("#2196F3"))

                        val data = BarData(barDataSet, barDataSet2, barDataSet3, barDataSet4)
                        barChart.data = data
                        //ukuran grafik atau bar
                        val barSpace = 0.02f
                        val groupSpace = 0.3f
                        val groupCount = 4f
                        data.barWidth = 0.15f
                        barChart.invalidate()
                        barChart.setNoDataTextColor(R.color.colorAccent)
                        barChart.setTouchEnabled(true)
                        barChart.description.isEnabled = false
                        barChart.xAxis.axisMinimum = 0f
                        barChart.setVisibleXRangeMaximum(
                            0f + barChart.barData.getGroupWidth(
                                groupSpace,
                                barSpace
                            ) * groupCount
                        )
                        barChart.groupBars(0f,groupSpace, barSpace)

                    }

                }

            }

        })
    }
}