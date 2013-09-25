package com.voilaweb.mobile.bamboogarden;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;


public class CreditsActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View creditsView = inflater.inflate(R.layout.credits, null);
        setContentView(creditsView);
        WebView cwv = (WebView)creditsView.findViewById(R.id.credits_content);
        cwv.loadUrl("file:///android_asset/credits.html");
    }
}
