package ru.zont.guesstheanime;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

class AdShower {

    static InterstitialAd load(Context context) {
        InterstitialAd interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId("ca-app-pub-7799305268524604/2483629164");
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
        return interstitial;
    }
}