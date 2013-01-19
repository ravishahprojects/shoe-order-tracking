package com.example.myfirstapp;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

/*
 * Unused methods from MainActivity
 */
public class BackupClass {

	private Context context;

	public Button createAndGetButton(final int id, final CharSequence text) {
		final Button btn = new Button(context);
		btn.setId(id);
		btn.setText(text);
		btn.setTextColor(Color.WHITE);
		return btn;
	}

	public TextView createAndGetTextView(final int id, final CharSequence text) {
		final TextView tv = new TextView(context);
		tv.setId(id);
		tv.setText(text);
		tv.setTextColor(Color.WHITE);
		return tv;
	}

}
