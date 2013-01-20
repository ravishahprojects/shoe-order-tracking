package com.example.myfirstapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

public class Utility {

	public static Button createAndGetButton(final int id,
			final CharSequence text, Context context) {
		final Button btn = new Button(context);
		btn.setId(id);
		btn.setText(text);
		btn.setTextColor(Color.WHITE);
		btn.setBackgroundColor(Color.GRAY);
		return btn;
	}

	public static TextView createAndGetTextView(final int id,
			final CharSequence text, Context context) {
		final TextView tv = new TextView(context);
		tv.setId(id);
		tv.setText(text);
		tv.setTextColor(Color.WHITE);
		return tv;
	}

	public static void alert(Object obj, Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle("Alert");
		alertDialogBuilder.setMessage("" + obj);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public static char[] getHttpRequest(String url) throws IOException,
			ClientProtocolException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse response = httpClient.execute(request);
		HttpEntity responseEntity = response.getEntity();

		// Read response data into buffer
		char[] buffer = new char[(int) responseEntity.getContentLength()];
		InputStream stream = responseEntity.getContent();
		InputStreamReader reader = new InputStreamReader(stream);
		reader.read(buffer);
		stream.close();
		return buffer;
	}

	public static String getNormalizedString(char[] response) {
		String str = "";
		for (char c : response) {
			str += c;
		}
		str = str.replace("\"", "");
		return str;
	}

}
