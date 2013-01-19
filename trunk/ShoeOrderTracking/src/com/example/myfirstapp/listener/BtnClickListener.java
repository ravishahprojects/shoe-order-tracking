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
		char[] response = Utility.getHttpRequest(url);
		processUpdateWorkResponse(response, id, btn);
		return response.toString();
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