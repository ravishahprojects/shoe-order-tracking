package com.example.myfirstapp.util;

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

}
