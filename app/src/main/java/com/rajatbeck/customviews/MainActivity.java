package com.rajatbeck.customviews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Random random = new Random();
    GoalProgressBar mGoalProgress;
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoalProgress = (GoalProgressBar) findViewById(R.id.progressBar);
        resetButton = (Button) findViewById(R.id.reset_btn);
        mGoalProgress.setGoal(60);
        if (savedInstanceState == null) {
            resetProgress();
        }
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetProgress();
            }
        });

    }

    public void resetProgress() {
        int prog = random.nextInt(100);
        mGoalProgress.setProgress(prog);

    }
}
