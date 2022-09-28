package works.luii.timbangan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Upload extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        TextView idkambing = (TextView) findViewById(R.id.textview_data_id);
        TextView beratkambing = (TextView) findViewById(R.id.textview_data_berat);

        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        String iddata = sharedPreferences.getString("id", "");
        Float beratdata = sharedPreferences.getFloat("berat", 0);

        idkambing.setText("ID : " + iddata);
        beratkambing.setText("Berat : " + beratdata.toString() + " kg");

        
    }
}
