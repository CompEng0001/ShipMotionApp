package com.example.cargoshipapp;

import static java.lang.Float.valueOf;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {
    private static final float VALUE_DRIFT = 0.05f;
    float azimuth;
    int counter = 0;
    long currentTime;

    /* renamed from: id */
    int f31id;
    float[] mAccelerometerData = new float[3];
    private LineChart mChart;
    float[] mMagnetometerData = new float[3];
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;
    private SensorManager mSensorManager;
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;
    float[] orientationValues = new float[3];
    float pitch;
    /* access modifiers changed from: private */
    public boolean plotData = true;
    /* access modifiers changed from: private */
    public boolean pressed = false;
    private boolean recordData = false;
    Button resumePause;
    float roll;
    TextView sampleTimeView;
    Button setNewSampleTime;
    StandardDeviation standardDeviation = new StandardDeviation();
    TextView textView;
    private Thread thread;
    TextView timeView;
    double totalTime = Utils.DOUBLE_EPSILON;
    int userDefinedSampleTime = 200;

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
        this.resumePause = (Button) findViewById(R.id.resumepausebtn);
        this.setNewSampleTime = (Button) findViewById(R.id.SampleTimeBtn);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }
        this.mTextSensorAzimuth = (TextView) findViewById(R.id.azimuthTV);
        this.mTextSensorPitch = (TextView) findViewById(R.id.pitchTV);
        this.mTextSensorRoll = (TextView) findViewById(R.id.rollTV);
        this.textView = (TextView) findViewById(R.id.CurrentFileTV);
        this.timeView = (TextView) findViewById(R.id.TimeTV);
        this.sampleTimeView = (TextView) findViewById(R.id.SampleTimeET);
        this.mChart = (LineChart) findViewById(R.id.chart);
        TextView textView2 = this.textView;
        textView2.setText("Current: Test_" + this.counter + ".csv");
        this.sampleTimeView.setText(String.valueOf(this.userDefinedSampleTime));
        this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        try {
            this.mSensorAccelerometer = this.mSensorManager.getDefaultSensor(1);
        } catch (Exception unused) {
            Toast.makeText(this, "Accelerometer not available on this device", Toast.LENGTH_LONG);
            finish();
        }
        try {
            this.mSensorMagnetometer = this.mSensorManager.getDefaultSensor(2);
        } catch (Exception unused2) {
            Toast.makeText(this, "Magnetometer is not available on this device", Toast.LENGTH_LONG);
            finish();
        }
        chartSetup();
        feedMultiple();
        this.resumePause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!MainActivity.this.pressed) {
                    MainActivity.this.onPause();
                    boolean unused = MainActivity.this.pressed = true;
                    MainActivity.this.resumePause.setText("Resume");
                    return;
                }
                MainActivity.this.onResume();
                MainActivity.this.registerSensors();
                MainActivity.this.counter++;
                MainActivity.this.textView.setText(" Current: Test_" + MainActivity.this.counter + ".csv");
                MainActivity.this.totalTime = Utils.DOUBLE_EPSILON;
                boolean unused2 = MainActivity.this.pressed = false;
                MainActivity.this.resumePause.setText("Pause");
            }
        });
        this.setNewSampleTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.userDefinedSampleTime = Integer.parseInt(MainActivity.this.sampleTimeView.getText().toString());
            }
        });
    }

    /* access modifiers changed from: protected */
    public void registerSensors() {
        if (this.mSensorAccelerometer != null) {
            this.mSensorManager.registerListener(this, this.mSensorAccelerometer, 500000);
        }
        if (this.mSensorMagnetometer != null) {
            this.mSensorManager.registerListener(this, this.mSensorMagnetometer, 500000);
        }
        this.currentTime = SystemClock.elapsedRealtime();
    }

    /* access modifiers changed from: protected */
    public void unRegisterSensors() {
        this.mSensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        switch (sensorEvent.sensor.getType()) {
            case 1:
                this.mAccelerometerData = (float[]) sensorEvent.values.clone();
                if (this.plotData) {
                    this.plotData = false;
                }
                this.recordData = true;
                break;
            case 2:
                this.mMagnetometerData = (float[]) sensorEvent.values.clone();
                this.recordData = false;
                break;
            default:
                return;
        }
        if (this.recordData) {
            float[] fArr = new float[9];
            if (SensorManager.getRotationMatrix(fArr, (float[]) null, this.mAccelerometerData, this.mMagnetometerData)) {
                SensorManager.getOrientation(fArr, this.orientationValues);
                if (elapsedRealtime - this.currentTime > ((long) this.userDefinedSampleTime)) {
                    SensorValues();
                    this.totalTime += (double) (((float) this.userDefinedSampleTime) / 1000.0f);
                    this.currentTime = SystemClock.elapsedRealtime();
                }
            }
        }
    }

    public void SensorValues() {
        this.azimuth = this.orientationValues[0] * 57.295776f;
        this.pitch = this.orientationValues[1] * 57.295776f;
        this.roll = this.orientationValues[2] * 57.295776f;
        addEntry(this.roll);
        this.standardDeviation.AccelData((double) this.pitch, (double) this.roll);
        this.timeView.setText(String.valueOf(String.format("%.4f", new Object[]{Double.valueOf(this.totalTime)})));
        logData(String.valueOf(this.roll) + "," + String.valueOf(this.pitch) + "," + String.valueOf(this.totalTime) + "\n");
        this.mTextSensorAzimuth.setText(String.format("%.4f", new Object[]{valueOf(this.azimuth)}));
        this.mTextSensorPitch.setText(String.format("%.4f", new Object[]{valueOf(this.pitch)}));
        this.mTextSensorRoll.setText(String.format("%.4f", new Object[]{valueOf(this.roll)}));
    }

    public void logData(String str) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
    File file = new File(externalStorageDirectory.getAbsolutePath() + "/Sensor_Data");
        if (!file.exists()) {
        file.mkdir();
    }
        try {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(file, "Test_" + String.valueOf(this.counter) + ".csv"), true);
        fileOutputStream.write(str.getBytes());
        fileOutputStream.close();
    } catch (IOException e) {
        //Toast.makeText(this, " " + String.valueOf(e), 0).show();
    }
}

    private void addEntry(float f) {
        LineData lineData = (LineData) this.mChart.getData();
        if (lineData != null) {
            ILineDataSet iLineDataSet = (ILineDataSet) lineData.getDataSetByIndex(0);
            if (iLineDataSet == null) {
                iLineDataSet = createSet();
                lineData.addDataSet(iLineDataSet);
            }
            lineData.addEntry(new Entry((float) iLineDataSet.getEntryCount(), f), 0);
            lineData.notifyDataChanged();
            this.mChart.notifyDataSetChanged();
            this.mChart.setVisibleXRangeMaximum(150.0f);
            this.mChart.setMaxVisibleValueCount(150);
            this.mChart.moveViewToX((float) lineData.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet lineDataSet = new LineDataSet((List<Entry>) null, "Dynamic Data");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(3.0f);
        //lineDataSet.setColor(SupportMenu.CATEGORY_MASK);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        return lineDataSet;
    }

    private void feedMultiple() {
        if (this.thread != null) {
            this.thread.interrupt();
        }
        this.thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    boolean unused = MainActivity.this.plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.thread.start();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        registerSensors();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.thread != null) {
            this.thread.interrupt();
        }
        unRegisterSensors();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        unRegisterSensors();
        this.thread.interrupt();
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        unRegisterSensors();
    }

    public void chartSetup() {
        this.mChart.getDescription().setEnabled(true);
        this.mChart.getDescription().setText("Real-Time Roll Plot");
        this.mChart.setTouchEnabled(true);
        this.mChart.setDragEnabled(true);
        this.mChart.setScaleEnabled(true);
        this.mChart.setDrawGridBackground(true);
        this.mChart.setPinchZoom(true);
        this.mChart.setBackgroundColor(-1);
        LineData lineData = new LineData();
        lineData.setValueTextColor(-1);
        this.mChart.setData(lineData);
        Legend legend = this.mChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(-1);
        XAxis xAxis = this.mChart.getXAxis();
        xAxis.setTextColor(-1);
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);
        YAxis axisLeft = this.mChart.getAxisLeft();
        axisLeft.setTextColor(-1);
        axisLeft.setDrawGridLines(false);
        axisLeft.setAxisMaximum(180.5f);
        axisLeft.setAxisMinimum(-180.5f);
        axisLeft.setZeroLineColor(-3355444);
        axisLeft.setZeroLineWidth(1.0f);
        axisLeft.setEnabled(true);
        axisLeft.setDrawZeroLine(true);
        this.mChart.getAxisRight().setEnabled(false);
        this.mChart.getAxisLeft().setDrawGridLines(false);
        this.mChart.getXAxis().setDrawGridLines(false);
        this.mChart.setDrawBorders(false);
    }
}