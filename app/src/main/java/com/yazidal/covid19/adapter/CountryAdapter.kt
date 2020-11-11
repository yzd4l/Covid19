package com.yazidal.covid19.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yazidal.covid19.R
import com.yazidal.covid19.model.CountriesItem
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_country.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CountryAdapter(
    private val country: ArrayList<CountriesItem>, private val clickList: (CountriesItem) -> Unit
) :
    RecyclerView.Adapter<CountryViewHolder>(), Filterable {

    var countrList = ArrayList<CountriesItem>()

    init {
        countrList = country
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return countrList.size
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(countrList[position], clickList)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                countrList = if (charSearch.isEmpty()) {
                    country
                } else {
                    val resultList = ArrayList<CountriesItem>()
                    for (row in country) {
                        val search = row.country?.toLowerCase(Locale.ROOT) ?: ""
                        if (search.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResult = FilterResults()
                filterResult.values = countrList
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                countrList = results?.values as ArrayList<CountriesItem>
                notifyDataSetChanged()
            }

        }
    }
}

class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(country: CountriesItem, clickList: (CountriesItem) -> Unit) {
        val country_name: TextView = itemView.tvCountry
        val country_imgFlag: CircleImageView = itemView.imgFlag
        val country_totalCase: TextView = itemView.totalCase
        val country_totalRecovered: TextView = itemView.totalRecovered
        val country_totalDeath: TextView = itemView.totalDeath

        val formatter: NumberFormat = DecimalFormat("#,###")

        country_name.tvCountry.text = country.country
        country_totalCase.totalCase.text = formatter.format(country.totalConfirmed?.toDouble())
        country_totalDeath.totalDeath.text = formatter.format(country.totalDeaths?.toDouble())
        country_totalRecovered.totalRecovered.text = formatter.format(country.totalRecovered?.toDouble())

        Glide.with(itemView).load("https://www.countryflags.io/"+ country.countryCode + "/flat/64.png")
            .into(country_imgFlag)

        country_name.setOnClickListener { clickList(country) }
        country_totalCase.setOnClickListener { clickList(country) }
        country_totalRecovered.setOnClickListener { clickList(country) }
        country_totalDeath.setOnClickListener { clickList(country) }
        country_imgFlag.setOnClickListener { clickList(country) }

    }

}