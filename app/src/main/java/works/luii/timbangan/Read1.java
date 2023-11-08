package works.luii.timbangan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Read1 extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_1);

        EditText editText = (EditText) findViewById(R.id.idinput);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        
        editText.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
                SharedPreferences.Editor IdKambing = sharedPreferences.edit();
                IdKambing.putString("id", editText.getText().toString());
                IdKambing.commit();

                Intent intent = new Intent(v.getContext(), ReadResult.class);
                startActivity(intent);

                Toast.makeText(this, "Id Kambing : " + editText.getText(), Toast.LENGTH_SHORT).show();
                editText.getText().clear();
                return true;
            }
            return false;
        });

    }

}
