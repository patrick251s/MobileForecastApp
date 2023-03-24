package com.example.forecastapp;

public class ForecastRecyclerView {
    private String time;
    private String date;
    private String temperature;
    private String pressure;
    private String humidity;
    private String rain;
    private String snow;
    private String icon;
    private String clouds;
    private String windSpeed;
    private String windDeg;
    private int forecastType;

    private String dayTemp;
    private String nightTemp;
    private String sunrise;
    private String sunset;

    //Konstruktor dla short forecast
    public ForecastRecyclerView(String time, String temperature, String pressure, String humidity, String rain, String snow, String icon, String clouds, String windSpeed, String windDeg, int forecastType) {
        this.time = time.substring(11, 16);
        this.date = time.substring(0, 10);
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.rain = rain;
        this.snow = snow;
        this.icon = icon;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.windDeg = windDeg;
        this.forecastType = forecastType;
    }

    //Konstruktor dla medium i long forecast
    public ForecastRecyclerView(String date, String sunrise, String sunset, String dayTemp, String nightTemp, String pressure, String humidity, String rain, String snow, String icon, String clouds, String windSpeed, String windDeg, int forecastType) {
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.dayTemp = dayTemp;
        this.nightTemp = nightTemp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.rain = rain;
        this.snow = snow;
        this.icon = icon;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.windDeg = windDeg;
        this.forecastType = forecastType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getSnow() {
        return snow;
    }

    public void setSnow(String snow) {
        this.snow = snow;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getClouds() {
        return clouds;
    }

    public void setClouds(String clouds) {
        this.clouds = clouds;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDeg() {
        return windDeg;
    }

    public void setWindDeg(String windDeg) {
        this.windDeg = windDeg;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public int getForecastType() {
        return forecastType;
    }

    public void setForecastType(int forecastType) {
        this.forecastType = forecastType;
    }

    public String getDayTemp() {
        return dayTemp;
    }

    public void setDayTemp(String dayTemp) {
        this.dayTemp = dayTemp;
    }

    public String getNightTemp() {
        return nightTemp;
    }

    public void setNightTemp(String nightTemp) {
        this.nightTemp = nightTemp;
    }
}
