/**
 * 
 */
package com.as.shoeordertracking.listener;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TableRow;

import com.as.shoeordertracking.Constants;
import com.as.shoeordertracking.R;
import com.as.shoeordertracking.ShoeOrderTrackingActivity;
import com.as.shoeordertracking.util.Utility;

/**
 * @author Ravi
 * 
 */
public class BtnClickListener implements OnClickListener {

	private SuccessHandler handler;

	@Override
	public void onClick(View v) {
		Button btn = (Button) v;
		//int id = btn.getId();
		try {
			CharSequence text = btn.getText().toString().split("\n")[0];
			if (Constants.UPDATE_WORK_IN.equalsIgnoreCase(text.toString().trim())) {
				updateWork(btn);				
			} else if (Constants.UNDO_IN.equalsIgnoreCase(text.toString().trim())) {
				undoUpdateWork(btn);
			} else if (Constants.UPDATE_WORK_OUT.equalsIgnoreCase(text.toString().trim())) {
				updateWork(btn);
			} else if (Constants.UNDO_OUT.equalsIgnoreCase(text.toString().trim())){				
				undoUpdateWork(btn);
			}
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

	private String updateWork(Button btn)
			throws ClientProtocolException, IOException {
		String[] s = btn.getTag().toString().split("-");
		String id = s[0];
		String opType = s[1];
		String deptName = s[2];
		String url = ShoeOrderTrackingActivity.SERVICE_URL + Constants.SEPARATOR
				+ Constants.WS_UPDATE_WORK_API + Constants.SEPARATOR + id
				+ Constants.SEPARATOR + deptName + Constants.SEPARATOR + opType ;
		char[] response = Utility.getHttpRequest(url);
		processUpdateWorkResponse(response, Integer.valueOf(id).intValue(), btn);	
		TableRow tr = (TableRow) btn.getParent();
		tr.setBackgroundResource(R.drawable.cell_shape);
		return response.toString();
		
	}

	private String undoUpdateWork(Button btn) throws ClientProtocolException,
			IOException {
		String[] s = btn.getTag().toString().split("-");
		String id = s[0];
		String opType = s[1];
		String deptName = s[2];
		String url = ShoeOrderTrackingActivity.SERVICE_URL + Constants.SEPARATOR
				+ Constants.WS_UNDO_API + Constants.SEPARATOR + id
				+ Constants.SEPARATOR + deptName + Constants.SEPARATOR + opType;
		char[] response = Utility.getHttpRequest(url);
		processUndoUpdateWorkResponse(response, Integer.valueOf(id).intValue(), btn);
		return response.toString();
	}
	
	private void processUndoUpdateWorkResponse(char[] response, int id,
			Button btn) {
		String str = Utility.getNormalizedString(response);
		// Show alerts based on the response
		if (Constants.UPDATE_WORK_SUCCESS.equalsIgnoreCase(str)) {
			handleSuccess(btn);
		} else {
			handleFailure(btn);
		}
	}

	private void processUpdateWorkResponse(char[] response, int id, Button btn) {
		String str = Utility.getNormalizedString(response);
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

	private void handleFailure(Button v) {
		handler.handleFailure(v);
	}

}