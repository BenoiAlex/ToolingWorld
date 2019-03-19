package com.mitadt.vem.toolingworld;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends AppCompatActivity {

    ImageView bgapp, clover;
    LinearLayout textsplash, texthome, dashboard;
    Animation frombottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);


        bgapp = (ImageView) findViewById(R.id.bgapp);
        clover = (ImageView) findViewById(R.id.clover);
        textsplash = (LinearLayout) findViewById(R.id.textsplash);
        texthome = (LinearLayout) findViewById(R.id.texthome);

        dashboard = (LinearLayout) findViewById(R.id.dashboard);

        bgapp.animate().translationY(-1900).setDuration(800).setStartDelay(3000);
        clover.animate().alpha(0).setDuration(800).setStartDelay(6000);
        textsplash.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(3000);

        //texthome.startAnimation(frombottom);
        //dashboard.startAnimation(frombottom);

        new CountDownTimer(1000, 1000) {
            @Override
            public void onFinish() {

                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.frombottom, R.anim.bganim);
                finish();
            }

            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();
    }
}
