package com.example.robin.pattern_search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
//import android.widget.ImageView;
import android.widget.TextView;

import be.groept.emedialab.util.GlobalResources;

public class StartActivity extends Activity{
    private View mContentView;

    TextView txtMessage;

    //Animation
  Animation animMove;
Animation animBounce;
    Animation animSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        GlobalResources.getInstance().getDevice().setMac(BluetoothAdapter.getDefaultAdapter().getAddress());
        mContentView = findViewById(R.id.Pattern_Search);
        //setContentView(R.layout.activity_start);
        txtMessage = (TextView) findViewById(R.id.textView);

        // load the animations
        animMove = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move);

                txtMessage.setVisibility(View.VISIBLE);
                // start the animation
                txtMessage.startAnimation(animMove);

        animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        Button JOINButton = (Button) findViewById(R.id.join);
        JOINButton.setVisibility(View.VISIBLE);
        JOINButton.startAnimation(animBounce);

        Button createButton = (Button) findViewById(R.id.start_new);
        createButton.setVisibility(View.VISIBLE);
        createButton.startAnimation(animBounce);

        animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        Button RULESButton = (Button) findViewById(R.id.rules);
        RULESButton.setVisibility(View.VISIBLE);
        createButton.startAnimation(animBounce);


        JOINButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                           Intent intent = new Intent(getBaseContext(), ConnectionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("JOIN", true);
                            intent.putExtras(bundle);
                            StartActivity.this.startActivity(intent);

                        }
                    });

        createButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), ConnectionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("JOIN", false);
                            intent.putExtras(bundle);
                            StartActivity.this.startActivity(intent);
                        }
                    });

                    RULESButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), ConnectionActivity.class);
                            StartActivity.this.startActivity(intent);
                        }
                    });

                    hide();
                }

                @SuppressLint("InlinedApi")
                private void hide() {

                    //Lollipop and higher
                    if (Build.VERSION.SDK_INT >= 21) {
                        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    }
                }

                @Override
                protected void onResume() {
                    super.onResume();
                    hide();

            }

         }


