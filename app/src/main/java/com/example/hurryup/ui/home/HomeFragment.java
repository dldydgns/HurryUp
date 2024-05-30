package com.example.hurryup.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hurryup.R;
import com.example.hurryup.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView switchStatusTextView; // 스위치 상태
    private View circleView;
    private float lastX, lastY;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        circleView = root.findViewById(R.id.circleView);

        // 데이터베이스에서 작은 원의 위치 가져오기
        float circleX = 2;
        float circleY = 2;

        movCricleTo(circleX, circleY);

        switchStatusTextView = root.findViewById(R.id.switchStatusTextView);

        Switch visibilitySwitch = binding.visibilitySwitch;
        visibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchStatusTextView.setText("ON");
            } else {
                switchStatusTextView.setText("OFF");
            }
        });


        return root;
    }

    private void movCricleTo(float x, float y){
        // 사각형 범위 내에서만 움직일 수 있도록 제한
        float minX = binding.rectangleView.getX();
        float maxX = minX + binding.rectangleView.getWidth() - circleView.getWidth();
        float minY = binding.rectangleView.getY();
        float maxY = minY + binding.rectangleView.getHeight() - circleView.getHeight();

        x = Math.max(minX, Math.min(x, maxX));
        y = Math.max(minY, Math.min(y, maxY));

        // 새로운 위치로 작은 원 이동
        circleView.setX(x);
        circleView.setY(y);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}