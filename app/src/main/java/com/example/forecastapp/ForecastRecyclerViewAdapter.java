package com.example.forecastapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ForecastRecyclerView> forecastList;

    public ForecastRecyclerViewAdapter(Context context, ArrayList<ForecastRecyclerView> forecastList) {
        this.context = context;
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastRecyclerViewAdapter.ViewHolder holder, int position) {
        ForecastRecyclerView modal = forecastList.get(position);
        loadForecastDatePart(modal, holder);
        loadForecastTemperaturePart(modal, holder);
        loadForecastPrecipationPart(modal, holder);
        loadForecastWindPart(modal, holder);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWindSpeed, tvTemperature, tvTime, tvDate, tvPrecip, tvClouds, tvPressure, tvHumidity, tvTempDescription;
        private ImageView ivCondition, ivWindArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.idTVDateCard);
            tvTime = itemView.findViewById(R.id.idTVTimeCard);
            tvTempDescription = itemView.findViewById(R.id.idTVTempDescriptionCard);
            tvTemperature = itemView.findViewById(R.id.idTVTemperatureCard);
            ivCondition = itemView.findViewById(R.id.idIVConditionCard);
            tvPrecip = itemView.findViewById(R.id.idTVPrecipCard);
            tvClouds = itemView.findViewById(R.id.idTVCloudsCard);
            tvPressure = itemView.findViewById(R.id.idTVPressureCard);
            tvHumidity = itemView.findViewById(R.id.idTVHumidityCard);
            tvWindSpeed = itemView.findViewById(R.id.idTVWindSpeedCard);
            ivWindArrow = itemView.findViewById(R.id.idIVWindArrowCard);
        }
    }

    public String getPrecipDesignation(int forecastType) {
        String designation = "";
        if(forecastType == 1) designation = "mm/3h";
        if(forecastType == 2 || forecastType == 3) designation = "mm/24h";
        return designation;
    }

    public void loadForecastDatePart(ForecastRecyclerView modal, ForecastRecyclerViewAdapter.ViewHolder holder) {
        if(modal.getForecastType() == 1) {
            holder.tvDate.setText(modal.getDate());
            holder.tvTime.setText(modal.getTime());
        }
        else if(modal.getForecastType() == 2 || modal.getForecastType() == 3) {
            holder.tvDate.setText(modal.getDate());
            holder.tvTime.setTextSize(18);
            holder.tvTime.setText("Sunrise: "+modal.getSunrise()+" Sunset: "+modal.getSunset());
        }
    }

    public void loadForecastTemperaturePart(ForecastRecyclerView modal, ForecastRecyclerViewAdapter.ViewHolder holder) {
        if(modal.getForecastType() == 1) {
            holder.tvTemperature.setText(modal.getTemperature() + "°C");
            float temp = Float.parseFloat(modal.getTemperature());
            if(temp > 0) holder.tvTemperature.setTextColor(ContextCompat.getColor(context, R.color.bootstrap_danger));
            else holder.tvTemperature.setTextColor(ContextCompat.getColor(context, R.color.bootstrap_primary));
        }
        else if(modal.getForecastType() == 2 || modal.getForecastType() == 3) {
            holder.tvTempDescription.setText("Day/Night");
            int tempD = Math.round(Float.parseFloat(modal.getDayTemp()));
            int tempN = Math.round(Float.parseFloat(modal.getNightTemp()));
            holder.tvTemperature.setTextSize(24);
            holder.tvTemperature.setText(Integer.toString(tempD)+"°C / "+Integer.toString(tempN)+"°C");
        }
    }

    public void loadForecastPrecipationPart(ForecastRecyclerView modal, ForecastRecyclerViewAdapter.ViewHolder holder) {
        float rainPrecip = Float.parseFloat(modal.getRain());
        float snowPrecip = Float.parseFloat(modal.getSnow());
        int rainPrecipInt = Math.round(rainPrecip);
        int snowPrecipInt = Math.round(snowPrecip);
        if(rainPrecip != 0 && snowPrecip != 0) {
            int precipSum = rainPrecipInt+snowPrecipInt;
            float precipSumF = rainPrecip + snowPrecip;
            if(modal.getForecastType() == 1) holder.tvPrecip.setText("Precip: "+Float.toString(precipSumF) + getPrecipDesignation(modal.getForecastType()));
            else holder.tvPrecip.setText("Precip: "+Integer.toString(precipSum) + getPrecipDesignation(modal.getForecastType()));
        }
        else if(rainPrecip == 0 && snowPrecip != 0) {
            if(modal.getForecastType() == 1) holder.tvPrecip.setText("Precip: "+Float.toString(snowPrecip) + getPrecipDesignation(modal.getForecastType()));
            else holder.tvPrecip.setText("Precip: "+Integer.toString(snowPrecipInt) + getPrecipDesignation(modal.getForecastType()));
        }
        else if(rainPrecip != 0 && snowPrecip == 0) {
            if(modal.getForecastType() == 1) holder.tvPrecip.setText("Precip: "+Float.toString(rainPrecip) + getPrecipDesignation(modal.getForecastType()));
            else holder.tvPrecip.setText("Precip: "+Integer.toString(rainPrecipInt) + getPrecipDesignation(modal.getForecastType()));
        }
        else {
            holder.tvPrecip.setText("Precip: 0");
        }
    }

    public void loadForecastWindPart(ForecastRecyclerView modal, ForecastRecyclerViewAdapter.ViewHolder holder) {
        Picasso.get().load("https://openweathermap.org/img/wn/"+modal.getIcon()+"@2x.png").into(holder.ivCondition);
        holder.tvClouds.setText("Clouds: "+modal.getClouds()+"%");
        holder.tvPressure.setText("Pressure: "+modal.getPressure()+" hPa");
        holder.tvHumidity.setText("Humitidy: "+modal.getHumidity()+"%");
        float windSpeedF = Float.parseFloat(modal.getWindSpeed());
        holder.tvWindSpeed.setText("Wind: "+Math.round(windSpeedF)+" km/h");
        holder.ivWindArrow.setRotation(Float.parseFloat(modal.getWindDeg())+180);
    }
}
