package works.luii.timbangan;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityStep2 extends AppCompatActivity {

    Button openSetting = (Button) findViewById(R.id.open_setting_pg2);
    Button nextButton = (Button) findViewById(R.id.next_pg1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        nextButton.setVisibility(View.GONE);

        openSetting.setOnClickListener(v -> {
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
        });

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ActivityStep3.class);
            startActivity(intent);
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            nextButton.setVisibility(View.VISIBLE);

        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            nextButton.setVisibility(View.GONE);
        }

    }
}
