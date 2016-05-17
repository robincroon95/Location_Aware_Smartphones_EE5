package com.example.bluetooth_data_transfer_test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;

import be.groept.emedialab.communications.DataHandler;
import be.groept.emedialab.communications.DataPacket;
import be.groept.emedialab.image_manipulation.RunPatternDetector;
import be.groept.emedialab.server.data.Position;
import be.groept.emedialab.util.GlobalResources;

public class DataTransfer extends AppCompatActivity {
    private static final String TAG = "DataTransfer";
    private BluetoothAdapter BT;
    private final int BLUETOOTH_REQUEST = 1;
    public View mContentView;
    private static final int DISCOVER_DURATION = 300;
    public static final int TYPE_CO = 0;
    private TextView ownPositionTextView;
    private TextView otherPositionTextView;
    private Position otherPosition = null;
    private Thread runPatternThread;
    final Activity activity = this;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == DataHandler.DATA_TYPE_DATA_PACKET) {
                Serializable data;
                if ((data = GlobalResources.getInstance().readData()) != null)
                    handleData((DataPacket) data);
            } else if (msg.what == DataHandler.DATA_TYPE_COORDINATES) {
                otherPosition = (Position) msg.obj;
                updatePosition(otherPosition, otherPositionTextView, "OTHER");
            } else if (msg.what == DataHandler.DATA_TYPE_DEVICE_DISCONNECTED) {
                BluetoothDevice device = (BluetoothDevice) msg.obj;
                Toast.makeText(DataTransfer.this, "Device " + device.getAddress() + " disconnected!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == DataHandler.DATA_TYPE_OWN_POS_UPDATED) {
                if (!GlobalResources.getInstance().getClient()) {
                    //The server position is updated, send this to the client!
                    GlobalResources.getInstance().sendData(new DataPacket(TYPE_CO, (Position) msg.obj));
                }
                updatePosition((Position) msg.obj, ownPositionTextView, "SELF");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datatransfer);
        mContentView = findViewById(R.id.relativeLayout);
        ownPositionTextView = (TextView) findViewById(R.id.ownPosition);

        otherPositionTextView = (TextView) findViewById(R.id.otherPosition);

        GlobalResources.getInstance().setHandler(handler);

        hide();
    }

    public Thread getThread() {
        return new Thread() {
            @Override
            public void run() {
                new RunPatternDetector(activity);
            }
        };
    }

    @SuppressLint("InlinedApi")
    private void hide() {

        // Lollipop and higher
        if (Build.VERSION.SDK_INT >= 21) {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    private void updatePosition(Position position, TextView textView, String prefix) {
        if (position != null) {
            if (position.getFoundPattern()) {
                textView.setTextColor(Color.parseColor("green"));
                textView.setText(String.format("%s: (%.2f, %.2f, %.2f) %.1f°", prefix, position.getX(), position.getY(), position.getZ(), position.getRotation()));
            } else {
                textView.setTextColor(Color.parseColor("red"));
            }
        }
    }

    private void handleData(DataPacket dataPacket) {
        switch (dataPacket.getDataType()) {
            case TYPE_CO: // only run by client

                otherPosition = (Position) dataPacket.getOptionalData();

                //Log.d(TAG, "RECEIVED COORDINATES FROM SERVER: " + otherPosition);
                updatePosition(otherPosition, otherPositionTextView, "Yours");
                break;

        }
    }

    public void BluetoothTransfer(View v) {

        BT = BluetoothAdapter.getDefaultAdapter();

        if (BT == null) {

            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG).show();
        } else {
            enableBluetooth();
        }
    }

    public void enableBluetooth() {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(i, BLUETOOTH_REQUEST);

    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent data) {
        if (request_code == BLUETOOTH_REQUEST && result_code == DISCOVER_DURATION) {

            Toast.makeText(getBaseContext(), " your bluetooth is enabled", Toast.LENGTH_LONG).show();


        }
    }
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " on resume called");

        hide();

        //Will continuously call the RunPatternDetector class
        runPatternThread = getThread();
        runPatternThread.run();

    }
}