package com.nocompany.bober.myfirstapplication;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by bober on 10/24/2016.
 */

public class AdManager extends Activity {

    public static InterstitialAd interAd;
    public static AdRequest request;

    public AdManager(Context context){
        interAd = new InterstitialAd(context);
        interAd.setAdUnitId("ca-app-pub-8074770329703494/1634556364");
        request = new AdRequest.Builder().build();
        interAd.loadAd(request);
        interAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed()
            {
                interAd.loadAd(request);
            }
        });
    }

    public void displayAd(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(interAd.isLoaded())
                    interAd.show();
            }});
    }
}
