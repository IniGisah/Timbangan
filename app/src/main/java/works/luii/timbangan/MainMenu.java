package works.luii.timbangan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.savedstate.SavedStateRegistryOwner;

public class MainMenu extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        //SharedPreferences.Editor Select = getSharedPreferences("Selection", Context.MODE_PRIVATE).edit();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        Button inputButton = findViewById(R.id.inputdata_button);
        Button readButton = findViewById(R.id.readdata_button);

        inputButton.setOnClickListener(v -> {
            saveLastButtonPressed(0);
            Intent intent = new Intent(v.getContext(), ActivityStep1.class);
            startActivity(intent);
        });

        readButton.setOnClickListener(v -> {
            saveLastButtonPressed(1);
            Intent intent = new Intent(v.getContext(), ActivityStep2.class);
            startActivity(intent);
        });
    }

    void saveLastButtonPressed(int buttonNumber) {
        SharedPreferences sharedPref = getSharedPreferences("selection", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("LAST_BUTTON", buttonNumber);
        editor.apply();
    }
}
