package com.example.hurryup.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.hurryup.database.Converters;
import com.example.hurryup.database.User;
import com.example.hurryup.database.UserRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment {
    DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    final static int[] minPieColor = {20, 194, 163};
    final static int[] maxPieColor = {213, 245, 239};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    private void setupDailyPieChart(FragmentDashboardBinding binding, List<PieEntry> entries, float textSize){
        // 차트 뷰를 참조
        PieChart pieChart = binding.chartDay;

        PieData pieData = new PieData();

        // 백분율로 표기
        pieChart.setUsePercentValues(true);

        //색상 설정
        List<Integer> colors = new ArrayList<>();

        Log.d("asd",entries.toString());

        // 항목의 총 합 계산
        float sum = 0f;
        for (PieEntry entry : entries) {
            sum += entry.getValue();
        }

        // 데이터 정렬
        Collections.sort(entries, (entry1, entry2) -> Float.compare(entry2.getValue(), entry1.getValue()));

        // 각 항목에 대한 색상 설정 및 데이터셋에 추가
        for(int i = 0; i < entries.size(); i++){
            // 항목별로 i번째 값이 커질수록 최대값에 근접하도록 설정
            float percentage = (float) i / entries.size();

            // 색상을 항목별로 i번째 값이 커질수록 최대값에 근접하도록 조절하여 설정
            int redValue = (int) (minPieColor[0] + ((maxPieColor[0] - minPieColor[0]) * percentage));
            int greenValue = (int) (minPieColor[1] + ((maxPieColor[1] - minPieColor[1]) * percentage));
            int blueValue = (int) (minPieColor[2] + ((maxPieColor[2] - minPieColor[2]) * percentage));

            int color = Color.rgb(redValue, greenValue, blueValue); // R: 최소값 + ((최대값 - 최소값) * i / 전체 항목 수), G: 최소값 + ((최대값 - 최소값) * i / 전체 항목 수), B: 최소값 + ((최대값 - 최소값) * i / 전체 항목 수)
            colors.add(color);
        }

        // 데이터셋 설정
        PieDataSet pieDataSet = new PieDataSet(entries, "Daily Chart");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(textSize);

        // 차트에 데이터 적용
        pieData.setDataSet(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

        // 차트의 상호작용 및 표시 설정
        pieChart.setTouchEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
    }

    private void setupWeeklyBarChart(FragmentDashboardBinding binding, List<BarEntry> entries, boolean showBaseline, float textSize, float barWidth) {
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
        BarDataSet barDataSet = new BarDataSet(entries, "Weekly Chart");
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
    public void onResume() {
        super.onResume();

        // LiveData를 관찰하고 데이터가 변경될 때마다 차트 업데이트
        dashboardViewModel.getPieChartData().observe(getViewLifecycleOwner(), entries -> {
            // PieChart 업데이트
            this.setupDailyPieChart(binding, entries, 10);
        });
        dashboardViewModel.getBarChartData().observe(getViewLifecycleOwner(), entries -> {
            // BarChart 업데이트
            this.setupWeeklyBarChart(binding, entries, false, 10, 0.75f);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
