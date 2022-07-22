package com.genar.genarweather

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kwabenaberko.openweathermaplib.model.threehourforecast.ThreeHourForecastWeather


open class WeatherItemAdapter(weatherItemList: List<ThreeHourForecastWeather>, ctx: Context) : RecyclerView.Adapter<WeatherItemAdapter.ViewHolder>(){
    private var weatherItemList: List<ThreeHourForecastWeather>
    private var ctx: Context

    init {
        this.weatherItemList = weatherItemList
        this.ctx = ctx
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.item_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(weatherItemList[position])

        holder.itemContainer.setOnClickListener {
            val intent = Intent(ctx, WeatherDetailActivity::class.java).apply{

            }
            ctx.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return weatherItemList.size
    }
    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemContainer: CardView = view.findViewById(R.id.weatherItemContainer)
        var date: TextView = view.findViewById(R.id.tv_date)
        var description: TextView = view.findViewById(R.id.tv_description)
        var maxTemp: TextView = view.findViewById(R.id.tv_maxTemp)
        var windSpeed: TextView = view.findViewById(R.id.tv_windSpeed)

        fun bind(item: ThreeHourForecastWeather){
            date.text = item.dtTxt
            description.text = item.weather[0].description
            maxTemp.text = item.main.tempMax.toString()
            windSpeed.text = item.wind.speed.toString()
        }
    }

}