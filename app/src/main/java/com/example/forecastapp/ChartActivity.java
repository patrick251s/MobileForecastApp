package com.example.forecastapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ChartActivity extends AppCompatActivity {

    private ArrayList<ChartData> chartDataList;
    private int forecastType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCharts);
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            forecastType = bundle.getInt("forecastType", forecastType);
            String color = bundle.getString("color");
            toolbar.setBackgroundColor(Color.parseColor(color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.parseColor(color));
            }
            chartDataList = bundle.getParcelableArrayList("chartData");
        }

        loadCharts();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadCharts() {
        loadCombinedChart();
        loadCloudsChart();
        loadWindChart();
    }

    public void loadCombinedChart() {
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        ArrayList<ILineDataSet> dataSetsLine = new ArrayList<>();
        ArrayList<String> xAxisValues = new ArrayList<>();
        ArrayList<Integer> colorList = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<Entry> lineEntries = new ArrayList<>();
        float maxPrecipationValue = 0;
        for(int i=0; i<chartDataList.size(); i++) {
            if(forecastType == 1) {
                xAxisValues.add(chartDataList.get(i).getTime().substring(8, 10)+" "+chartDataList.get(i).getTime().substring(10, 13)+"h");
            }
            lineEntries.add(new Entry(lineEntries.size(), chartDataList.get(i).getTemp()));
            if(forecastType == 2 || forecastType == 3) {
                xAxisValues.add(chartDataList.get(i).getTime().substring(0, 5));
                xAxisValues.add(chartDataList.get(i).getTime().substring(0, 5)+" N");
                lineEntries.add(new Entry(lineEntries.size(), chartDataList.get(i).getTempNight()));
            }
            float rain = chartDataList.get(i).getRain();
            float snow = chartDataList.get(i).getSnow();
            barEntries.add(new BarEntry(barEntries.size(), rain+snow));
            if(rain+snow > maxPrecipationValue) maxPrecipationValue = rain+snow;
            if(forecastType == 2 || forecastType == 3) {
                barEntries.add(new BarEntry(barEntries.size(), rain+snow));
            }
            int barColor = R.color.dark;
            if(rain > 0 && snow == 0) barColor = Color.GREEN;
            else if(rain == 0 && snow > 0 ) barColor = Color.BLUE;
            else if(rain > 0 && snow > 0) barColor = Color.rgb(0, 128, 128);
            colorList.add(barColor);
            if(forecastType == 2 || forecastType == 3) {
                colorList.add(barColor);
            }
        }

        BarData barData = new BarData();
        BarDataSet barDataSet = new BarDataSet(barEntries, "Precipation (green=>rain, blue=>snow)");
        barDataSet.setColor(Color.WHITE);
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColors(colorList);
        barDataSet.setDrawValues(false);
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        barData.addDataSet(barDataSet);

        LineData lineData = new LineData();
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Temperature");
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setColor(Color.rgb(255, 140, 0));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineData.addDataSet(lineDataSet);

        CombinedData data = new CombinedData();
        data.setData(barData);
        data.setData(lineData);

        CombinedChart cChart = findViewById(R.id.combinedChart);
        cChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        cChart.getAxisLeft().setTextColor(Color.WHITE);
        cChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Math.round(value)+"℃";
            }
        });
        cChart.getAxisRight().setTextColor(Color.WHITE);
        cChart.getAxisRight().setAxisMinimum(0);
        if(maxPrecipationValue < 5) cChart.getAxisRight().setAxisMaximum(5);
        else if(maxPrecipationValue < 10) cChart.getAxisRight().setAxisMaximum(10);
        cChart.getXAxis().setTextColor(Color.WHITE);
        cChart.getLegend().setEnabled(false);
        cChart.getDescription().setEnabled(false);
        cChart.setData(data);
    }

    public void loadCloudsChart() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<String> xAxisValues = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i=0; i<chartDataList.size(); i++) {
            if(forecastType == 1) {
                xAxisValues.add(chartDataList.get(i).getTime().substring(8, 10)+" "+chartDataList.get(i).getTime().substring(10, 13)+"h");
            }
            else if(forecastType == 2 || forecastType == 3) {
                xAxisValues.add(chartDataList.get(i).getTime().substring(0, 5));
            }
            entries.add(new Entry(i, chartDataList.get(i).getClouds()));
        }
        LineDataSet set1 = new LineDataSet(entries, "Clouds (%)");
        set1.setColor(Color.rgb(232, 232, 232));
        set1.setFillAlpha(80);
        set1.setDrawFilled(true);
        set1.setFillColor(Color.rgb(245, 245, 245));
        set1.setLineWidth(3f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        dataSets.add(set1);

        LineChart lineChart = findViewById(R.id.cloudsChart);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.getAxisRight().setAxisMinimum(0);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
    }

    public void loadWindChart() {
        ArrayList<ILineDataSet> dataSetsLine = new ArrayList<>();
        ArrayList<String> xAxisValues = new ArrayList<>();
        ArrayList<Entry> lineEntries = new ArrayList<>();
        float maxWindSpeedValue = 0;
        for(int i=0; i<chartDataList.size(); i++) {
            if(forecastType == 1) {
                xAxisValues.add(chartDataList.get(i).getTime().substring(8, 10) + " " + chartDataList.get(i).getTime().substring(10, 13) + "h");
            }
            else if(forecastType == 2 || forecastType == 3) {
                xAxisValues.add(chartDataList.get(i).getTime().substring(0, 5));
            }
            float windSpeed = chartDataList.get(i).getWindSpeed();
            if(windSpeed > maxWindSpeedValue) maxWindSpeedValue = windSpeed;
            lineEntries.add(new Entry(i, chartDataList.get(i).getWindSpeed()));
        }

        LineData lineData = new LineData();
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Wind speed");
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setColor(Color.YELLOW);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineData.addDataSet(lineDataSet);

        LineChart lineChart = findViewById(R.id.windChart);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.getAxisRight().setAxisMinimum(0);
        if(maxWindSpeedValue < 5) {
            lineChart.getAxisLeft().setAxisMaximum(5);
            lineChart.getAxisRight().setAxisMaximum(5);
        }
        else if(maxWindSpeedValue < 10) {
            lineChart.getAxisLeft().setAxisMaximum(10);
            lineChart.getAxisRight().setAxisMaximum(10);
        }
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setData(lineData);
    }
}