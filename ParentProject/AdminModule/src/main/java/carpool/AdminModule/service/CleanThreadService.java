package carpool.AdminModule.service;

import carpool.HttpServer.clean.ScheduledClean;
import carpool.HttpServer.common.DebugLog;

public class CleanThreadService extends Thread {
    // This method is called when the thread runs
    public void run() {
		ScheduledClean scheduledClean = new ScheduledClean(04, 00, 00);
		scheduledClean.start();
		DebugLog.d("ScheduledClean thread starting, cleaning at 4:30AM every day");
    }
}