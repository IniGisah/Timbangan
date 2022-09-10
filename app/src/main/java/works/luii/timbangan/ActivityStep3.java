package works.luii.timbangan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityStep3 extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);

        Button nextButton = (Button) findViewById(R.id.next_pg3);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Scan1.class);
            startActivity(intent);
        });
    }
}
