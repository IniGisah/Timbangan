package works.luii.timbangan;

import static android.content.res.Configuration.HARDKEYBOARDHIDDEN_NO;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityStep2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        Button openSetting = (Button) findViewById(R.id.open_setting_pg2);
        Button nextButton = (Button) findViewById(R.id.next_pg2);
        nextButton.setVisibility(View.GONE);
        // Init
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(getResources().getConfiguration().hardKeyboardHidden == HARDKEYBOARDHIDDEN_NO) {
                    nextButton.setVisibility(View.VISIBLE);
                    handler.removeCallbacksAndMessages(null);
                }
                //Toast.makeText(ActivityStep2.this, "handler running...", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(runnable, 1000);

        openSetting.setOnClickListener(v -> {
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
        });

        if(getResources().getConfiguration().hardKeyboardHidden == HARDKEYBOARDHIDDEN_NO) nextButton.setVisibility(View.VISIBLE);

        nextButton.setOnClickListener(v -> {
            handler.removeCallbacks(runnable);
            Intent intent = new Intent(v.getContext(), ActivityStep3.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Button nextButton = (Button) findViewById(R.id.next_pg2);
        if(getResources().getConfiguration().hardKeyboardHidden == HARDKEYBOARDHIDDEN_NO) nextButton.setVisibility(View.VISIBLE);
    }
}
