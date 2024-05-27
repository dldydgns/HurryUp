package com.example.hurryup.ui.dashboard;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.hurryup.database.Converters;
import com.example.hurryup.database.StateCount;
import com.example.hurryup.database.User;
import com.example.hurryup.database.UserRepository;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {
    UserRepository userRepository;
    // PieChart 데이터를 관리하기 위한 MutableLiveData
    private MutableLiveData<List<PieEntry>> pieChartData;
    // BarChart 데이터를 관리하기 위한 MutableLiveData
    private MutableLiveData<List<BarEntry>> barChartData;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

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
        userRepository.getTodayStateCount().observeForever(stateCounts -> {
            if (stateCounts != null && !stateCounts.isEmpty()) {
                List<PieEntry> entries = new ArrayList<>();
                for (StateCount stateCount : stateCounts) {
                    entries.add(new PieEntry(stateCount.count, stateCount.state));
                }

                // 라벨 설정
                for (PieEntry entry : entries) {
                    switch ((int) entry.getData()) {
                        case 1: entry.setLabel("좌측 전방"); break;
                        case 2: entry.setLabel("전방"); break;
                        case 3: entry.setLabel("우측 전방"); break;
                        case 4: entry.setLabel("좌측"); break;
                        case 5: entry.setLabel("정자세"); break;
                        case 6: entry.setLabel("우측"); break;
                        case 7: entry.setLabel("좌측 후방"); break;
                        case 8: entry.setLabel("후방"); break;
                        case 9: entry.setLabel("우측 후방"); break;
                        default: break;
                    }
                }

                // PieChart 데이터 설정
                pieChartData.setValue(entries);
            }
        });
    }


    private void loadBarChartData() {
        long currentTimestamp = Converters.dateToTimestamp(new Date());
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {
            final int dayOfWeek = i;
            userRepository.getStateCountFromDay(0, currentTimestamp, dayOfWeek).observeForever(count -> {
                entries.add(new BarEntry(dayOfWeek, count));
                if (entries.size() == 7) {
                    // 모든 요일의 데이터를 가져온 후에 BarChartData 업데이트
                    barChartData.setValue(entries);
                }
            });
        }
    }
}
