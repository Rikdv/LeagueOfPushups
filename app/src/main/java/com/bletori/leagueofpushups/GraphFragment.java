package com.bletori.leagueofpushups;

/**
 * Created by Woonkamer on 21-2-2016.
 */
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GraphFragment extends Fragment implements GameActivity.UpdateableFragment {
    com.github.mikephil.charting.charts.HorizontalBarChart chart;
    @Override
    public void update(ArrayList<HashMap<String, Object>> dat, ArrayList<HashMap<String, Object>> dat2) {
        HashMap dealt = new HashMap();
        HashMap taken = new HashMap();
        HashMap names = new HashMap();
        for(int i = 0; i < 5; i++){
            HashMap<String, Object> inf = dat.get(i);
            dealt.put(i, inf.get("dealt"));
            taken.put(i, inf.get("taken"));
            names.put(i, inf.get("champ"));
        }

        for(int i = 0; i < 5; i++){
            HashMap<String, Object> inf = dat2.get(i);
            dealt.put(i + 5, inf.get("dealt"));
            taken.put(i + 5, inf.get("taken"));
            names.put(i + 5, inf.get("champ"));
        }

        BarData data = new BarData(getXAxisValues(names), getDataSet(dealt, taken));
        chart.setData(data);
        chart.invalidate();
        chart.setScaleEnabled(false);
        chart.setDescription("");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.graphfragment, container, false);
        chart = (com.github.mikephil.charting.charts.HorizontalBarChart)view.findViewById(R.id.chart);
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        return view;
    }

    private ArrayList<IBarDataSet> getDataSet(HashMap dealt, HashMap taken) {
        ArrayList<IBarDataSet> dataSets;
        // DAMAGE DEALT
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(Integer.parseInt(dealt.get(0).toString()), 9); // Player 1
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(Integer.parseInt(dealt.get(1).toString()), 8); // Player 2
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(Integer.parseInt(dealt.get(2).toString()), 7); // Player 3
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(Integer.parseInt(dealt.get(3).toString()), 6); // Player 4
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(Integer.parseInt(dealt.get(4).toString()), 5); // Player 5
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(Integer.parseInt(dealt.get(5).toString()), 4); // Player 6
        valueSet1.add(v1e6);
        BarEntry v1e7 = new BarEntry(Integer.parseInt(dealt.get(6).toString()), 3); // Player 7
        valueSet1.add(v1e7);
        BarEntry v1e8 = new BarEntry(Integer.parseInt(dealt.get(7).toString()), 2); // Player 8
        valueSet1.add(v1e8);
        BarEntry v1e9 = new BarEntry(Integer.parseInt(dealt.get(8).toString()), 1); // Player 9
        valueSet1.add(v1e9);
        BarEntry v1e10 = new BarEntry(Integer.parseInt(dealt.get(9).toString()), 0); // Player 10
        valueSet1.add(v1e10);

        // DAMAGE TAKEN
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(Integer.parseInt(taken.get(0).toString()), 9); // Player 1
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(Integer.parseInt(taken.get(1).toString()), 8); // Player 2
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(Integer.parseInt(taken.get(2).toString()), 7); // Player 3
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(Integer.parseInt(taken.get(3).toString()), 6); // Player 4
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(Integer.parseInt(taken.get(4).toString()), 5); // Player 5
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(Integer.parseInt(taken.get(5).toString()), 4); // Player 6
        valueSet2.add(v2e6);
        BarEntry v2e7 = new BarEntry(Integer.parseInt(taken.get(6).toString()), 3); // Player 7
        valueSet2.add(v2e7);
        BarEntry v2e8 = new BarEntry(Integer.parseInt(taken.get(7).toString()), 2); // Player 8
        valueSet2.add(v2e8);
        BarEntry v2e9 = new BarEntry(Integer.parseInt(taken.get(8).toString()), 1); // Player 9
        valueSet2.add(v2e9);
        BarEntry v2e10 = new BarEntry(Integer.parseInt(taken.get(9).toString()), 0); // Player 10
        valueSet2.add(v2e10);

        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Damage taken");
        barDataSet2.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Damage dealt");
        barDataSet1.setColor(Color.rgb(155, 0, 0));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        Collections.reverse(dataSets);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues(HashMap names) {
        ArrayList<String> xAxis = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            xAxis.add(names.get(i).toString());
        }
        Collections.reverse(xAxis);
        return xAxis;
    }

}