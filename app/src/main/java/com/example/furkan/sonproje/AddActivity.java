package com.example.furkan.sonproje;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddActivity extends AppCompatActivity {
    private static final int NOTIF_ID = 1;
    SQLiteDatabase db;
    Spinner hatirlatma;
    EditText not_yaz,saat_ayarla,tarih_ayarla,baslik_yaz;
    Button btn_kaydet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        addItemsOnSpinner();
        baslik_yaz=findViewById(R.id.baslik_yaz);
        not_yaz=findViewById(R.id.not_yaz);
        tarih_ayarla =findViewById(R.id.tarih_ayarla);
        saat_ayarla=findViewById(R.id.saat_ayarla);
        btn_kaydet = findViewById(R.id.btn_kaydet);

        timePicker();
        datePicker();

        //veritabanı tablo açma
        try {
            db = this.openOrCreateDatabase("notDB", MODE_PRIVATE,null);
            //db.execSQL("drop table notlar");
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    //erteleme zamanını secme
    public void addItemsOnSpinner() {
        hatirlatma=findViewById(R.id.hatırlat_spinner);
        List<String> list = new ArrayList<String>();
        list.add("Kac dakika once hatirlatilsin? ");
        list.add("10");
        list.add("30");
        list.add("60");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hatirlatma.setAdapter(dataAdapter);
    }

    //kaydetme butonu
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void kaydet(View view){
        try{
            SQLiteDatabase db=this.openOrCreateDatabase("notDB", MODE_PRIVATE,
                    null);
            msDonustur();
            db.execSQL("create table if not exists notlar(id integer not null primary key autoincrement,baslik varchar,detay varchar,tarih date,saat time,hatirlatma varchar)");
            //notlar tablosuna veri ekleme
            String sql=String.format("insert into notlar(baslik,detay,tarih,saat,hatirlatma"+
                            ")values('%s','%s','%s','%s','%s')", baslik_yaz.getText().toString(),not_yaz.getText().toString(),tarih_ayarla.getText().toString(),
                    saat_ayarla.getText().toString()+":00",hatirlatma.getSelectedItem().toString());
            db.execSQL("create table if not exists kuyruk(id integer not null primary key autoincrement,baslik varchar,detay varchar,kalan int)");
            //kuyruğa tablosuna veri ekleme
            String sql2=String.format("insert into kuyruk(baslik,detay,kalan"+
                            ")values('%s','%s','%s')", baslik_yaz.getText().toString(),not_yaz.getText().toString()
                    ,msDonustur());
            db.execSQL(sql);
            db.execSQL(sql2);
            Intent intent=new Intent(AddActivity.this, Alarm.class);
            PendingIntent p1=PendingIntent.getBroadcast(getApplicationContext(),0, intent,0);
            AlarmManager a=(AlarmManager)getSystemService(ALARM_SERVICE);

            a.set(AlarmManager.RTC,System.currentTimeMillis() + msDonustur(), p1);
            Intent intent2=new Intent(AddActivity.this, MainActivity.class);
            Toast.makeText(this, "Kayıt Başarılı", Toast.LENGTH_LONG).show();
            startActivity(intent2);

        }
        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long msDonustur(){
        Date date = new Date();

        SimpleDateFormat myDate = new SimpleDateFormat("yyyy MM dd");
        SimpleDateFormat myTime = new SimpleDateFormat("HH:mm:ss");
        myTime.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String gelenTarih = tarih_ayarla.getText().toString();
        String gelenSaat = saat_ayarla.getText().toString() + ":00";
        int once = Integer.parseInt(hatirlatma.getSelectedItem().toString());
        try {
            Date date1 = myDate.parse(myDate.format(date));
            Date date2 = myDate.parse(gelenTarih);
            Date time1 = myTime.parse(myTime.format(date));
            Date time2 = myTime.parse(gelenSaat);
            long hatirlat = TimeUnit.MILLISECONDS.convert(once, TimeUnit.MINUTES);
            long day = (date2.getTime() - date1.getTime());
            long time = (time2.getTime() - time1.getTime());
            long totalTime = (day + time) - hatirlat;
            return  totalTime;
        } catch (ParseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return 0;
        }
    }
    public void timePicker(){
        // Get open TimePickerDialog button.
        saat_ayarla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        saat_ayarla.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                boolean is24Hour = true;
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this, android.R.style.Theme_Holo_Light_Dialog, onTimeSetListener, hour, minute, is24Hour);
                timePickerDialog.show();
            }
        });
    }
    public void datePicker(){
        // Get open TimePickerDialog button.
        tarih_ayarla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new OnDateSetListener instance. This listener will be invoked when user click ok button in DatePickerDialog.
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append(year);
                        strBuf.append(" ");
                        strBuf.append(month + 1);
                        strBuf.append(" ");
                        strBuf.append(dayOfMonth);

                        tarih_ayarla.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int year = now.get(java.util.Calendar.YEAR);
                int month = now.get(java.util.Calendar.MONTH);
                int day = now.get(java.util.Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, android.R.style.Theme_Holo_Dialog, onDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
    }
}
