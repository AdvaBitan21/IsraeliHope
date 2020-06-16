package com.technion.android.israelihope;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.technion.android.israelihope.Objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatisticsFragment extends Fragment {

    //TODO: change when we will have actual data

    int firstQuiz[] = {100, 50, 40, 70};
    int questions[] = {60, 80, 100, 50};
    int questions2[] = {70, 20, 10, 15};
    String groups[] = {"בדואים", "דרוזים", "אתיופים", "צרקסים"};

    //HI Dani, use this class for the object you get from firebase, so you can get the hist
    public class HistogramObject{
        HashMap<String,Integer>hist;

        public HistogramObject() {
        }

        public HashMap<String, Integer> getHist() {
            return hist;
        }

        public void setHist(HashMap<String, Integer> hist) {
            this.hist = hist;
        }

        public void addToHistByUser(String type) {
            hist.put( type, hist.get(type) + 1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpPieChart1();
        setUpPieChart2();
        setUpPieChart3();
    }

    private void setUpPieChart1() {
        // populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < firstQuiz.length; i++) {
            pieEntries.add(new PieEntry(firstQuiz[i], groups[i]));
        }

        TextView title1 = getView().findViewById(R.id.title1);
        title1.setText("התפלגות 1 כלשהי");
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        dataSet.setSelectionShift(10f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.35f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);

        //Get the Chart
        PieChart chart = (PieChart)getView().findViewById(R.id.chart1);
        chart.setDrawHoleEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setData(data);
        chart.animateY(1000);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.invalidate();
    }

    private void setUpPieChart2() {
        // populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) {
            pieEntries.add(new PieEntry(questions[i], groups[i]));
        }

        TextView title2 = getView().findViewById(R.id.title2);
        title2.setText("התפלגות 2 כלשהי");
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        dataSet.setSelectionShift(10f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.35f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);

        //Get the Chart
        PieChart chart = (PieChart)getView().findViewById(R.id.chart2);
        chart.setDrawHoleEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setData(data);
        chart.animateY(1000);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.invalidate();
    }

    private void setUpPieChart3() {
        // populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < questions2.length; i++) {
            pieEntries.add(new PieEntry(questions2[i], groups[i]));
        }

        TextView title3 = getView().findViewById(R.id.title3);
        title3.setText("התפלגות 3 כלשהי");
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        dataSet.setSelectionShift(10f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.35f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);

        //Get the Chart
        PieChart chart = (PieChart)getView().findViewById(R.id.chart3);
        chart.setDrawHoleEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
    }


}
