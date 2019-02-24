package com.example.furkan.sonproje;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    ArrayList<String> notListe = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView liste;
    public static int a = 0;
    public static int alertID;
    TextView textView;
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.not_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    //üç nokta olusturma kısmı
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_menu) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        liste = findViewById(R.id.liste);
        db = this.openOrCreateDatabase("notDB", MODE_PRIVATE, null);
        db.execSQL("create table if not exists notlar(id integer not null primary key autoincrement,baslik varchar,detay varchar,tarih date,saat time,hatirlatma varchar)");
        //db.execSQL("drop table notlar");
        //db.execSQL("drop table kuyruk");
        //db.execSQL("insert into notlar(baslik, detay, tarih,saat,hatirlatma)values('baslik','detay','2018 12 02','17:20:00','30')");

        Cursor cursor = db.rawQuery("select baslik from notlar", null);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                notListe.add(cursor.getString(0));

            } while (cursor.moveToNext());
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notListe);
            liste.setAdapter(adapter);
        }
        //Alert Dialog Kısmı
        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int pos = position + 1;
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("İşlem seçin");
                builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Cursor cursor = db.rawQuery("Select id from notlar", null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                do {
                                    a++;
                                    if (pos == a) {
                                        alertID = Integer.parseInt(cursor.getString(0));
                                        String sql = String.format("Delete from notlar where id = " + alertID, null);
                                        db.execSQL(sql);
                                        notListe.remove(position);
                                        liste.invalidateViews();
                                        return;
                                    }

                                } while (cursor.moveToNext());
                            }
                        } catch (Exception ex) {
                        }
                    }
                });
                AlertDialog.Builder güncelle = builder.setNeutralButton("Güncelle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), DetayActivity.class);
                        startActivity(intent);
                        Cursor cursor = db.rawQuery("Select id from notlar", null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            do {
                                a++;
                                if (pos == a) {
                                    alertID = Integer.parseInt(cursor.getString(0));
                                    return;
                                }
                            } while (cursor.moveToNext());


                        }
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.show();
            }
        });

    }
}
