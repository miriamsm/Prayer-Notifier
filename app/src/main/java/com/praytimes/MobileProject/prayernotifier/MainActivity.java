package com.praytimes.MobileProject.prayernotifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.praytimes.MobileProject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    Button calculatePrayTimes;
    ArrayList<PrayerTime> list;
    RecyclerView times;
    RecyclerView.Adapter adapter;
    LocationManager locationManager;
    double latitude;
    double longitude;
    private AudioManager myAudioManager;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    GPSTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel(); // Create a notification channel
        gpsTracker = new GPSTracker(MainActivity.this);
        Button settings = findViewById(R.id.settingButton);
        Button qibla =findViewById(R.id.qibla);
        qibla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent q = new Intent(MainActivity.this, Kibla_Activity.class);
                startActivity(q);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 2);
            }
        });


        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }else{
            startLocationUpdates();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();


        refreshTimes();

    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshTimes();
    }

    void refreshTimes(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        GPSTracker gpsTracker = new GPSTracker(MainActivity.this);


        latitude=Double.parseDouble(prefs.getString(getString(R.string.latitude), "0.0"));
        longitude=Double.parseDouble(prefs.getString(getString(R.string.longitude), "0.0"));
        if (gpsTracker.canGetLocation()) {
            // Get current latitude and longitude
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }




        PrayTime prayers = new PrayTime();
        double timezone = prayers.getBaseTimeZone();

        String s1 = prefs.getString(getString(R.string.juristic), "");
        String s2 = prefs.getString(getString(R.string.calculation), "");
        String s3 = latitude+"";



        int RG1;
        int RG2;
        int RG4 = 0; // just intilize
        if (!(s1.trim().equals("") && s2.trim().equals(""))) {

            RG1 = Integer.parseInt(s1);
            RG2 = Integer.parseInt(s2);
            prayers.setCalcMethod(RG2);
            prayers.setAsrJuristic(RG1);
            prayers.setTimeFormat(RG4);

            prayers.setAdjustHighLats(0);
        } else {
            prayers.setTimeFormat(1);
            prayers.setCalcMethod(4);
            prayers.setAsrJuristic(0);
            prayers.setAdjustHighLats(0);
        }


        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);






            PrayerTime fajer = new PrayerTime();
        PrayerTime Sunrise = new PrayerTime();
        PrayerTime Duhur = new PrayerTime();
        PrayerTime asser = new PrayerTime();

        PrayerTime magrib = new PrayerTime();
        PrayerTime isha = new PrayerTime();

        ArrayList<String> prayerNames = prayers.getTimeNames();
        times = (RecyclerView) findViewById(R.id.timesView);
        list = new ArrayList<PrayerTime>();


        Date nowtime = new Date();
        SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH");
        String time = TIME_FORMAT.format(nowtime);

        int timeInt = Integer.parseInt(time);
        int next = 0;
        double nextFloat = 0;
        boolean isNext= true;


        for (int t = 0; t < prayerTimes.size(); t++) {
            switch (t){
            case 0: {
                if(RG4==0) {
                    fajer.setName(prayerNames.get(t));
                    fajer.setTime(prayerTimes.get(t));
                    String p = prayerTimes.get(t);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour && timeInt<20) {
                        fajer.setNext(false);
                    } else if (next < hour && isNext) {
                        fajer.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        fajer.setNext(false);
                    }
                }else if(RG4==3){
                    fajer.setName(prayerNames.get(t));
                    fajer.setTime(prayerTimes.get(t).substring(0, 5));
                    String p = prayerTimes.get(t).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour && timeInt<20) {
                        fajer.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        fajer.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        fajer.setNext(false);
                    }
                }else {
                    fajer.setName(prayerNames.get(t));
                    fajer.setTime(prayerTimes.get(t));
                }
                list.add(fajer);
                break;
            }
                case 1: {
                if(RG4==0) {
                    Sunrise.setName(prayerNames.get(t));
                    Sunrise.setTime(prayerTimes.get(t));
                    String p = prayerTimes.get(t);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        Sunrise.setNext(false);
                    } else if (next < hour && isNext) {
                        Sunrise.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        Sunrise.setNext(false);
                    }
                }else if(RG4==3){
                    Sunrise.setName(prayerNames.get(t));
                    Sunrise.setTime(prayerTimes.get(t).substring(0, 5));
                    String p = prayerTimes.get(t).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour) {
                        Sunrise.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        Sunrise.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        Sunrise.setNext(false);
                    }
                }else {
                    Sunrise.setName(prayerNames.get(t));
                    Sunrise.setTime(prayerTimes.get(t));
                }
                list.add(Sunrise);
            }break;
                case 2: {
                if(RG4==0) {
                    Duhur.setName(prayerNames.get(t));
                    Duhur.setTime(prayerTimes.get(t));
                    String p = prayerTimes.get(t);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        Duhur.setNext(false);
                    } else if (next < hour && isNext) {
                        Duhur.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        Duhur.setNext(false);
                    }
                }else if(RG4==3){
                    Duhur.setName(prayerNames.get(t));
                    Duhur.setTime(prayerTimes.get(t).substring(0, 5));
                    String p = prayerTimes.get(t).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour) {
                        Duhur.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        Duhur.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        Duhur.setNext(false);
                    }
                }else {
                    Duhur.setName(prayerNames.get(t));
                    Duhur.setTime(prayerTimes.get(t));
                }
                list.add(Duhur);
            }break;
                case 3: {
                if(RG4==0) {
                    asser.setName(prayerNames.get(t));
                    asser.setTime(prayerTimes.get(t));
                    String p = prayerTimes.get(t);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        asser.setNext(false);
                    } else if (next < hour && isNext) {
                        asser.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        asser.setNext(false);
                    }
                }else if(RG4==3){
                    asser.setName(prayerNames.get(t));
                    asser.setTime(prayerTimes.get(t).substring(0, 5));
                    String p = prayerTimes.get(t).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour) {
                        asser.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        asser.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        asser.setNext(false);
                    }
                }else {
                    asser.setName(prayerNames.get(t));
                    asser.setTime(prayerTimes.get(t));
                }
                list.add(asser);
            }
