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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

;import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//Code Grundlagen: https://codelabs.developers.google.com/codelabs/mlkit-android#6
//Code Tutorial: https://www.youtube.com/watch?v=wFHR-dR7TpQ Min.20
//Problembehandlung und Recherche via: https://developers.google.com/ml-kit/vision/text-recognition/v2/android?hl=de
//Die Funktion textSpeichern() wurde mit Hilfe von ChatGPT erarbeitet

public class ScannerActivity extends AppCompatActivity {


    private ImageView erfassenIV;
    private TextView resultatTV;
    private Button aufnehmenBtn, erfassenBtn, speichernBtn;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    EditText mEditText;



    /**
     *Initialisiserungscode für die GUI und für die ClickListener
     *
     * @param savedInstanceState Speichern des Zustandes der Activity
     */
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        //Initialisierung der Elemente der Benutzeroberfläche
        erfassenIV = findViewById(R.id.idIVLogo1); //Foto
        resultatTV = findViewById(R.id.textView);   //hier wird der Text angezeigt
        aufnehmenBtn = findViewById(R.id.startScanButton2);
        erfassenBtn = findViewById(R.id.button2);
        speichernBtn = findViewById(R.id.safeButton);


        /**
         *Ereignis, wenn "Aufnehmen" Button gedrückt wird. Prüft die Kameraerlaubnis und erfasst ein Bild oder fordert die Erlaubnis neu an
         * @param v die view auf die geklickt wurde
         *
         */
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


        /**
         *Ereignis, wenn "Text von Bild scannen" Button gedrückt wird. Ruft die detectText() Methode auf.
         * @param v die view auf die geklickt wurde
         *
         */
        erfassenBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                detectText();

            }
        });


        /**
         *Ereignis wenn "Speichern" Button gedrückt wird. Ruft die textSpeichern() Methode auf.
         * @param v die view auf die geklickt wurde
         *
         */
        speichernBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSpeichern();
            }
        });
    }

    /**
     *Überprüfen der Kameraberechtigung
     * @return boolean camPermission Gibt zurück, ob die Erlaubnis erteilt ist
     *
     */

    private boolean checkPermission(){
        int camPermission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return camPermission == PackageManager.PERMISSION_GRANTED;
    }


    /**
     *Anfordern der Kameraberechtigung
     */
    private void requestPermission(){
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this,new String[]{CAMERA}, PERMISSION_CODE);
    }


    /**
     * Kamera öffnen und Bildaufnahme ermöglichen
     */
    private void bildErfassen(){ //ab if von Hand geschrieben
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }


    /**
     * Feststellen, ob die Berechtigung erteilt wurde oder nicht und darüber informieren
     *  @param requestCode Der Anforderungscode, der bei der Berechtigungsanfrage verwendet wurde.
     *  @param permissions Das Array der angeforderten Berechtigungen.
     *  @param grantResults Ein Array, das das Ergebnis der Berechtigungsanfrage für jede angeforderte Berechtigung enthält.
     */
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

    /**
     * Aufgenommenes Bild weiterverarbeiten. Wird aufgerufen, um auf das Ergebnis einer Aktivität für die Bildaufnahme zu reagieren.
     *
     * @param requestCode Der Anforderungscode, der bei der Aktivitätsanforderung verwendet wurde.
     * @param resultCode Der Ergebniscode, der den Erfolg oder das Scheitern der Aktivität anzeigt.
     * @param data Die Intent-Daten, die das Ergebnis der Aktivität enthalten, einschließlich des aufgenommenen Bilds.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            erfassenIV.setImageBitmap(imageBitmap);
        }
    }


    /**
     * MLKIt relevante Klasse, Virtual Machine kommt zum Einsatz
     * Methode zur Texterkennung
     */
    private void detectText() {
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);//im Video normal int - nicht degree
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text>result=recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
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
        //Hinweisen auf Scheitern der Texterkennung
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScannerActivity.this, "Texterkennung fehlgeschlagen"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Methode zum Speichern des aufgenommenden Textes
     */
     private void textSpeichern() {
        TextView textView = findViewById(R.id.textView);
        String textToSave = textView.getText().toString();

        File downloadsVerzeichnis = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        //Verzeichnis erstellen, falls es noch nicht existiert
        if (!downloadsVerzeichnis.exists()) {
            if (downloadsVerzeichnis.mkdirs()) {
                Log.d("Verzeichnis erstellt", "Das Verzeichnis wurde erstellt");
            } else {
                Log.e("Verzeichnis erstellen", "Fehler beim Erstellen des Verzeichnisses");
            }
        }

        if (downloadsVerzeichnis.exists()) {
            File datei = new File(downloadsVerzeichnis, "recentScan.txt");
            try {
                FileOutputStream fos = new FileOutputStream(datei);
                fos.write(textToSave.getBytes());
                fos.close();
                Toast.makeText(this, "Scan im externen Downloads-Verzeichnis gespeichert", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Fehler beim Speichern der Datei", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Downloads-Verzeichnis im externen Speicher konnte nicht erstellt werden", Toast.LENGTH_SHORT).show();
        }
    }

}