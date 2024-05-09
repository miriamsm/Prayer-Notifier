package com.praytimes.MobileProject.prayernotifier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.praytimes.MobileProject.R;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class SettingsFragment extends PreferenceFragment{

    // list prefs
    public static final String PREF_JURISTIC = "juristic";
    public static final String PREF_CALC = "calculation";
    public static final String PREF_LATITUDE = "latitude";
    public static final String PREF_TIME = "time";
    public static final String PREF_SILENT = "silent";
    private static final int CALLBACK_CODE = 0;
    private Preference locationPreference;
    private GPSTracker gpsTracker;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private ProgressDialog progressDialog;

    private int RG1 = 0;
    private int RG2 = 4;
    private int RG3 = 0;
    private int RG4 = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        locationPreference = findPreference("location_pref");
        // Listener for location preference
        locationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                updateLocation();
                return true;
            }
        });

        // Listener for preference changes
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // Handle preference changes
                if (key.equals(PREF_JURISTIC)) {
                    ListPreference juristicPref = (ListPreference) findPreference(key);
                    RG1 = Integer.valueOf((juristicPref.getValue()));
                    Toast.makeText(getActivity(), "Juristic method updated", Toast.LENGTH_SHORT).show();
                }
                if (key.equals(PREF_CALC)) {
                    ListPreference PREF_CALCPref = (ListPreference) findPreference(key);
                    RG1 = Integer.valueOf((PREF_CALCPref.getValue()));
                    Toast.makeText(getActivity(), "calculation method updated", Toast.LENGTH_SHORT).show();
                }
                if (key.equals(PREF_TIME)) {
                    ListPreference PREF_TIMEPref = (ListPreference) findPreference(key);

                    RG1 = Integer.valueOf((PREF_TIMEPref.getValue()));
                    Toast.makeText(getActivity(), "time Format method updated", Toast.LENGTH_SHORT).show();
                }


                // Handle other preference changes similarly
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALLBACK_CODE) {
            Toast.makeText(getActivity(), "Access granted!", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update location
    private void updateLocation() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(true);
        progressDialog.show();


        this.progressDialog.show();

        gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            // Get current latitude and longitude
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            // Update the preference with the new location
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putString("latitude", String.valueOf(latitude));
            editor.putString("longitude", String.valueOf(longitude));
            editor.apply();

            // Trigger prayer times update
//            ((MainActivity) getActivity()).refreshTimes();

            progressDialog.setMessage("Location updated...");


            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            },2000);
            Toast.makeText(getActivity(), "Location updated", Toast.LENGTH_SHORT).show();
        } else {
            this.progressDialog.dismiss();
            // Show settings alert dialog if GPS is not enabled
            gpsTracker.showSettingsAlert();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void silencePhone(long timeInMillis) {
        // Your existing code
    }

    public int getRG1() {
        return RG1;
    }

    public int getRG2() {
        return RG2;
    }

    public int getRG3() {
        return RG3;
    }

    public int getRG4() {
        return RG4;
    }
}
