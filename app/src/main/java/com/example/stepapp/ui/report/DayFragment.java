package com.example.stepapp.ui.report;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DayFragment extends Fragment {

    public int todaySteps = 0;
    TextView numStepsTextView;
    AnyChartView anyChartView;

    Date cDate = new Date();

    public Map<String, Integer> stepsByDay = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        View root = inflater.inflate(R.layout.fragment_day, container, false);

        // Create column chart
        anyChartView = root.findViewById(R.id.dayBarChart);
        anyChartView.setProgressBar(root.findViewById(R.id.loadingBar));

        Cartesian cartesian = createColumnChart();
        anyChartView.setBackgroundColor("#00000000");
        anyChartView.setChart(cartesian);

        return root;
    }

    /**
     * Utility function to create the column chart
     *
     * @return Cartesian: cartesian with column chart and data
     */
    //
    public Cartesian createColumnChart(){
        //***** Read data from SQLiteDatabase *********/
        // Get the map with day and number of steps
        //  from the database and initialize it to variable stepsByDay
        stepsByDay = StepAppOpenHelper.loadStepsByDay(getContext());

        // Put the number of steps for each date in graph_map
        // from the number of steps read from the database
        Map<String, Integer> graph_map = new TreeMap<>(stepsByDay);

        //***** Create column chart using AnyChart library *********/
        // 1. Create and get the cartesian coordinate system for column chart
        Cartesian cartesian = AnyChart.column();

        // 2. Create data entries for x and y axis of the graph
        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        // 3. Add the data to column chart and get the columns
        Column column = cartesian.column(data);

        //***** Modify the UI of the chart *********/
        // Change the color of column chart and its border
        column.fill("#1EB980");
        column.stroke("#1EB980");

        //Modifying properties of tooltip
        column.tooltip()
                .titleFormat("At day: {%X}")
                .format("{%Value}{groupsSeparator: } Steps")
                .anchor(Anchor.RIGHT_TOP);

        // Modify column chart tooltip properties
        column.tooltip()
                .position(Position.RIGHT_TOP)
                .offsetX(0d)
                .offsetY(5);

        // Modifying properties of cartesian
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.yScale().minimum(0);

        // Modify the UI of the cartesian
        cartesian.yAxis(0).title("Number of steps");
        cartesian.xAxis(0).title("Day");
        cartesian.background().fill("#00000000");
        cartesian.animation(true);

        return cartesian;
    }

}