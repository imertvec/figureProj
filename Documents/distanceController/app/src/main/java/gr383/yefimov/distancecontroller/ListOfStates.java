package gr383.yefimov.distancecontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class ListOfStates extends AppCompatActivity {
    ListView listView;
    String selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_states);

        //objects for working with database
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //init adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //get data from database -> array -> adapter
        Cursor cursor = db.rawQuery("SELECT state FROM States", null);
        String[] states = new String[cursor.getCount()];

        if(cursor.moveToFirst()){
            for(int j = 0; j < cursor.getCount(); j++){
                states[j] = cursor.getString(cursor.getColumnIndex("state"));
                cursor.moveToNext();
            }
        }

        for(String x: states){
            adapter.add(x);
        }

        //init listView
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = states[position];
            }
        });
        listView.setAdapter(adapter);
    }

    public void returnBack(View v){
        Intent intent = new Intent();
        intent.putExtra("state", selectedItem);
        setResult(RESULT_OK, intent);
        Log.e("secondAct", selectedItem);
        finish();
    }
}