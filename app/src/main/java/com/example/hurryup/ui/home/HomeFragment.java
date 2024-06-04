package com.example.hurryup.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hurryup.R;
import com.example.hurryup.database.Converters;
import com.example.hurryup.database.User;
import com.example.hurryup.database.UserRepository;
import com.example.hurryup.databinding.FragmentHomeBinding;

import java.util.Date;
import java.util.Random;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public static boolean Haptic;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Haptic = false;

        // Circle의 위치를 바인딩하여 변경사항을 관찰
        homeViewModel.getCircleBias().observe(getViewLifecycleOwner(), bias -> {
            // ConstraintLayout.LayoutParams를 가져와서 bias 설정
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.circleView.getLayoutParams();
            layoutParams.horizontalBias = (float) bias.x;
            layoutParams.verticalBias = (float) bias.y;

            // 애니메이션 적용
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(1000); // 애니메이션 지속 시간 설정 (300ms)

            // TransitionManager를 사용하여 애니메이션 적용
            TransitionManager.beginDelayedTransition(binding.constraintLayout, autoTransition);

            // Circle의 위치를 업데이트
            binding.circleView.setLayoutParams(layoutParams);

            // bias.x와 bias.y가 모두 0.5가 아니면 rectangleView의 배경색과 테두리 색상 변경
            if (bias.x == 0.5 && bias.y == 0.5) {
                GradientDrawable drawable = (GradientDrawable) binding.rectangleView.getBackground();
                drawable.setColor(ContextCompat.getColor(requireContext(), R.color.cushion));
                drawable.setStroke(10, ContextCompat.getColor(requireContext(), R.color.cushionline));
            }
            else {
                GradientDrawable drawable = (GradientDrawable) binding.rectangleView.getBackground();
                drawable.setColor(ContextCompat.getColor(requireContext(), R.color.cushion_warning));
                drawable.setStroke(10, ContextCompat.getColor(requireContext(), R.color.cushionline_warning));
            }
        });

        // 스위치의 상태 변화 감지 리스너 설정
        binding.visibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 스위치가 ON일 때
                if (isChecked) {
                    binding.phoneImageView.setImageResource(R.drawable.haptic);
                    binding.switchStatusTextView.setText("ON");
                    Haptic = true;
                } else { // 스위치가 OFF일 때
                    binding.phoneImageView.setImageResource(R.drawable.normal);
                    binding.switchStatusTextView.setText("OFF");
                    Haptic = false;
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}