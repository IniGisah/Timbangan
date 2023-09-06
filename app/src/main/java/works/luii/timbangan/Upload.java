package works.luii.timbangan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class Upload extends AppCompatActivity {
    JSONObject jsonObject = new JSONObject();
    //works.luii.timbangan.MyBluetoothPrintersConnections printerblutut;

    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        String iddata = sharedPreferences.getString("id", "");
        Float berat = sharedPreferences.getFloat("berat", 0);
        //int iddataint = Integer.parseInt(iddata);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        TextView idkambing = (TextView) findViewById(R.id.textview_data_id);
        TextView beratkambing = (TextView) findViewById(R.id.textview_data_berat);

        Float beratdata = berat / 100; 

        idkambing.setText("ID : " + iddata);
        beratkambing.setText("Berat : " + beratdata.toString() + " kg");

        try {
            jsonObject.put("animal_name", "kambing");
            jsonObject.put("id", iddata);
            jsonObject.put("animal_species", "kambing");
            jsonObject.put("weight", beratdata);
            String jsonString = jsonObject.toString();
            new PostData().execute(jsonString);
            /*
            EscPosPrinter printer = new EscPosPrinter(Objects.requireNonNull(printerblutut.getList())[0], 203, 48f, 32);
            printer.printFormattedText(
                    "[C]<u><font size='big'>Laporan Timbangan</font></u>\n" +
                    "[L]\n" +
                    "[C]================================\n" +
                    "[L]\n" +
                    "[L]<b>ID Kambing: </b>[R]"+ iddata + "\n" +
                    "[L]<b>Berat Kambing: </b>[R]"+ beratdata.toString() + "\n" +
                    "[C]================================\n"
                    );

             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // on below line creating a class to post the data.
    class PostData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            ProgressBar loadingPB = findViewById(R.id.idLoadingPB);
            TextView textresult = (TextView) findViewById(R.id.result_text);
            Button buttonretry = findViewById(R.id.retry);
            Button homebutton = findViewById(R.id.home);
            Button newscanbutton = findViewById(R.id.newscan);
            HttpURLConnection client = null;
            try {
                runOnUiThread(() -> {
                    loadingPB.setVisibility(View.VISIBLE);
                    buttonretry.setVisibility(View.GONE);
                });

                // on below line creating a url to post the data.
                URL url = new URL("http://priv.luii.my.id:3000/db");

                // on below line opening the connection.
                client = (HttpURLConnection) url.openConnection();

                // on below line setting method as post.
                client.setRequestMethod("POST");

                // on below line setting content type and accept type.
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestProperty("Accept", "application/json");
                client.setRequestProperty("authorization", "password akses database");

                // on below line setting client.
                client.setDoOutput(true);

                // on below line we are creating an output stream and posting the data.
                try (OutputStream os = client.getOutputStream()) {
                    byte[] input = strings[0].getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // on below line creating and initializing buffer reader.
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), "utf-8"))) {

                    // on below line creating a string builder.
                    StringBuilder response = new StringBuilder();

                    // on below line creating a variable for response line.
                    String responseLine = null;

                    // on below line writing the response
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // on below line displaying a toast message.
                    String finalResponseLine = responseLine;
                    runOnUiThread(() -> {
                        loadingPB.setVisibility(View.GONE);
                        //textresult.setText("Data telah sukses diupload!\nRespons : " + finalResponseLine);
                        //Toast.makeText(Upload.this, "Data has been posted to the API." + response, Toast.LENGTH_SHORT).show();
                        homebutton.setVisibility(View.VISIBLE);
                        newscanbutton.setVisibility(View.VISIBLE);
                        loadingPB.setVisibility(View.GONE);
                    });


                    homebutton.setOnClickListener(v -> {
                        Intent intent = new Intent(Upload.this, MainMenu.class);
                        startActivity(intent);
                    });

                    newscanbutton.setOnClickListener(v -> {
                        Intent intent = new Intent(Upload.this, Scan1.class);
                        startActivity(intent);
                    });
                    /*
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(Upload.this, Scan1.class);
                            startActivity(intent);
                        }
                    }, 5000);   //5 seconds

                     */
                }

            } catch (Exception e) {
                // on below line handling the exception.
                runOnUiThread(() -> {
                    textresult.setText("Data gagal di upload. \nMohon tekan tombol dibawah untuk mengulangi proses upload.");
                    buttonretry.setVisibility(View.VISIBLE);
                });
                buttonretry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //jsonObject.put("animal_name", "kambing");
                            //jsonObject.put("id", iddataint);
                            //jsonObject.put("animal_species", "kambing");
                            //jsonObject.put("weight", beratdata);
                            String jsonString = jsonObject.toString();
                            new PostData().execute(jsonString);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                });
                e.printStackTrace();
                //Toast.makeText(Upload.this, "Fail to post the data : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                if (client != null) {
                    client.disconnect();
                }
            }
            return null;
        }
    }
}

