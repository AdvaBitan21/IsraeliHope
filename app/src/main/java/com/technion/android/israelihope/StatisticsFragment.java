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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Adapters.UserAdapter;
import com.technion.android.israelihope.Objects.User;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    //TODO: change when we will have actual data

    int firstQuiz[] = {100, 50, 40, 70};
    int questions[] = {60, 80, 100, 50};
    int questions2[] = {70, 20, 10, 15};
    String groups[] = {"בדואים", "דרוזים", "אתיופים", "צרקסים"};



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statistics_fragment, null);
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
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        PieData data = new PieData(dataSet);

        //Get the Chart
        PieChart chart = (PieChart)getView().findViewById(R.id.chart1);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
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
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        PieData data = new PieData(dataSet);

        //Get the Chart
        PieChart chart = (PieChart)getView().findViewById(R.id.chart2);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
    }

    private void setUpPieChart3() {
        // populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < firstQuiz.length; i++) {
            pieEntries.add(new PieEntry(firstQuiz[i], groups[i]));
        }

        TextView title3 = getView().findViewById(R.id.title3);
        title3.setText("התפלגות 3 כלשהי");
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        PieData data = new PieData(dataSet);

        //Get the Chart
        PieChart chart = (PieChart)getView().findViewById(R.id.chart3);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
    }


}
