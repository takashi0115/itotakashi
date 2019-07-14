package com.example.accelerometertest;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import java.util.List;

import static java.nio.file.Files.size;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private TextView textView, textInfo, textaction;
    float sensorX, sensorY, sensorZ;

    double compACC;  //合成加速度
    float Atotal;   //合成加速度の合計
    float Aave;     //合成加速度の平均
    float Adist;    //合成加速度の分散

    int i;
    int flag;

    int cnt = 0;
    int cursor = 0;
    int DIST_LIMIT = 50;
    Double distribute[] = new Double[DIST_LIMIT];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        textInfo = findViewById(R.id.text_info);

        // Get an instance of the TextView
        textView = findViewById(R.id.text_view);

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Listenerの登録
        Sensor accel = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
    }

    // 解除するコードも入れる!
    @Override
    protected void onPause() {
        super.onPause();
        // Listenerを解除
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // float sensorX, sensorY, sensorZ;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorX = event.values[0];
            sensorY = event.values[1];
            sensorZ = event.values[2];

            compACC = Math.sqrt(sensorX * sensorX + sensorY * sensorY + sensorZ * sensorZ); //合成加速度の計算

            String strTmp = "加速度センサー\n"
                    + " X: " + sensorX + "\n"
                    + " Y: " + sensorY + "\n"
                    + " Z: " + sensorZ + "\n"
                    + "\n"
                    + "行っている行動:";
            textView.setText(strTmp);

            //showInfo(event);
        }
    }
    
    public void run() {
        if (cnt < DIST_LIMIT) { //50個まで配列
            distribute[cnt] = compACC;
        } else {
            if (cursor < DIST_LIMIT) {
                distribute[cursor] = compACC;
                cursor++;
            } else {
                cursor = 0;
            }
        }
        if(size(distribute) < DIST_LIMIT) {
            act_recognition();
            cnt++;
        } else {
            Atotal = 0;
            Adist = 0;
            for (i = 0; i < DIST_LIMIT; i++) {
                Atotal += distribute[i];
            }

            Aave = Atotal / DIST_LIMIT;

            for (i = 0; i < DIST_LIMIT; i++) {
                Adist += (distribute[i] - Aave) * (distribute[i] - Aave);
            }

            Adist = Adist / DIST_LIMIT;

            act_recognition();
            cnt++;
        }
    }

    public int size(Double[] array){
        int count = 0;
        for(Double prop : array){
            if(prop != null){
                count++;
            }
        }
        return count;
    }

    public void act_recognition(){
        //行動判別
        if(0.6 < Adist && Adist < 14){
            flag = 1;
        }else if(14 < Adist && Adist < 25){
            flag = 2;
        }else if(25 < Adist){
            flag = 3;
        }else {
            flag = 0;
        }

        if(flag == 1){
            String strTmp = "walk\n";
            textView.setText(strTmp);
        }else if(flag == 2){
            String strTmp = "stairs down\n";
            textView.setText(strTmp);
        }else if(flag == 3){
            String strTmp = "run\n";
            textView.setText(strTmp);
        }else{
            String strTmp = "stand\n";
            textView.setText(strTmp);
        }
    }





    // （お好みで）加速度センサーの各種情報を表示
    /*private void showInfo(SensorEvent event){
        // センサー名
        StringBuffer info = new StringBuffer("Name: ");
        info.append(event.sensor.getName());
        info.append("\n");

        // ベンダー名
        info.append("Vendor: ");
        info.append(event.sensor.getVendor());
        info.append("\n");

        // 型番
        info.append("Type: ");
        info.append(event.sensor.getType());
        info.append("\n");

        // 最小遅れ
        int data = event.sensor.getMinDelay();
        info.append("Mindelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // 最大遅れ
        //data = event.sensor.getMaxDelay();
        info.append("Maxdelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // レポートモード
       // data = event.sensor.getReportingMode();
        String stinfo = "unknown";
        if(data == 0){
            stinfo = "REPORTING_MODE_CONTINUOUS";
        }else if(data == 1){
            stinfo = "REPORTING_MODE_ON_CHANGE";
        }else if(data == 2){
            stinfo = "REPORTING_MODE_ONE_SHOT";
        }
        info.append("ReportingMode: ");
        info.append(stinfo);
        info.append("\n");

        // 最大レンジ
        info.append("MaxRange: ");
        float fData = event.sensor.getMaximumRange();
        info.append(String.valueOf(fData));
        info.append("\n");

        // 分解能
        info.append("Resolution: ");
        fData = event.sensor.getResolution();
        info.append(String.valueOf(fData));
        info.append(" m/s^2\n");

        // 消費電流
        info.append("Power: ");
        fData = event.sensor.getPower();
        info.append(String.valueOf(fData));
        info.append(" mA\n");

        textInfo.setText(info);
    }*/

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
