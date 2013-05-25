package com.as.shoeordertracking;

import java.util.HashSet;
import java.util.Set;

public class Constants {

	public static final String GET_WORK = "GetWork";
	public static final String WS_UPDATE_WORK_API = "UpdateWork";
	public static final String UPDATE_WORK_IN = "In";
	public static final String UPDATE_WORK_OUT = "Out";
	public static final String UNDO_IN = "Undo - In";
	public static final String UNDO_OUT = "Undo - Out";
	public static final String WS_UNDO_API = "UndoUpdateWork";	
	public static final String SEPARATOR = "/";
	public static final String DEPT_NAME = "DEPT_NAME";
	public static final String PROPERTIES_FILE = "settings.properties";
	public static final String WS_GET_STATUS_API = "GetStatus";
	public static final String WS_GET_NEW_WORK_API = "GetPreviousDepartmentStatus";
	public static final String WS_GET_TOTAL_PAIRS_API = "GetTotalOutstandingPairs";
	//public static String HTTP_SERVICE1_SVC; //= "http://192.168.1.15:8081/Service1.svc";
	
	public static final Set<String> keysToProcess = new HashSet<String>();

	static {
		keysToProcess.add("ono");
		keysToProcess.add("cname");
		keysToProcess.add("style");
		keysToProcess.add("colour");
		keysToProcess.add("ssize");		
	}

	public static final String UPDATE_WORK_SUCCESS = "Work Updated";

}
