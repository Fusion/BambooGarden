package com.voilaweb.mobile.bamboogarden;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutDialog extends Dialog {

	private static Context mContext = null;
	
	
	public AboutDialog(Context context) {
		super(context);
		mContext = context;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		String version = getAppVersion();
		setContentView(R.layout.about);
		//TextView tv = (TextView)findViewById(R.id.legal_text);
		//tv.setText(readRawTextFile(R.raw.legal));
		TextView tv = (TextView)findViewById(R.id.info_text);
		tv.setText(Html.fromHtml(
                readRawTextFile(R.raw.info).
                        replaceAll("<!--version-->", version)));
		tv.setLinkTextColor(R.color.rbm_headers_color); // tee-hee
		Linkify.addLinks(tv, Linkify.ALL);

        findViewById(R.id.button_credits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, CreditsActivity.class));
            }
        });

		setCanceledOnTouchOutside(true); // easily dismissed
	}
	
	
	private String getAppVersion() {
		String version = "?";
		try {
			PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {}
		return version;
	}
	
	
	public static String readRawTextFile(int id) {
		InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();
        try {
        	while (( line = buf.readLine()) != null) text.append(line);
         } catch (IOException e) {
            return null;
         }
         return text.toString();
     }
}
