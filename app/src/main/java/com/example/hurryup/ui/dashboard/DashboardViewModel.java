package com.example.hurryup.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {

    // PieChart 데이터를 관리하기 위한 MutableLiveData
    private MutableLiveData<List<PieEntry>> pieChartData;
    // BarChart 데이터를 관리하기 위한 MutableLiveData
    private MutableLiveData<List<BarEntry>> barChartData;

    public LiveData<List<PieEntry>> getPieChartData() {
        if (pieChartData == null) {
            pieChartData = new MutableLiveData<>();
            loadPieChartData(); // 초기 데이터 로드
        }
        return pieChartData;
    }

    public LiveData<List<BarEntry>> getBarChartData() {
        if (barChartData == null) {
            barChartData = new MutableLiveData<>();
            loadBarChartData(); // 초기 데이터 로드
        }
        return barChartData;
    }

    private void loadPieChartData() {
        // PieChart 데이터를 불러오는 로직을 작성
        // 예시 데이터 사용
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1,1));
        entries.add(new PieEntry(2,2));
        entries.add(new PieEntry(3,3));
        entries.add(new PieEntry(4,4));
        entries.add(new PieEntry(5,5));
        entries.add(new PieEntry(6,6));
        entries.add(new PieEntry(7,7));
        pieChartData.setValue(entries);
    }

    private void loadBarChartData() {
        // BarChart 데이터를 불러오는 로직을 작성
        // 예시 데이터 사용
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1,70));
        entries.add(new BarEntry(2,40));
        entries.add(new BarEntry(3,60));
        entries.add(new BarEntry(4,87));
        entries.add(new BarEntry(5,42));
        entries.add(new BarEntry(6,53));
        entries.add(new BarEntry(7,10));
        barChartData.setValue(entries);
    }

    // PieChart 및 BarChart 데이터를 업데이트하는 메서드
    public void updatePieChartData(List<PieEntry> updatedData) {
        pieChartData.setValue(updatedData);
    }

    public void updateBarChartData(List<BarEntry> updatedData) {
        barChartData.setValue(updatedData);
    }
}
