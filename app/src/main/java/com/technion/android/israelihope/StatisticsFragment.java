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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static class HistogramObject{
        private HashMap<String, Integer> hist;

        public HistogramObject() { }

        public HashMap<String, Integer> getHist() {
            return hist;
        }

        public void setHist(HashMap<String, Integer> hist) {
            this.hist = hist;
        }

        public void addToHistByUser(String type) {
            if(type.equals(User.UserType.Christian1)||type.equals(User.UserType.Christian2)||type.equals(User.UserType.Christian3))
                hist.put( "Christian", hist.get(type) + 1);
            if(type.equals(User.UserType.Jewish1)||type.equals(User.UserType.Jewish2)||type.equals(User.UserType.Jewish3))
                hist.put( "Jewish", hist.get(type) + 1);
            if(type.equals(User.UserType.Muslim1)||type.equals(User.UserType.Muslim2)||type.equals(User.UserType.Muslim3))
                hist.put( "Muslim", hist.get(type) + 1);
            if(type.equals(User.UserType.Druze1) || type.equals(User.UserType.Druze2) || type.equals(User.UserType.Druze3))
                hist.put( "Druze", hist.get(type) + 1);
            else
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
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);

        db.collection("Statistics").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for( QueryDocumentSnapshot documentSnapshot : task.getResult()){
                    setUp(documentSnapshot);
                }
                Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);

            }
        });

    }

    private void setUp(QueryDocumentSnapshot dc) {
        HistogramObject histogramObject = dc.toObject(HistogramObject.class);
        switch (dc.getId()){
            case "CountUsers":
                setUpAllUsersStatistics(histogramObject.getHist());
                break;
            case "FirstQuizHistogram_AcademicStaff":
                setUpBarChart(histogramObject.getHist(), R.id.titleBar1,"הסגל האקדמי", R.id.chartBar1);
                break;
            case "FirstQuizHistogram_AdministrativeStaff":
                setUpBarChart(histogramObject.getHist(), R.id.titleBar2,"הסגל המנהלי", R.id.chartBar2);
                break;
            case "FirstQuizHistogram_Students":
                setUpBarChart(histogramObject.getHist(), R.id.titleBar3,"הסטודנטים", R.id.chartBar3);
                break;

        }
    }

    private void setUpAllUsersStatistics(Map<String, Integer> hist) {
        final List<PieEntry> pieEntries = new ArrayList<>();
        for ( HashMap.Entry<String, Integer> entry : hist.entrySet()) {
            String key = updateTitle(entry.getKey());
            Integer value = entry.getValue();
            if (value > 0)
                pieEntries.add(new PieEntry(value, key));
        }

        TextView title1 = getView().findViewById(R.id.title0);
        title1.setText("התפלגות המשתתפים לפי דתות");
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setSelectionShift(10f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.35f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        //Get the Chart
        PieChart chart = (PieChart)getView().findViewById(R.id.chart0);
        chart.setDrawHoleEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setData(data);
        chart.animateY(1000);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.invalidate();
    }

    private static String updateTitle(String name){
        switch (name) {
            case "Jewish":
                return "יהודים";
            case "Christian":
                return "נוצרים";
            case "Muslim":
                return "מוסלמים";
            default:
                return "דרוזים";
        }
    }

    private void setUpBarChart(HashMap<String, Integer> hist, int title_id, String role, int chart_id) {
        List<BarEntry> barEntries = new ArrayList<>();

       final ArrayList<String> labels = new ArrayList<>(hist.keySet());
        Collections.sort(labels);

        for (int i = 0; i < labels.size(); i++){
            int label = getLabel(labels.get(i));
            int value = hist.get(labels.get(i));
            if (value > 0 ){
                barEntries.add(new BarEntry(label, value, label));
            }
        }


        TextView title = getView().findViewById(title_id);
        title.setText("אחוז התשובות הנכונות בקרב "+ role);
        BarDataSet dataSet = new BarDataSet(barEntries, "אחוזים");
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
//        dataSet.setStackLabels(labels.toArray(new String[labels.size()]));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(8f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(2f); // set custom bar width


        //Get the Chart
        BarChart chart = (BarChart)getView().findViewById(chart_id);
        chart.setData(data);
        chart.setFitBars(true);
        chart.animateY(1000);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawBorders(false);
        chart.getXAxis().setLabelRotationAngle(0);
        chart.setTouchEnabled(false);

        //X AXIS
        XAxis xl = chart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(false);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setAxisMinimum(0);
        xl.setAxisMaximum(100);
        xl.setLabelCount(10);
        xl.isForceLabelsEnabled();
        xl.setLabelRotationAngle(0);
        labels.add("0");
        Collections.sort(labels);
        xl.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0) {
                    return labels.get((int) value/10);
                }
                return "";
            }
        });

        //Y AXIS
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(true);

        chart.invalidate();
    }

    private int getLabel(String key) {
        String[] split = key.split("-");
        return Integer.parseInt(split[0]) + 10;
    }

}
