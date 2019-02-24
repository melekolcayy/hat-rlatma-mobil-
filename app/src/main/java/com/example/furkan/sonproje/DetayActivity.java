package com.example.furkan.sonproje;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DetayActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText baslikText, detayText, saatText, tarihText;
    Spinner animsatmaText;
    int notID = MainActivity.alertID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detay);
        addItemsOnSpinner();

        baslikText = findViewById(R.id.baslik_guncelle);
        detayText = findViewById(R.id.not_guncelle);
        tarihText = findViewById(R.id.tarih_guncelle);
        saatText = findViewById(R.id.saat_guncelle);
        animsatmaText = findViewById(R.id.hatırlat_spinner);
        db = this.openOrCreateDatabase("notDB", MODE_PRIVATE, null);

        try{
            Cursor cursor = db.rawQuery("Select * from notlar where id = " + notID, null);
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    baslikText.setText(cursor.getString(1));
                    detayText.setText(cursor.getString(2));
                    tarihText.setText(cursor.getString(3));
                    saatText.setText(cursor.getString(4));
                    //animsatmaText.setSelection(cursor.getInt(5));
                } while (cursor.moveToNext());
            }
        }catch(Exception ex){

        }

    }

    public void addItemsOnSpinner() {
        animsatmaText=findViewById(R.id.hatırlat_spinner);
        List<String> list = new ArrayList<String>();
        list.add("Kac dakika once hatirlatilsin? ");
        list.add("10");
        list.add("30");
        list.add("60");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animsatmaText.setAdapter(dataAdapter);
    }
    public void guncelle(View view){
        try{
            SQLiteDatabase db=this.openOrCreateDatabase("notDB", MODE_PRIVATE,
                    null);
            String sql=String.format("update notlar set baslik='%s',detay='%s',tarih='%s',saat='%s',hatirlatma='%s' where id = " + notID,
                    baslikText.getText().toString(),detayText.getText().toString(),tarihText.getText().toString(),
                    saatText.getText().toString(),animsatmaText.getSelectedItem().toString());
            db.execSQL(sql);
            Intent intent=new Intent(this, MainActivity.class);
            Toast.makeText(this, "Güncelleme Başarılı", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
