package com.example.hurryup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SpActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moveMain(2);	//2초 후 main activity 로 넘어감
    }

    private void moveMain(int sec) {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //new Intent(현재 context, 이동할 activity)
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);


                finish();	//현재 액티비티 종료
            }
        }, 1000 * sec); // sec초 정도 딜레이를 준 후 시작
    }


}
