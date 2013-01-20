package com.example.myfirstapp;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myfirstapp.listener.BtnClickListener;
import com.example.myfirstapp.listener.SuccessHandler;
import com.example.myfirstapp.util.Utility;

public class MainActivity extends Activity implements SuccessHandler {

	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void handleButton(View v) {
		try {
			tryWCF();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void tryWCF() throws ClientProtocolException, IOException,
			JSONException {
		char[] response = requestData();
		processGetWorkResponse(enhanceResponse(response));
	}

	private char[] requestData() throws IOException, ClientProtocolException {
		String url = Constants.HTTP_SERVICE1_SVC + Constants.SEPARATOR
				+ Constants.GET_WORK + Constants.SEPARATOR + "dept1";
		char[] buffer = Utility.getHttpRequest(url);
		return buffer;
	}

	private String enhanceResponse(char[] buffer) {
		String responseString = new String(buffer);
		responseString = responseString.substring(1,
				responseString.length() - 1);
		responseString = responseString.replace("\\", "");
		return responseString;
	}

	private void processGetWorkResponse(String responseString)
			throws JSONException {
		JSONArray vehicle = new JSONArray(responseString);
		processViewForResponse(vehicle);
	}

	private void processViewForResponse(JSONArray result) throws JSONException {
		LayoutInflater inflater = getLayoutInflater();

		ScrollView sView = (ScrollView) inflater.inflate(
				R.layout.activity_main, null);
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

	private void processKeys(int i, int j, TableRow tbrow, JSONObject row)
			throws JSONException {
		Iterator keys = row.keys();
		while (keys.hasNext()) {
			j = processKey(i, j, tbrow, row, keys);
		}
	}

	private int processKey(int i, int j, TableRow tbrow, JSONObject row,
			Iterator keys) throws JSONException {
		CharSequence txtLabel;
		Object next = keys.next();
		String key = next.toString();
		if (Constants.keysToProcess.contains(key)) {
			int id = i * ++j;
			txtLabel = row.getString(key);
			if ("id".equalsIgnoreCase(key)) {
				Button btn = Utility.createAndGetButton(
						Integer.valueOf(txtLabel.toString()).intValue(),
						Constants.UPDATE_WORK, context);
				BtnClickListener clickListener = new BtnClickListener();
				clickListener.setHandler(this);
				btn.setOnClickListener(clickListener);
				tbrow.addView(btn);
			} else {
				txtLabel = txtLabel + "|";
				TextView view = Utility.createAndGetTextView(id, txtLabel,
						context);
				tbrow.addView(view);
			}
		}
		return j;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
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
