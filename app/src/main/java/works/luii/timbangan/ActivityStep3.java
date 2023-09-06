package works.luii.timbangan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityStep3 extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("selection", Context.MODE_PRIVATE);
        //SharedPreferences.Editor Select = sharedPreferences.edit();
        int selection = sharedPreferences.getInt("LAST_BUTTON", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);
        Button nextButton = (Button) findViewById(R.id.next_pg3);
        nextButton.setOnClickListener(v -> {
            if (selection == 1){
                Intent intent = new Intent(v.getContext(), Read1.class);
                startActivity(intent);
            } else if (selection == 0){
                Intent intent = new Intent(v.getContext(), Scan1.class);
                startActivity(intent);
            }

        });
    }
}
