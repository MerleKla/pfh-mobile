package com.example.text_scanner;

import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;

import org.w3c.dom.Text;


public class ScannerActivity extends AppCompatActivity {

    //Code von youtube https://www.youtube.com/watch?v=wFHR-dR7TpQ Min.20
    private ImageView erfassenIV;  //captureIV
    private TextView resultatTV;
    private Button aufnehmenBtn, erfassenBtn;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        erfassenIV = findViewById(R.id.idIVLogo);
        resultatTV = findViewById(R.id.textView);
        aufnehmenBtn = findViewById(R.id.startScanButton2);
        erfassenBtn = findViewById(R.id.button2);

        erfassenBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                detectText();

            }
        });

        aufnehmenBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
              if(checkPermission()){
                   bildErfassen();
               }
               else {
                   requestPermission();
               }
            }
        });

    }

    private boolean checkPermission(){
        int camPermission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return camPermission == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this,new String[]{CAMERA}, PERMISSION_CODE);
    }

    private void bildErfassen(){ //ab if von Hand geschrieben
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(cameraPermission){
                Toast.makeText(this, "Erlaubnis erteilt", Toast.LENGTH_SHORT).show();
                bildErfassen();
            }
            else{
                Toast.makeText(this, "Erlaubnis verweigert", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            erfassenIV.setImageBitmap(imageBitmap);
        }
    }

    private void detectText() {
        //InputImage image = InputImage.fromBitmap(1); //im Video normal int - nicht degree
        //TextRegnizer recognizer = TextRecognition...
        //Task<Text>result=recognizer.process(image).addOnSuccessListener


    }

}


