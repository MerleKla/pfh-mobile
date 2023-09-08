package com.example.text_scanner;

import static android.Manifest.permission.CAMERA;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

;import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ScannerActivity extends AppCompatActivity {

    //Code von youtube https://www.youtube.com/watch?v=wFHR-dR7TpQ Min.20
    private ImageView erfassenIV;  //captureIV
    private TextView resultatTV;
    private Button aufnehmenBtn, erfassenBtn, speichernBtn;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    EditText mEditText;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        erfassenIV = findViewById(R.id.idIVLogo1); //Foto
        resultatTV = findViewById(R.id.textView);   //hier wird der Text angezeigt
        aufnehmenBtn = findViewById(R.id.startScanButton2);
        erfassenBtn = findViewById(R.id.button2);
        speichernBtn = findViewById(R.id.safeButton);


        //mEditText = findViewById(R.id.safeButton);
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

        speichernBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Methode zum erstellen und speichern des Textes --> detect text methode muss genutzt und abgespeichert werden
                textSpeichern();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            erfassenIV.setImageBitmap(imageBitmap);
        }
    }

    //MLKIt relevante Klasse -> VM
    private void detectText() {
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);//im Video normal int - nicht degree
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text>result=recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) { //evtl muss hier nicht @NonNull vor Text text
                StringBuilder result = new StringBuilder();
                for(Text.TextBlock block: text.getTextBlocks()){
                    String blockText = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for(Text.Line line : block.getLines()){
                        String lineText = line.getText();
                        Point[] lineCornerPoint = line.getCornerPoints();
                        Rect linRect = line.getBoundingBox();
                        for(Text.Element element: line.getElements()){
                            String elementText = element.getText();
                            result.append(elementText);

                        }
                        resultatTV.setText(blockText);

                    }
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScannerActivity.this, "Texterkennung fehlgeschlagen"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }

    //Codegrundlage von ChatGPT:
    private void textSpeichern() {

        TextView textView = findViewById(R.id.textView);
        String textToSave = textView.getText().toString();
        String dateiName = "Download\recentScan.txt";

        try {
            FileOutputStream fos = openFileOutput(dateiName, Context.MODE_PRIVATE);
            fos.write(textToSave.getBytes());
            fos.close();

            Toast.makeText(this, "Scan in downloads gespeichert", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, "Fehler beim Speichern der Datei", Toast.LENGTH_SHORT).show();
        }
    }

}


