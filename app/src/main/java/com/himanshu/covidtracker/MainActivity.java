package com.himanshu.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mtodaytotal, mtotal, mactive ,mtodayactive,mrecovered,mtodayrecovered,mdeaths,mtodaydeaths;

    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types = {"cases","deaths","recovered","active"};
    private List<ModelClass> modelclasslist;
    private List<ModelClass> modelclasslist2;
    PieChart mpiechart;
    private RecyclerView recyclerView;
    com.himanshu.covidtracker.Adapter  adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        countryCodePicker = findViewById(R.id.ccp);
        mtodayactive = findViewById(R.id.todayactive);
        mactive = findViewById(R.id.activecase);
        mdeaths = findViewById(R.id.totaldeath);
        mtodaydeaths = findViewById(R.id.todaydeath);
        mrecovered = findViewById(R.id.recoveredcase);
        mtodayrecovered = findViewById(R.id.todayrecovered);
        mtotal = findViewById(R.id.totalcase);
        mtodaytotal = findViewById(R.id.todaytotal);
        mpiechart = findViewById(R.id.piechart);
        spinner = findViewById(R.id.spinner);
        mfilter = findViewById(R.id.filter);
        recyclerView = findViewById(R.id.recyclerview);
        modelclasslist = new ArrayList<>();
        modelclasslist2 = new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelclasslist2.addAll(response.body());
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(),modelclasslist2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getDefaultCountryName();
                fetchdata();
            }
        });
        fetchdata();



    }

    private void fetchdata() {

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelclasslist.addAll(response.body());
                for(int i =0;i<modelclasslist.size();i++)
                {
                    if(modelclasslist.get(i).getCountry().equals(country))
                    {
                        mactive.setText((modelclasslist.get(i).getActive()));
                        mtodaydeaths.setText((modelclasslist.get(i).getTodayDeaths()));
                        mtodayrecovered.setText((modelclasslist.get(i).getTodayRecovered()));
                        mtodaytotal.setText((modelclasslist.get(i).getTodayCases()));
                        mtotal.setText((modelclasslist.get(i).getCases()));
                        mdeaths.setText((modelclasslist.get(i).getDeaths()));
                        mrecovered.setText((modelclasslist.get(i).getRecovered()));

                        int active,total,recovered,deaths;
                        active= Integer.parseInt(modelclasslist.get(i).getActive());
                        total= Integer.parseInt(modelclasslist.get(i).getCases());
                        recovered= Integer.parseInt(modelclasslist.get(i).getRecovered());
                        deaths= Integer.parseInt(modelclasslist.get(i).getDeaths());

                        updategraph(active,total,recovered,deaths);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

        private void updategraph(int active, int total, int recovered, int deaths) {
        mpiechart.clearChart();

        mpiechart.addPieSlice(new PieModel("Confirm",total,Color.parseColor("#FF8701")));
        mpiechart.addPieSlice(new PieModel("Active",total,Color.parseColor("#FF4CAF50")));
        mpiechart.addPieSlice(new PieModel("Recovered",total,Color.parseColor("#38ACCD")));
        mpiechart.addPieSlice(new PieModel("Deaths",total,Color.parseColor("#F55c47")));
        mpiechart.startAnimation();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = types[position];
        mfilter.setText(item);
        adapter.filter(item);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}