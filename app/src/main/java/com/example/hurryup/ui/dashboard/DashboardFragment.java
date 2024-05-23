package com.example.hurryup.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hurryup.R;
import com.example.hurryup.databinding.FragmentDashboardBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final BarChart barchart = binding.chartDay;
        //dashboardViewModel.drawChart().observe(getViewLifecycleOwner(), barchart::);

        ArrayList<PieEntry> day_chart = new ArrayList<>();
        day_chart.add(new PieEntry(1,400));
        day_chart.add(new PieEntry(2,20));
        day_chart.add(new PieEntry(3,600));
        day_chart.add(new PieEntry(4,402));
        day_chart.add(new PieEntry(5,545));
        day_chart.add(new PieEntry(6,243));
        day_chart.add(new PieEntry(7,700));

        setupDailyPieChart(binding, R.id.chart_day, day_chart, false, 10);


        ArrayList<BarEntry> week_chart = new ArrayList<>();
        week_chart.add(new BarEntry(1,70));
        week_chart.add(new BarEntry(2,40));
        week_chart.add(new BarEntry(3,60));
        week_chart.add(new BarEntry(4,87));
        week_chart.add(new BarEntry(5,42));
        week_chart.add(new BarEntry(6,53));
        week_chart.add(new BarEntry(7,10));

        setupWeeklyBarChart(binding, R.id.chart_week, week_chart, false, 10,0.75f);

        return root;
    }

    public static void setupDailyPieChart(FragmentDashboardBinding binding, int chartId, List<PieEntry> entries, boolean showBaseline, float textSize){
        // 차트 뷰를 참조
        PieChart pieChart = binding.chartDay;

        PieData pieData = new PieData();

        // 백분율로 표기
        pieChart.setUsePercentValues(true);

        //색상 설정
        List<Integer> colors = new ArrayList<>();

        int sum = 0;
        for (PieEntry entry : entries) { sum += entry.getY(); }

        for(PieEntry entry : entries){
            float weight = 255*(entry.getY()/sum)/2;
            colors.add(Color.rgb(26*weight,252*weight,211*weight));
        }

        // 데이터셋 설정 및 차트에 데이터 추가
        PieDataSet pieDataSet = new PieDataSet(entries, "piedataset");
        pieDataSet.setColors(colors);  // 데이터셋의 색상 설정
        pieDataSet.setValueTextSize(textSize); // 각 항목의 텍스트 크기 설정
        pieDataSet.setSliceSpace(3f);
        pieData.addDataSet(pieDataSet);  // 데이터 추가
        pieChart.setData(pieData);  // 차트에 데이터 적용
        pieChart.invalidate();  // 차트를 다시 그림

        // 차트의 상호작용 및 표시 설정
        pieChart.setTouchEnabled(false);  // 터치 인터랙션 비활성화
        pieChart.getDescription().setEnabled(false);  // 설명 비활성화
    }

    public static void setupWeeklyBarChart(FragmentDashboardBinding binding, int chartId, List<BarEntry> entries, boolean showBaseline, float textSize, float barWidth) {
        // 차트 뷰를 참조
        BarChart barChart = binding.chartWeek;

        BarData barData = new BarData();

        // 가장 높은 값을 찾음
        float maxValue = Collections.max(entries, Comparator.comparing(BarEntry::getY)).getY();

        // 각 항목에 대한 색상을 설정. 가장 높은 값은 다른 색상으로 표시.
        List<Integer> colors = new ArrayList<>();
        for (BarEntry entry : entries) {
            if (entry.getY() == maxValue) {
                colors.add(Color.parseColor("#14c2a3"));  // 최대 값일 때의 색상
            } else {
                colors.add(Color.LTGRAY);  // 그 외의 값의 색상
            }
        }

        // 차트 하단 기준선 표시
        if (showBaseline) {
            LimitLine ll = new LimitLine(0f);
            ll.setLineColor(Color.parseColor("#f2f2f2"));  // 기준선의 색상
            ll.setLineWidth(2f);  // 기준선의 두께

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.addLimitLine(ll);  // 왼쪽 축에 기준선 추가
        }

        // X축 포맷
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value){
                String days[] = new String[]{"일","월","화","수","목","금","토"};

                return days[(int)value - 1];
            }
        });

        // 데이터셋 설정 및 차트에 데이터 추가
        BarDataSet barDataSet = new BarDataSet(entries, "bardataset");
        barDataSet.setColors(colors);  // 데이터셋의 색상 설정
        barDataSet.setValueTextSize(textSize); // 각 항목의 텍스트 크기 설정
        barData.addDataSet(barDataSet);  // 데이터 추가
        barChart.setData(barData);  // 차트에 데이터 적용
        barChart.invalidate();  // 차트를 다시 그림

        // 바의 너비 설정
        barData.setBarWidth(barWidth);

        // 바의 최대값 설정
        barChart.getAxisLeft().setAxisMaximum(100f);

        // 차트의 상호작용 및 표시 설정
        barChart.setTouchEnabled(false);  // 터치 인터랙션 비활성화
        barChart.getDescription().setEnabled(false);  // 설명 비활성화
        barChart.setDrawGridBackground(false);  // 그리드 배경 비활성화
        barChart.getXAxis().setDrawGridLines(false);  // X축 그리드 선 비활성화
        barChart.getAxisLeft().setDrawGridLines(false);  // 왼쪽 축 그리드 선 비활성화
        barChart.getAxisRight().setDrawGridLines(false);  // 오른쪽 축 그리드 선 비활성화
        barChart.getXAxis().setDrawLabels(true);  // X축 라벨 비활성화
        barChart.getAxisLeft().setDrawLabels(false);  // 왼쪽 축 라벨 비활성화
        barChart.getAxisRight().setDrawLabels(false);  // 오른쪽 축 라벨 비활성화
        barChart.getAxisRight().setEnabled(false);  // 오른쪽 축 비활성화
        barChart.getLegend().setEnabled(false);  // 범례 비활성화
        barChart.getAxisLeft().setDrawAxisLine(false);  // 왼쪽 축 선 비활성화
        barChart.getXAxis().setDrawAxisLine(false);  // X축 선 비활성화
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}