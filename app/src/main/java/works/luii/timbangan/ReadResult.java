package works.luii.timbangan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ReadResult extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        String iddata = sharedPreferences.getString("id", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_result);

        TextView idkambing = findViewById(R.id.textview_dataid_read);
        //TextView beratkambing = (TextView) findViewById(R.id.textview_data_berat);

        Button homebutton = findViewById(R.id.home_read);
        Button newreadbutton = findViewById(R.id.newread);

        new GetData().execute(iddata);

        idkambing.setText("ID : " + iddata);

        homebutton.setOnClickListener(v -> {
            Intent intent = new Intent(ReadResult.this, MainMenu.class);
            startActivity(intent);
        });

        newreadbutton.setOnClickListener(v -> {
            Intent intent = new Intent(ReadResult.this, Scan1.class);
            startActivity(intent);
        });
    }

    class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... iddata) {
            SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
            String iddata2 = sharedPreferences.getString("id", "");
            ProgressBar loadingPB = findViewById(R.id.idLoadingPBRes);
            TextView textresult = findViewById(R.id.result_text_read);
            TextView weightbefore = findViewById(R.id.textview_data_beratbefore);
            TextView weightafter = findViewById(R.id.textview_data_beratafter);
            TextView difftext = findViewById(R.id.textview_data_selisih);
            HttpURLConnection urlConnection = null;
            loadingPB.setVisibility(View.VISIBLE);

            try {
                String urlstring = "http://priv.luii.my.id:3000/db/" + iddata2;
                URL url = new URL(urlstring);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("authorization", "password akses database");

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                // Convert request content to string
                InputStream is = urlConnection.getInputStream();
                String content = convertInputStream(is, "UTF-8");
                is.close();

                Log.v("Url request", urlstring);
                Log.v("Data get res:", content);

                JSONObject obj = new JSONObject(content);
                JSONArray dataArray = obj.getJSONArray("data");
                JSONObject data = dataArray.getJSONObject(0);
                String beratbefore = data.getString("weight_before");
                String beratafter = data.getString("weight_after");
                Float selisih = Float.parseFloat(beratbefore) - Float.parseFloat(beratafter);

                runOnUiThread(() -> {
                    loadingPB.setVisibility(View.GONE);
                    //textresult.setText("Data telah sukses dibaca!\nRespons : " + content);
                    weightbefore.setText("Berat Awal : " + beratbefore + " kg");
                    weightafter.setText("Berat Sesudah : " + beratafter+ " kg");
                    difftext.setText("Selisih : " + selisih + " kg");
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
        private String convertInputStream(InputStream is, String encoding) {
            Scanner scanner = new Scanner(is, encoding).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
