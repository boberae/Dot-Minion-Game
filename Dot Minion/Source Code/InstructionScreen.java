package com.nocompany.bober.myfirstapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by bober on 10/2/2016.
 */
public class InstructionScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

    }

    public void returnToMenu(View v){
        setContentView(R.layout.activity_main);
    }
}
