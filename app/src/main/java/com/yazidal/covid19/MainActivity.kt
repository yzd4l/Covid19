package com.yazidal.covid19

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.yazidal.covid19.adapter.CountryAdapter
import com.yazidal.covid19.model.CountriesItem
import com.yazidal.covid19.model.Response
import com.yazidal.covid19.network.ApiService
import com.yazidal.covid19.network.RetrofitBuilder.retrofit
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {
    private var ascending = true

    companion object{
       private lateinit var adaptersCountry: CountryAdapter

    }
   private lateinit var adaptersNegara: CountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptersNegara.filter.filter(newText)
                return false
            }

        })

        swipe_refresh.setOnRefreshListener {
            getNegara()
            swipe_refresh.isRefreshing = false
        }

        initializedView()
        getNegara()

    }

    private fun initializedView() {
        btnSequence.setOnClickListener {
            sequenceWithoutInternet(ascending)
            ascending = !ascending

        }

    }

    private fun sequenceWithoutInternet(ascending: Boolean) {
        rv_country.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            if (ascending) {
                (layoutManager as LinearLayoutManager).reverseLayout = true
                (layoutManager as LinearLayoutManager).stackFromEnd = true
                Toast.makeText(this@MainActivity, "Z - A", Toast.LENGTH_SHORT).show()
            } else {
                (layoutManager as LinearLayoutManager).reverseLayout = true
                (layoutManager as LinearLayoutManager).stackFromEnd = true
                Toast.makeText(this@MainActivity, "A - Z", Toast.LENGTH_SHORT).show()
            }
            adapter = adapter
        }
    }

    private fun getNegara() {
        val api = retrofit.create(ApiService::class.java)
        api.getAllCountry().enqueue(object : Callback<Response> {
            override fun onFailure(call: Call<Response>, t: Throwable) {
                progress_bar.visibility = View.GONE
            }

            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful) {
                    val getListDataCorona = response.body()!!.global
                    val formatter: java.text.NumberFormat = java.text.DecimalFormat("#,###")
                    txt_confirmed_globe.text =
                        formatter.format(getListDataCorona?.totalConfirmed?.toDouble())
                    txt_recovered_globe.text =
                        formatter.format(getListDataCorona?.totalRecovered?.toDouble())
                    txt_death_globe.text =
                        formatter.format(getListDataCorona?.totalDeaths?.toDouble())
                    rv_country.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        progress_bar.visibility = View.GONE
                        adaptersNegara = CountryAdapter(
                            response.body()!!.countries as ArrayList<CountriesItem>

                        ) {country -> itemClicked(country)}
                        adapter = adaptersNegara
                    }
                }
            }

            private fun itemClicked(country: CountriesItem) {
                val pindahData = Intent(this@MainActivity,DetailCountry::class.java)
                pindahData.putExtra(DetailCountry.EXTRA_COUNTRY, country)
                startActivity(pindahData)
            }
        })
    }
}