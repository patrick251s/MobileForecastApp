package com.example.forecastapp;

import android.os.Parcel;
import android.os.Parcelable;

public class ChartData implements Parcelable {
    private String time;
    private float temp;
    private float tempNight;
    private float rain;
    private float snow;
    private float clouds;
    private float windSpeed;

    public ChartData(String time, float temp, float tempNight, float rain, float snow, float clouds, float windSpeed) {
        this.time = time;
        this.temp = temp;
        this.tempNight = tempNight;
        this.rain = rain;
        this.snow = snow;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
    }

    public ChartData(String time, float temp, float rain, float snow, float clouds, float windSpeed) {
        this.time = time;
        this.temp = temp;
        this.rain = rain;
        this.snow = snow;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getRain() {
        return rain;
    }

    public void setRain(float rain) {
        this.rain = rain;
    }

    public float getSnow() {
        return snow;
    }

    public void setSnow(float snow) {
        this.snow = snow;
    }

    public float getClouds() {
        return clouds;
    }

    public void setClouds(float clouds) {
        this.clouds = clouds;
    }

    public float getTempNight() {
        return tempNight;
    }

    public void setTempNight(float tempNight) {
        this.tempNight = tempNight;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public ChartData(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<ChartData> CREATOR = new Parcelable.Creator<ChartData>() {
        public ChartData createFromParcel(Parcel in) {
            return new ChartData(in);
        }

        public ChartData[] newArray(int size) {
            return new ChartData[size];
        }

    };

    public void readFromParcel(Parcel in) {
        time = in.readString();
        temp = in.readFloat();
        tempNight = in.readFloat();
        rain = in.readFloat();
        snow = in.readFloat();
        clouds = in.readFloat();
        windSpeed = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(time);
        parcel.writeFloat(temp);
        parcel.writeFloat(tempNight);
        parcel.writeFloat(rain);
        parcel.writeFloat(snow);
        parcel.writeFloat(clouds);
        parcel.writeFloat(windSpeed);
    }
}
