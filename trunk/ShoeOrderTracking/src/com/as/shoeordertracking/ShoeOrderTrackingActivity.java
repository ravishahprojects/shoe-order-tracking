package com.as.shoeordertracking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.CalendarContract.Colors;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.View.OnClickListener;


import com.as.shoeordertracking.listener.BtnClickListener;
import com.as.shoeordertracking.listener.SuccessHandler;
import com.as.shoeordertracking.util.Utility;

public class ShoeOrderTrackingActivity extends Activity implements SuccessHandler {

	final Context context = this;
	private String deptName;
	private static String ipAddress;
	private String prevDeptName;
	private Handler mHandler = new Handler();
	private Handler mHandler1 = new Handler();
	public static String SERVICE_URL;
	
	private TextView tvDisplayDate;
	private DatePicker dpResult;
	private Button btnChangeDate;
 
	private int year;
	private int month;
	private int day;
 
	static final int DATE_DIALOG_ID = 999;
	
	
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

		if (deptName != null) {
			mHandler.removeCallbacks(autoRefreshOrderData);
			mHandler.postDelayed(autoRefreshOrderData,100);			
			mHandler1.removeCallbacks(getTotalOutstandingPairs);
			mHandler1.postDelayed(getTotalOutstandingPairs,100);			
			setContentView(R.layout.activity_shoe_order_tracking);			
		}
		//setCurrentDateOnView();
		//addListenerOnButton();
	}

	private Runnable getTotalOutstandingPairs = new Runnable() {
		public void run() {
			try {
				totalOutstandingPairs();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mHandler1.postDelayed(getTotalOutstandingPairs, 300000);
		}
	};
	
	private void totalOutstandingPairs() throws ClientProtocolException, IOException, JSONException{
		String url = ShoeOrderTrackingActivity.SERVICE_URL + Constants.SEPARATOR + Constants.WS_GET_TOTAL_PAIRS_API + Constants.SEPARATOR + deptName;
		char[] c = Utility.getHttpRequest(url);
		String responseString = enhanceResponse(c);
		JSONArray result = new JSONArray(responseString);
		JSONObject row = result.getJSONObject(0);
		String s = "Total Pairs : " + row.getString("totalpairs");
		TextView tv = (TextView) findViewById(R.id.color);
		tv.setText(s);
	}
	
	private Runnable autoRefreshOrderData = new Runnable() {
		public void run() {
			try {
				refreshOrderData();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mHandler.postDelayed(autoRefreshOrderData, 300000);
		}
	};
		
	private void handleSettingsFileReadWrite() throws FileNotFoundException {
		try {
			//File file = context.getFileStreamPath(getFilesDir().toString() + "/settings.properties");
			File file = new File(getFilesDir().toString() + "/settings.properties");
			
			if (file.exists()) {
				readData();
			} else {				
				writeData();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/* if (deptName == null || deptName.isEmpty()) {
			writeData();
		} */
	}

	private void writeData() {
		try {
			Properties properties = loadProperties(R.raw.settings);
			String deptNameProp = properties.getProperty(Constants.DEPT_NAME);
			if (deptNameProp == null || deptNameProp.isEmpty()) {
				loadSettingsView();
			} else {
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
		/* Properties properties = loadProperties(R.raw.settings);
		deptName = properties.getProperty(Constants.DEPT_NAME);
		Log.i("readData", "Property read from file :" + Constants.DEPT_NAME	+ ":" + deptName); */
		FileInputStream fis = openFileInput("settings.properties");
	    InputStreamReader inputStreamReader = new InputStreamReader(fis);
	    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	    StringBuilder sb = new StringBuilder();
	    String line;	    
	    while ((line = bufferedReader.readLine()) != null) {
	        sb.append(line);
	        sb.append("\n");
	    }
		fis.close();
		String[] lines = sb.toString().split("\\n");

		deptName = lines[0];
		ipAddress = lines[1];
		prevDeptName = lines[2];
		SERVICE_URL = "http://" + ipAddress.toString() + ":8081/Service1.svc";
	}
	
	public void handleUpdateSettingsButton(View v) {
		try {
			EditText edtDeptName = (EditText) findViewById(R.id.edtDeptName);
			deptName = edtDeptName.getText().toString();
			EditText edtIPAddress = (EditText) findViewById(R.id.edtIPAddress);
			ipAddress = edtIPAddress.getText().toString();
			EditText edtPrevDept = (EditText) findViewById(R.id.edtPrevDept);
			prevDeptName = edtPrevDept.getText().toString();
			
			FileOutputStream fos = openFileOutput("settings.properties", Context.MODE_WORLD_READABLE);
			fos.write(deptName.getBytes());
			fos.write("\n".getBytes());
			fos.write(ipAddress.getBytes());
			fos.write("\n".getBytes());
			fos.write(prevDeptName.getBytes());			
			fos.close();
			//Log.i("something", getFilesDir().toString());
			setContentView(R.layout.activity_shoe_order_tracking);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleRefreshButton(View v) {
		try {
			refreshOrderData();
			totalOutstandingPairs();
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
		String url = ShoeOrderTrackingActivity.SERVICE_URL + Constants.SEPARATOR + Constants.GET_WORK + Constants.SEPARATOR + deptName;
		return Utility.getHttpRequest(url);
	}
	
	private ArrayList<String> requestOrderStatus(int id, String deptName) throws IOException, ClientProtocolException, JSONException {	
		ArrayList<String> s = new ArrayList<String>();
		String url = ShoeOrderTrackingActivity.SERVICE_URL + Constants.SEPARATOR + Constants.WS_GET_STATUS_API + Constants.SEPARATOR + id + Constants.SEPARATOR + deptName;
		char[] c = Utility.getHttpRequest(url);
		String responseString = enhanceResponse(c);
		JSONArray result = new JSONArray(responseString);
		for (int i = 0; i < result.length(); i++) {
			JSONObject row = result.getJSONObject(i);
			s.add(row.getString("fdate") + " - " + row.getString("optype"));
		}
		return s;
	}

	private String enhanceResponse(char[] buffer) {
		String responseString = new String(buffer);
		responseString = responseString.substring(1, responseString.length() - 1);
		responseString = responseString.replace("\\", "");
		return responseString;
	}

	private void processGetWorkResponse(String responseString) throws JSONException, IOException {
		JSONArray vehicle = new JSONArray(responseString);
		processViewForResponse(vehicle);
	}

	private void processViewForResponse(JSONArray result) throws JSONException, IOException {
		LayoutInflater inflater = getLayoutInflater();
		ScrollView sView = (ScrollView) inflater.inflate(R.layout.activity_shoe_order_tracking, null);
		TableLayout tableLayout = (TableLayout) sView.getChildAt(0);

		for (int i = 0; i < result.length(); i++) {
			int j = 0;
			TableRow tbrow = new TableRow(this);
			JSONObject row = result.getJSONObject(i);
			processKeys(i, j, tbrow, row);
			tbrow.setBackgroundResource(R.drawable.cell_shape);
			tableLayout.addView(tbrow);
		}
		sView.removeAllViews();
		sView.addView(tableLayout);
		setContentView(sView);
		if (!deptName.equals(prevDeptName)) {
			displayNewWork();
		}
	}
	
	private void displayNewWork() throws ClientProtocolException, IOException, JSONException {
		String url = ShoeOrderTrackingActivity.SERVICE_URL + Constants.SEPARATOR + Constants.WS_GET_NEW_WORK_API + Constants.SEPARATOR + deptName + Constants.SEPARATOR + prevDeptName;
		char[] c = Utility.getHttpRequest(url);
		String responseString = enhanceResponse(c);
		JSONArray result = new JSONArray(responseString);
		for (int i = 0; i < result.length(); i++) {
			JSONObject row = result.getJSONObject(i);
			int id = Integer.valueOf(row.getString("id")).intValue();
			TableRow tr = (TableRow) findViewById(id);
			tr.setBackgroundResource(R.drawable.cell_shape_red);			
		}
	}

	@SuppressWarnings("rawtypes")
	private void processKeys(int i, int j, TableRow tbrow, JSONObject row) throws JSONException, IOException {
		Iterator keys = row.keys();
		/* while (keys.hasNext()) {
			j = processKey(i, j, tbrow, row, keys);
		} */
		String s = "";
		
		s += "\nOrder #: " + row.getString("ono");
		s += "\n" + row.getString("style") + " " + row.getString("colour") + " " + row.getString("ssize");
		s += "\n" + row.getString("cname");
		s += "\n" + JSONDateToDate(row.getString("delivery_date"),"date");
		s += "\n" + row.getString("dispatch_type");
		/* while (keys.hasNext()) {			
			Object next = keys.next();
			String key = next.toString();
			
			if (Constants.keysToProcess.contains(key)) {
				if (key.equalsIgnoreCase("ono")) {
					s += "\nOrder #: " + row.getString(key); 
				} else if (key.equalsIgnoreCase("cname")) {
					s += "\nCustomer: " + row.getString(key);
				} else if (key.equalsIgnoreCase("style")) {
					style += "\n" + row.getString(key);
				} else if (key.equalsIgnoreCase("colour")) {
					style += " " + row.getString(key);
				} else if (key.equalsIgnoreCase("ssize")) {
					style += " " + row.getString(key);
				} 
			} 
		} */
		int id = Integer.valueOf(row.getString("id")).intValue();
		tbrow.setId(id);
		
		Button view = Utility.createAndGetButton(10, s.substring(1), context, deptName);
		tbrow.addView(view);
		
		String inDate = "";
		String outDate = "";
		
		ArrayList<String> os = requestOrderStatus(id, deptName);
		for(int ctr=0; ctr < os.size(); ctr++) {
			String osd = os.get(ctr);
			String[] s1 = osd.split("-");
			String fdate = s1[0];
			String opType = s1[1];
			if (opType.trim().equalsIgnoreCase("in")) {
				inDate = JSONDateToDate(fdate, "datetime");
			}			
			if (opType.trim().equalsIgnoreCase("out")) {
				outDate = JSONDateToDate(fdate, "datetime");
			}
		}
		
		Button btn = Utility.createAndGetButton(id, Constants.UPDATE_WORK_IN, context, deptName);
		BtnClickListener clickListener = new BtnClickListener();
		clickListener.setHandler(this);
		btn.setOnClickListener(clickListener);
		if (!inDate.isEmpty()) {
			btn.setText(Constants.UNDO_IN + "\n" + inDate);
			btn.setTextColor(Color.GREEN);
		}
		tbrow.addView(btn);		
		
		Button btnOut = Utility.createAndGetButton(id, Constants.UPDATE_WORK_OUT, context, deptName);
		BtnClickListener clickListenerOut = new BtnClickListener();
		clickListenerOut.setHandler(this);
		btnOut.setOnClickListener(clickListenerOut);
		if(!outDate.isEmpty()){
			btnOut.setText(Constants.UNDO_OUT + "\n" + outDate);
			btnOut.setTextColor(Color.GREEN);
		}
		tbrow.addView(btnOut);
	}

	private String JSONDateToDate(String jsonDate, String dtype) {
	    int idx1 = jsonDate.indexOf("(");
	    int idx2 = jsonDate.indexOf(")");			    
	    long l = Long.valueOf(jsonDate.substring(idx1+1, idx2));	    
		Date date = new Date(l);
		SimpleDateFormat sdf;
		if (dtype.equalsIgnoreCase("datetime")) {
			sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		} else {
			sdf = new SimpleDateFormat("dd-MM-yyyy");	
		}
		return sdf.format(date);
	}
	
	/* @SuppressWarnings("rawtypes")
	private int processKey(int i, int j, TableRow tbrow, JSONObject row, Iterator keys) throws JSONException {
		CharSequence txtLabel;
		Object next = keys.next();
		String key = next.toString();
		if (Constants.keysToProcess.contains(key)) {
			int id = i * ++j;
			txtLabel = row.getString(key);
			if ("id".equalsIgnoreCase(key)) {
				Button btn = Utility.createAndGetButton(Integer.valueOf(txtLabel.toString()).intValue(), Constants.UPDATE_WORK_IN, context, deptName);
				BtnClickListener clickListener = new BtnClickListener();
				clickListener.setHandler(this);
				btn.setOnClickListener(clickListener);
				tbrow.addView(btn);
			} else {
				TextView view = Utility.createAndGetTextView(txtLabel, context, this);
				tbrow.addView(view);
			}
		}
		return j;
	} */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_shoe_order_tracking, menu);
		return true;
	}

	@Override
	public void handleSuccess(View v) {
		Button btn = (Button) v;
		if (btn.getText().toString().equalsIgnoreCase(Constants.UPDATE_WORK_IN)) {
			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");			
			btn.setText(Constants.UNDO_IN + "\n" + sdf.format(dt));
			btn.setTextColor(Color.GREEN);
		} else if (btn.getText().toString().toLowerCase().contains("undo - in")) { 
			btn.setText(Constants.UPDATE_WORK_IN);
			btn.setTextColor(Color.WHITE);
		} else if (btn.getText().toString().equalsIgnoreCase(Constants.UPDATE_WORK_OUT)) {		
			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");			
			btn.setText(Constants.UNDO_OUT + "\n" + sdf.format(dt));
			btn.setTextColor(Color.GREEN);
		}  else if (btn.getText().toString().toLowerCase().contains("undo - out")) { 
			btn.setText(Constants.UPDATE_WORK_OUT);
			btn.setTextColor(Color.WHITE);
		}
	}

	@Override
	public void handleFailure(View v) {
		Button btn = (Button) v;
		btn.setBackgroundColor(Color.RED);
	}
	
	 
	public void setCurrentDateOnView() {
		 
		tvDisplayDate = (TextView) findViewById(R.id.tvDate);
		dpResult = (DatePicker) findViewById(R.id.dpResult);
 
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
 
		// set current date into textview
		tvDisplayDate.setText(new StringBuilder()
			// Month is 0 based, just add 1
			.append(month + 1).append("-").append(day).append("-")
			.append(year).append(" "));
 
		// set current date into datepicker
		dpResult.init(year, month, day, null);
 
	}

	public void addListenerOnButton() {
		 
		btnChangeDate = (Button) findViewById(R.id.btnChangeDate);
		
		btnChangeDate.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) { 
				showDialog(DATE_DIALOG_ID); 
			} 
		}); 
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		   // set date picker as current date
		   return new DatePickerDialog(this, datePickerListener, 
                         year, month,day);
		}
		return null;
	}
 
	private DatePickerDialog.OnDateSetListener datePickerListener 
                = new DatePickerDialog.OnDateSetListener() {
 
		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
 
			// set selected date into textview
			tvDisplayDate.setText(new StringBuilder().append(month + 1)
			   .append("-").append(day).append("-").append(year)
			   .append(" "));
 
			// set selected date into datepicker also
			dpResult.init(year, month, day, null);
 
		}
	};
	
}
