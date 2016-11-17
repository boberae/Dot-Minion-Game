package com.nocompany.bober.myfirstapplication;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    private SoundPool sounds;
    private SoundPool.Builder soundBuilder;
    private AudioAttributes attributes;
    private AudioAttributes.Builder attributesBuilder;
    private int music;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8074770329703494/");

        //Determine screen height and width for scaling
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;
        Constants.PLAYER_RADIUS_INITIAL = dm.heightPixels / 25;
        Constants.DOT_RADIUS = dm.heightPixels / 100;

        createSoundPool();
        startMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
    }


    public void launchGame(View v){
        setContentView(new GamePanel(this));
        mp.stop();
    }

    public void createSoundPool(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            attributesBuilder = new AudioAttributes.Builder();
            attributesBuilder.setUsage(AudioAttributes.USAGE_GAME);
            attributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            attributes = attributesBuilder.build();
            soundBuilder = new SoundPool.Builder();
            soundBuilder.setAudioAttributes(attributes);
            sounds = soundBuilder.build();
        }
        else{
            sounds = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        music = sounds.load(this, R.raw.lose5, 1);
        sounds.setLoop(music,4);
    }

    public void startMusic(){
        mp = MediaPlayer.create(this, R.raw.music4);
        mp.setLooping(true);
        mp.start();
    }

    public void goToInstructions(View v) {
        setContentView(R.layout.help_screen_1);
    }

    public void nextInstruction(View v) { setContentView(R.layout.help_screen_2); }

    public void returnToMenu(View v) {
        setContentView(R.layout.activity_main);
    }

}
