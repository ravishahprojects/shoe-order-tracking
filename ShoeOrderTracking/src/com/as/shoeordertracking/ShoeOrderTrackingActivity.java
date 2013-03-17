package com.as.shoeordertracking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.as.shoeordertracking.listener.BtnClickListener;
import com.as.shoeordertracking.listener.SuccessHandler;
import com.as.shoeordertracking.util.Utility;

public class ShoeOrderTrackingActivity extends Activity implements SuccessHandler {

	final Context context = this;
	private String deptName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			handleSettingsFileReadWrite();
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			super.onCreate(savedInstanceState);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (deptName != null)
			setContentView(R.layout.activity_shoe_order_tracking);
	}

	private void handleSettingsFileReadWrite() throws FileNotFoundException {
		try {
			File file = context.getFileStreamPath(Constants.PROPERTIES_FILE);
			if (file.exists()) {
				readData();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (deptName == null || deptName.isEmpty()) {
			writeData();
		}
	}

	private void writeData() {
		try {
			Properties properties = loadProperties(R.raw.settings);
			String deptNameProp = properties.getProperty(Constants.DEPT_NAME);
			if (deptNameProp == null || deptNameProp.isEmpty()) {
				loadSettingsView();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadSettingsView() {
		setContentView(R.layout.activity_settings);
	}

	private Properties loadProperties(int id) throws IOException {
		InputStream rawResource = this.getResources().openRawResource(id);
		Properties properties = new Properties();
		properties.load(rawResource);
		return properties;
	}

	private void readData() throws FileNotFoundException, IOException {
		Properties properties = loadProperties(R.raw.settings);
		deptName = properties.getProperty(Constants.DEPT_NAME);
		Log.i("readData", "Property read from file :" + Constants.DEPT_NAME	+ ":" + deptName);
	}

	public void handleUpdateSettingsButton(View v) {
		try {
			EditText edtDeptName = (EditText) findViewById(R.id.edtDeptName);
			deptName = edtDeptName.getText().toString();
			Properties properties = loadProperties(R.raw.settings);
			FileOutputStream outputStream = this.getResources().openRawResourceFd(R.raw.settings).createOutputStream();
			properties.store(outputStream, "Storing Dept Name");
			properties.setProperty(Constants.DEPT_NAME, deptName);
			setContentView(R.layout.activity_shoe_order_tracking);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleRefreshButton(View v) {
		try {
			refreshOrderData();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void refreshOrderData() throws ClientProtocolException, IOException, JSONException {
		char[] response = requestOrderData();
		processGetWorkResponse(enhanceResponse(response));
	}

	private char[] requestOrderData() throws IOException, ClientProtocolException {
		String url = Constants.HTTP_SERVICE1_SVC + Constants.SEPARATOR + Constants.GET_WORK + Constants.SEPARATOR + deptName;
		return Utility.getHttpRequest(url);
	}

	private String enhanceResponse(char[] buffer) {
		String responseString = new String(buffer);
		responseString = responseString.substring(1, responseString.length() - 1);
		responseString = responseString.replace("\\", "");
		return responseString;
	}

	private void processGetWorkResponse(String responseString) throws JSONException {
		JSONArray vehicle = new JSONArray(responseString);
		processViewForResponse(vehicle);
	}

	private void processViewForResponse(JSONArray result) throws JSONException {
		LayoutInflater inflater = getLayoutInflater();
		ScrollView sView = (ScrollView) inflater.inflate(R.layout.activity_shoe_order_tracking, null);
		TableLayout tableLayout = (TableLayout) sView.getChildAt(0);
		for (int i = 0; i < result.length(); i++) {
			int j = 0;
			TableRow tbrow = new TableRow(this);
			JSONObject row = result.getJSONObject(i);
			processKeys(i, j, tbrow, row);
			tableLayout.addView(tbrow);
		}
		sView.removeAllViews();
		sView.addView(tableLayout);
		setContentView(sView);
	}

	private void processKeys(int i, int j, TableRow tbrow, JSONObject row) throws JSONException {
		Iterator keys = row.keys();
		while (keys.hasNext()) {
			j = processKey(i, j, tbrow, row, keys);
		}
	}

	private int processKey(int i, int j, TableRow tbrow, JSONObject row, Iterator keys) throws JSONException {
		CharSequence txtLabel;
		Object next = keys.next();
		String key = next.toString();
		if (Constants.keysToProcess.contains(key)) {
			int id = i * ++j;
			txtLabel = row.getString(key);
			if ("id".equalsIgnoreCase(key)) {
				Button btn = Utility.createAndGetButton(Integer.valueOf(txtLabel.toString()).intValue(), Constants.UPDATE_WORK, context);
				BtnClickListener clickListener = new BtnClickListener();
				clickListener.setHandler(this);
				btn.setOnClickListener(clickListener);
				tbrow.addView(btn);
			} else {
				// txtLabel = txtLabel + "|";
				TextView view = Utility.createAndGetTextView(id, txtLabel, context, this);
				tbrow.addView(view);
			}
		}
		return j;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_shoe_order_tracking, menu);
		return true;
	}

	@Override
	public void handleSuccess(View v) {
		Button btn = (Button) v;
		if (btn.getText().toString().equalsIgnoreCase(Constants.UPDATE_WORK)) {
			btn.setBackgroundColor(Color.GREEN);
			btn.setText(Constants.UNDO);
		} else if (btn.getText().toString().equalsIgnoreCase(Constants.UNDO)) {
			btn.setBackgroundColor(Color.GRAY);
			btn.setText(Constants.UPDATE_WORK);
		}
	}

	@Override
	public void handleFailure(View v) {
		Button btn = (Button) v;
		btn.setBackgroundColor(Color.RED);
	}
}
