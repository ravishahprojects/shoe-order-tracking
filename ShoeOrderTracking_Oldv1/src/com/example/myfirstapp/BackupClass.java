package com.example.myfirstapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

/*
 * Unused methods from MainActivity
 */
public class BackupClass {

	private static final String PROPERTIES_FILE = null;
	private Context context;
	private String deptName;

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

	public void writeData1() throws FileNotFoundException {
		FileOutputStream fOut = null;
		OutputStreamWriter myOutWriter = null;
		try {
			File file = new File(PROPERTIES_FILE);
			file.createNewFile();
			fOut = new FileOutputStream(file);
			myOutWriter = new OutputStreamWriter(fOut);
			deptName = "dept1";
			myOutWriter.append(deptName);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (myOutWriter != null)
					myOutWriter.close();
				if (fOut != null)
					fOut.close();
			} catch (IOException e) {
				// Ignore
			}

		}
	}

}
