package com.caffeine.coronavirusnews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private WebView web;
    private RelativeLayout splash, heading, today;
    private ProgressBar pbar;

    private Dialog bangladesh;
    private String con="", rec="", dea="";
    private DatabaseReference reference;

    private static final String AD_UNIT_ID = "ca-app-pub-8004167717556638/2725694245";
    private InterstitialAd mInterstitialAd;
    private ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_UNIT_ID);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        web = findViewById(R.id.webview);
        splash = findViewById(R.id.splash);
        heading = findViewById(R.id.heading);
        today = findViewById(R.id.today);
        pbar = findViewById(R.id.pbar);
        bangladesh = new Dialog(this);

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(android_id);
        ref.setValue(android_id);

        reference = FirebaseDatabase.getInstance().getReference().child("Corona");

        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        FuckingFuck fuck = new FuckingFuck("https://accounts.google.com/ServiceLogin?passive=1209600&continue=https://google.com/covid19-map/&followup=https://google.com/covid19-map/");
        web.setWebViewClient(fuck);
        web.loadUrl("https://google.com/covid19-map/");

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
                else {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            }
        });

        web.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {

                if (progress < 100){
                    pbar.setProgress(progress);
                }

                if (progress == 100){
                    splash.setAnimation(anim);
                    splash.setVisibility(View.GONE);
                    pbar.setVisibility(View.GONE);
                    heading.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mInterstitialAd.isLoaded()){
                                mInterstitialAd.show();
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                            else {
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        }
                    }, 5000);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }
                        else {
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }

                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });
            }
        }, 1, 50, TimeUnit.SECONDS);
    }

    private void showDialog(){
        bangladesh.setContentView(R.layout.todays_bangladesh);

        final TextView confirmed, recovered, death, hours;
        final ProgressBar progressBar;
        confirmed = bangladesh.findViewById(R.id.confirmed);
        recovered = bangladesh.findViewById(R.id.recovered);
        death = bangladesh.findViewById(R.id.death);
        hours = bangladesh.findViewById(R.id.hours);
        progressBar = bangladesh.findViewById(R.id.progress);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                con = dataSnapshot.child("con").getValue(String.class);
                rec = dataSnapshot.child("rec").getValue(String.class);
                dea = dataSnapshot.child("dea").getValue(String.class);

                String HOUR = "24 hours";
                String CON = "Confirmed: " + con;
                String REC = "Recovered: " + rec;
                String DEA = "Death: " + dea;
                progressBar.setVisibility(View.GONE);

                hours.setText(HOUR);
                confirmed.setText(CON);
                recovered.setText(REC);
                death.setText(DEA);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bangladesh.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bangladesh.show();
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()){
            web.goBack();
        }
        else {
            super.onBackPressed();
            finish();
        }
    }
}
