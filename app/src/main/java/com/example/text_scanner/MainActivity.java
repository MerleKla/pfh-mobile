package com.example.text_scanner;
// github right click, git, add, then commit (first try/second try/final commit), then git, push https://github.com/MerleKla/pfh-mobile.git
// API: Firebase ML Kit
// Code Tutorials: https://www.youtube.com/watch?v=wFHR-dR7TpQ Min.20
// https://www.youtube.com/watch?v=EcfUkjlL9RI
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button startScan;

    /**
     *Initialisiserungscode für die ClickListener
     * @param savedInstanceState Speichern des Zustandes der Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startScan = findViewById(R.id.startScanButton);
        startScan.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this,ScannerActivity.class);
                startActivity(i);
            }
        });
    }
}