break;
                case 5: {
                if(RG4==0) {
                    magrib.setName(prayerNames.get(t));
                    magrib.setTime(prayerTimes.get(t));
                    String p = prayerTimes.get(t);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        magrib.setNext(false);
                    } else if (next < hour && isNext) {
                        magrib.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        magrib.setNext(false);
                    }
                }else if(RG4==3){
                    magrib.setName(prayerNames.get(t));
                    magrib.setTime(prayerTimes.get(t).substring(0, 5));
                    String p = prayerTimes.get(t).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    if (timeInt > hour) {
                        magrib.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        magrib.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        magrib.setNext(false);
                    }
                }else{
                    magrib.setName(prayerNames.get(t));
                    magrib.setTime(prayerTimes.get(t));
                }
                list.add(magrib);
            }break;
                case 6: {
                if(RG4==0) {
                    isha.setName(prayerNames.get(t));
                    isha.setTime(prayerTimes.get(t));
                    String p = prayerTimes.get(t);
                    String nextPrayer = p.substring(0, 2);
                    int hour = Integer.parseInt(nextPrayer);
                    if (timeInt > hour) {
                        isha.setNext(false);
                    } else if (next < hour && isNext) {
                        isha.setNext(true);
                        next = hour;
                        isNext = false;
                    } else {
                        isha.setNext(false);
                    }
                }else if(RG4==3){
                    isha.setName(prayerNames.get(t));
                    isha.setTime(prayerTimes.get(t).substring(0, 5));
                    String p = prayerTimes.get(t).substring(0, 5);
                    double hour = Double.parseDouble(p);
                    System.out.println(hour);
                    if (timeInt > hour) {
                        isha.setNext(false);
                    } else if (nextFloat < hour && isNext) {
                        isha.setNext(true);
                        nextFloat = hour;
                        isNext = false;
                    } else {
                        isha.setNext(false);
                    }
                }else{
                    isha.setName(prayerNames.get(t));
                    isha.setTime(prayerTimes.get(t));
                }
                list.add(isha);
            }}
        }
        times.setHasFixedSize(true);

        adapter = new ViewAdapter(MainActivity.this, list);

        times.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // get the current date (today) in yyyy/MM/dd format
        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd ");
        String date = DATE_FORMAT.format(today);

        /*
         * the user will get notified based on what prayer is upcoming next
         */
        for (int i = 0; i < list.size(); i++) {
            // Generate a pending intent to be used later
            Intent intent = new Intent(MainActivity.this, PrayTimeNotification.class);
            intent.putExtra("NotificationID", 1);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            String timeInHours="";
            if(RG4==0) {
                timeInHours = list.get(i).getTime().concat(":00");
            }else if(RG4==3){
                String c = list.get(i).getTime();
                String t = c.substring(0,c.indexOf("."));
                if(Integer.parseInt(t)<10){
                    String time1 = "0"+t +":"+c.substring(c.indexOf(".")+1,c.indexOf(".")+3)+":00";
                    timeInHours = time1;
                }else{
                    String time1 = t +":"+c.substring(c.indexOf(".")+1,c.indexOf(".")+3)+":00";
                    timeInHours = time1;
                }
            }
            //set the alarm as hh:mm:ss
            if(list.get(i).isNext()) {
                Toast.makeText(MainActivity.this, "alarm is set at " + timeInHours, Toast.LENGTH_SHORT).show();
                String myDate = date.concat(timeInHours);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date finalDate = null;
                try {
                    finalDate = sdf.parse(myDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(finalDate!=null){
                long timeInMillis = finalDate.getTime(); // get time in millisecond
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }}
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2 && resultCode == Activity.RESULT_OK)
        {
            refreshTimes();
        }
    }

    private void startLocationUpdates() {
        // Check if GPS location can be obtained
        if (gpsTracker.canGetLocation()) {
            Location location = gpsTracker.getLocation();
            if (location != null) {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                // Do something with the obtained latitude and longitude
                Toast.makeText(MainActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show settings alert dialog if GPS is not enabled
            gpsTracker.showSettingsAlert();
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PrayerTimeReminderChannel";
            String description = "Channel for upcoming prayer time";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANEL_1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Toast.makeText(this, "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}

