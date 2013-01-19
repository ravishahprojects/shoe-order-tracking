package com.example.myfirstapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myfirstapp.listener.SuccessHandler;
import com.example.myfirstapp.util.Utility;

public class MainActivity extends Activity implements SuccessHandler {

	final Context context = this;
	private boolean headerRow;

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
		char[] buffer = getHttpRequest(url);
		return buffer;
	}

	private char[] getHttpRequest(String url) throws IOException,
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

	private String enhanceResponse(char[] buffer) {
		String responseString = new String(buffer);
		responseString = responseString.substring(1,
				responseString.length() - 1);
		responseString = responseString.replace("\\", "");
		return responseString;
	}

	private void processGetWorkResponse(String responseString)
			throws JSONException {
		System.out.println("JSon Reply ::" + responseString);
		JSONArray vehicle = new JSONArray(responseString);
		processViewForResponse(vehicle);
	}

	private void processViewForResponse(JSONArray vehicle) throws JSONException {
		ScrollView sv = new ScrollView(this);
		TableLayout ll = new TableLayout(this);
		ll.setBackgroundColor(Color.BLACK);

		headerRow = true;

		for (int i = 0; i < vehicle.length(); i++) {
			int j = 0;
			TableRow tbrow = new TableRow(this);
			JSONObject row = vehicle.getJSONObject(i);
			processKeys(i, j, tbrow, row);
			ll.addView(tbrow);
			if (headerRow) {
				headerRow = false;
				i--;
			}
			// alert(row.getString("ono") + " - " + row.getString("style"));
		}
		sv.addView(ll);
		setContentView(sv);
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
			if (headerRow) {
				txtLabel = key + "|";
				TextView view = Utility.createAndGetTextView(id, txtLabel,
						context);
				tbrow.addView(view);
			} else {
				txtLabel = row.getString(key);
				if ("id".equalsIgnoreCase(key)) {
					Button btn = Utility.createAndGetButton(
							Integer.valueOf(txtLabel.toString()).intValue(),
							"Update Work", context);
					ClickListener clickListener = new ClickListener();
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
		}
		return j;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class ClickListener implements OnClickListener {

		private SuccessHandler handler;

		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			int id = btn.getId();
			try {
				updateWork(id, btn);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public SuccessHandler getHandler() {
			return handler;
		}

		public void setHandler(SuccessHandler handler) {
			this.handler = handler;
		}

		private String updateWork(int id, Button btn)
				throws ClientProtocolException, IOException {
			String url = Constants.HTTP_SERVICE1_SVC + Constants.SEPARATOR
					+ Constants.UPDATE_WORK + Constants.SEPARATOR + id;
			// Utility.alert(url, context);
			char[] response = getHttpRequest(url);
			processUpdateWorkResponse(response, id, btn);
			return response.toString();
		}

		private void processUpdateWorkResponse(char[] response, int id,
				Button btn) {
			String str = getString(response);
			System.out.println("Response :::::::" + str);
			// Show alerts based on the response
			if (Constants.UPDATE_WORK_SUCCESS.equalsIgnoreCase(str)) {
				handleSuccess(btn);
			} else {
				handleFailure(btn);
			}
		}

		private void handleSuccess(Button v) {
			handler.handleSuccess(v);
		}
	}

	@Override
	public void handleSuccess(View v) {
		Button btn = (Button) v;
		btn.setBackgroundColor(Color.CYAN);
		btn.setText("Undo");
	}

	public String getString(char[] response) {
		String str = "";
		for (char c : response) {
			str += c;
		}
		return str;
	}

	@Override
	public void handleFailure(View v) {
		Button btn = (Button) v;
		btn.setBackgroundColor(Color.RED);
	}
}
