/**
 * 
 */
package com.example.myfirstapp.listener;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.myfirstapp.Constants;
import com.example.myfirstapp.util.Utility;

/**
 * @author Ravi
 * 
 */
public class BtnClickListener implements OnClickListener {

	private SuccessHandler handler;

	@Override
	public void onClick(View v) {
		Button btn = (Button) v;
		int id = btn.getId();
		try {
			CharSequence text = btn.getText();
			if (Constants.UPDATE_WORK.equalsIgnoreCase(text.toString())) {
				updateWork(id, btn);
			} else if (Constants.UNDO.equalsIgnoreCase(text.toString())) {
				undoUpdateWork(id, btn);
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

	private String updateWork(int id, Button btn)
			throws ClientProtocolException, IOException {
		String url = Constants.HTTP_SERVICE1_SVC + Constants.SEPARATOR
				+ Constants.WS_UPDATE_WORK_API + Constants.SEPARATOR + id;
		char[] response = Utility.getHttpRequest(url);
		processUpdateWorkResponse(response, id, btn);
		return response.toString();
	}

	private String undoUpdateWork(int id, Button btn) throws ClientProtocolException,
			IOException {
		String url = Constants.HTTP_SERVICE1_SVC + Constants.SEPARATOR
				+ Constants.WS_UNDO_API + Constants.SEPARATOR + id;
		char[] response = Utility.getHttpRequest(url);
		processUndoUpdateWorkResponse(response, id, btn);
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