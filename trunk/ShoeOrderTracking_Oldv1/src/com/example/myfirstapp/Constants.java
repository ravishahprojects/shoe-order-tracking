package com.example.myfirstapp;

import java.util.HashSet;
import java.util.Set;

public class Constants {

	public static final String GET_WORK = "GetWork";
	public static final String WS_UPDATE_WORK_API = "UpdateWork";
	public static final String UPDATE_WORK = "Update Work";
	public static final String UNDO = "Undo";
	public static final String WS_UNDO_API = "UndoUpdateWork";
	public static final String HTTP_SERVICE1_SVC = "http://192.168.100.1:8081/Service1.svc";
	public static final String SEPARATOR = "/";
	public static final String DEPT_NAME = "DEPT_NAME";
	public static final String PROPERTIES_FILE = "settings.properties";

	public static final Set<String> keysToProcess = new HashSet<String>();

	static {
		keysToProcess.add("colour");
		keysToProcess.add("id");
		keysToProcess.add("ono");
	}

	public static final String UPDATE_WORK_SUCCESS = "Work Updated";

}
