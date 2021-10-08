package gr383.yefimov.distancecontroller;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private DatagramPacket dmPacket;
    private DatagramSocket dmSocket;
    private InetAddress ipAddress;
    private int width = 4;
    private int height = 4;
    private int port = 2000;
    private String host = "node00.ddns.net";
    private int bias = 0;
    private SeekBar[] seekBars = new SeekBar[3];
    private Button[][] btn = new Button[width][height];
    private int[][] colorMatrix = new int[width][height];
    private Button[] menuButtons = new Button[6];
    private LinearLayout[] linear = new LinearLayout[7];
    private LinearLayout mainLinear;
    private Boolean confirmChange = false;
    private int[] rgb = new int[]{ 0, 0, 0};
    private int[][] matrix = {
        {3, 4, 11, 12},
        {2, 5, 10, 13},
        {1, 6, 9, 14},
        {0, 7, 8, 15}
    };
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLinear = findViewById(R.id.main);
        try {
            dmSocket = new DatagramSocket(null);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        db = new DBHelper(this);
        createGridOfButtons();
        setSettingOfMenuButtons(menuButtons);
    }

    private void createGridOfButtons(){
        //Making grid of buttons 4x4
        for (int j = 0; j < width; j++) {
            linear[j] = new LinearLayout(getApplicationContext());
            linear[j].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linear[j].setOrientation(LinearLayout.HORIZONTAL);
            mainLinear.addView(linear[j]);

            for (int i = 0; i < height; i++) {
                btn[j][i] = new Button(this);
                btn[j][i].setId(matrix[j][i]);
                btn[j][i].getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
                btn[j][i].setTag(Color.BLACK);
                btn[j][i].setLayoutParams(new LinearLayout.LayoutParams(150, 150));
                btn[j][i].setOnClickListener(v -> {
                    Button button = (Button)v;
                    ColorDrawable color = new ColorDrawable();
                    color.setColor(Color.argb(255, rgb[0], rgb[1], rgb[2]));
                    if(confirmChange){
                        button.getBackground().setColorFilter(color.getColor(), PorterDuff.Mode.MULTIPLY);
                        button.setTag(color.getColor());
                    }
                });
                linear[j].addView(btn[j][i]);
            }
        }

        //Initialization of menuButtons
        for(int j = 4; j < linear.length; j++){
            linear[j] = new LinearLayout(getApplicationContext());
            linear[j].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linear[j].setOrientation(LinearLayout.HORIZONTAL);
            linear[j].setGravity(Gravity.CENTER_HORIZONTAL);

            for(int i = 0 ; i < 2; i++){
                menuButtons[i + bias] = new Button(getApplicationContext());
                menuButtons[i + bias].setText(String.valueOf(i + bias));
                menuButtons[i + bias].setLayoutParams(new LinearLayout.LayoutParams(305, 100));
                linear[j].addView(menuButtons[i + bias]);
            }

            bias += 2;
            mainLinear.addView(linear[j]);
        }
    }

    private void sendPacket(int tag, ColorDrawable color){
        byte[] buf = new byte[5];
        byte[] confirm = new byte[1];
        buf[0] = 2;
        buf[1] = (byte)tag;
        buf[2] = (byte)Color.red(color.getColor());
        buf[3] = (byte)Color.green(color.getColor());
        buf[4] = (byte)Color.blue(color.getColor());
        try{
            ipAddress = InetAddress.getByName(host);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            for(int i = 0; i < 5; i++){
                dmPacket = new DatagramPacket(buf, buf.length, ipAddress, port);
                dmSocket.send(dmPacket);

                dmPacket = new DatagramPacket(confirm, confirm.length, ipAddress, port);
                dmSocket.send(dmPacket);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSeekBars(LinearLayout linearLayout, Button btn){
        //Making seekBars color
        for(int i = 0; i < 3; i++){
            seekBars[i] = new SeekBar(getApplicationContext());
            seekBars[i].setTag(i);
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                    //change color of Button Changes
                    int color = Color.argb(255, rgb[0], rgb[1], rgb[2]);
                    btn.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    if((int)seekBar.getTag() == 0) {
                        rgb[0] = seekBar.getProgress();
                    }
                    else if((int)seekBar.getTag() == 1){
                        rgb[1] = seekBar.getProgress();
                    }
                    else if((int)seekBar.getTag() == 2){
                        rgb[2] = seekBar.getProgress();
                    }

                    confirmChange = false;
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

            seekBars[i].setMax(255);
            linearLayout.addView(seekBars[i]);
        };
        seekBars[0].getThumb().setColorFilter(Color.argb(255, 255, 0, 0), PorterDuff.Mode.SRC_ATOP);
        seekBars[1].getThumb().setColorFilter(Color.argb(255, 0, 255, 0), PorterDuff.Mode.SRC_ATOP);
        seekBars[2].getThumb().setColorFilter(Color.argb(255, 0,0,  255), PorterDuff.Mode.SRC_ATOP);
        seekBars[0].getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        seekBars[1].getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        seekBars[2].getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
    }

    private void setSettingOfMenuButtons(Button[] buttons){
        buttons[0].setText("Set params");
        buttons[0].setOnClickListener(v -> {
            callParamsDialog();
        });

        buttons[1].setText("Change color");
        buttons[1].setOnClickListener(v -> {
            callColorDialog();
        });

        buttons[2].setText("Translate");
        buttons[2].setOnClickListener(v -> {
            Thread thread = new Thread(new netConnection());
            thread.start();
        });

        buttons[3].setText("<<Rotate<<");
        buttons[3].setOnClickListener(v -> {
            rotateMatrix();
        });
        buttons[4].setText("Save state");
        buttons[4].setOnClickListener(v -> {
            //add state to database
            SQLiteDatabase database = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            int k = 0;
            String[] states = new String[16];
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    states[k] = String.valueOf(btn[i][j].getTag());
                    k++;
                }
            }

            Log.e("msg", Arrays.toString(states));
            contentValues.put("state", Arrays.toString(states));
            database.insert("States", null, contentValues);
        });
        buttons[5].setText("Get state");
        buttons[5].setOnClickListener(v -> {
            //get state from database
            Intent intent = new Intent(this, ListOfStates.class);
            startActivityForResult(intent, 1);
        });
    }

    private void callColorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        Button changeColor = new Button(getApplication().getBaseContext());

        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        changeColor.setText("Set");
        changeColor.setLayoutParams(new LinearLayout.LayoutParams(305, 100));
        changeColor.getBackground().setColorFilter(Color.argb(255, rgb[0], rgb[1], rgb[2]), PorterDuff.Mode.MULTIPLY);
        changeColor.setOnClickListener(v1 -> {
            confirmChange = true;
        });

        setSeekBars(layout, changeColor);
        layout.addView(changeColor);
        builder.setView(layout);
        builder.show();
    }

    private void callParamsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Button setParams = new Button(getApplication().getBaseContext());
        LinearLayout layout = new LinearLayout(this);
        EditText[] fields = new EditText[2];
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        //settings for EditTexts
        for(int i = 0; i < fields.length; i++){
            fields[i] = new EditText(this);
            fields[i].setGravity(Gravity.CENTER);
            fields[i].setLayoutParams(new LinearLayout.LayoutParams(500, 70));
            layout.addView(fields[i]);
        }

        fields[0].setHint("ip");
        fields[1].setHint("port");
        fields[0].setPadding(20, 0, 20, 0);
        fields[1].setPadding(20, 0, 20, 0);
        fields[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(fields[0].getText().length() != 0){
                    try{
                        host = String.valueOf(fields[0].getText());
                    }
                    catch (Exception ignored){ }
                }
            }
        });

        fields[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(fields[0].getText().length() != 0) {
                    try{
                        port = Integer.parseInt(String.valueOf(fields[1].getText()));
                    }
                    catch (Exception ignored){ }
                }

            }
        });
        //settings for set-button
        setParams.setText("Set");
        setParams.setLayoutParams(new LinearLayout.LayoutParams(305, 100));
        setParams.setOnClickListener(v1 -> {
            try{
                Log.e(host, String.valueOf(port));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });

        layout.addView(setParams);
        builder.setView(layout);
        builder.show();
    }

    private class netConnection implements Runnable{
        public void run(){
            ColorDrawable colorDrawable = new ColorDrawable();
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    if(btn[i][j].getTag() != null){
                        int color = (int) btn[i][j].getTag();
                        colorDrawable.setColor(color);
                        sendPacket(btn[i][j].getId(), colorDrawable);
                    }
                }
            }
        }
    }

    private void rotateMatrix(){
        //get current button's color matrix
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                colorMatrix[i][j] = (int)btn[i][j].getTag();
            }
        }

        //rotate matrix
        for (int i = 0; i < width / 2; i++) {
            for (int j = i; j < width - i - 1; j++) {
                int tempMatrix = matrix[i][j];
                matrix[i][j] = matrix[width - 1 - j][i];
                matrix[width - 1 - j][i] = matrix[width - 1 - i][width - 1 - j];
                matrix[width - 1 - i][width - 1 - j] = matrix[j][width - 1 - i];
                matrix[j][width - 1 - i] = tempMatrix;

                int tempColor = colorMatrix[i][j];
                colorMatrix[i][j] = colorMatrix[width - 1 - j][i];
                colorMatrix[width - 1 - j][i] = colorMatrix[width - 1 - i][width - 1 - j];
                colorMatrix[width - 1 - i][width - 1 - j] = colorMatrix[j][width - 1 - i];
                colorMatrix[j][width - 1 - i] = tempColor;
            }
        }

        //reindex buttons
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                btn[i][j].setTag(colorMatrix[i][j]);
                if(btn[i][j].getTag() != null){
                    btn[i][j].getBackground().setColorFilter(colorMatrix[i][j], PorterDuff.Mode.MULTIPLY);
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            String state = data.getStringExtra("state");
            int k = 0;
            Log.e("msg", String.valueOf(state));
            String[] trueStates = (String[]) Arrays.asList(state.substring(1, state.length() - 1).split(", ")).toArray();
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    btn[i][j].getBackground().setColorFilter(Integer.parseInt(trueStates[k]), PorterDuff.Mode.MULTIPLY);
                    btn[i][j].setTag(Integer.parseInt(trueStates[k]));
                    k++;
                }
            }
        }
        else
            Log.e("msg", "data is null");
    }
}