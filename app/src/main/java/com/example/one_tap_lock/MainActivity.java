package com.example.one_tap_lock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothClass;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static java.lang.Thread.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button hard_lock_button = (Button) findViewById(R.id.HARD_LOCK_BUTTON);
        hard_lock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DevicePolicyManager deviceManager = (DevicePolicyManager) getSystemService(Context. DEVICE_POLICY_SERVICE );
                ComponentName thisApp = new ComponentName(getApplicationContext(), MainActivity.class);
                if(deviceManager != null & deviceManager.isAdminActive(thisApp)) {
                    deviceManager.lockNow();
                    finishAffinity();
                    System.exit(0);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enable admin access for this app in settings", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(deviceManager.ACTION_ADD_DEVICE_ADMIN);
//                    intent.putExtra(deviceManager.EXTRA_DEVICE_ADMIN, thisApp);
//                    intent.putExtra(deviceManager.EXTRA_ADD_EXPLANATION , "Please enable the admin access" );
//                    startActivityForResult(intent, 1);
                }
            }
        });

        Button soft_lock_button = (Button) findViewById(R.id.SOFT_LOCK_BUTTON);
        soft_lock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean value = false;
                value = Settings.System.canWrite(getApplicationContext());
                if(value) {
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 500);
                    // wait for 15 seconds and reset timeout to 5 mins
                    synchronized (currentThread()) {
                        try {
                            currentThread().wait(15000);
                        } catch (Exception e) {
                            System.out.println("Exception caught: " + e);
                        }
                    }
                    // resetting timeout back to 5 mins
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 300000);
                    finishAffinity();
                    System.exit(0);
                }
                else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivity(intent);
                }
            }
        });
    }
}