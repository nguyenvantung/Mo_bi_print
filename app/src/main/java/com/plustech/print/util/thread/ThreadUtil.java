package com.plustech.print.util.thread;

/*
 * Copyright 2013 NVan
 */
import android.util.Log;

public class ThreadUtil {
	
	/**
	 * Stop threads by name in array.
	 * @param threadNames
	 */
	public static void stopThread(String[] threadNames) {
		Thread[] threads = new Thread[Thread.activeCount()];
		Thread.enumerate(threads);
		String _threadName = "";

		for (Thread t : threads) {
			try {
				_threadName = t.getName();
				for (String threadName : threadNames) {
//					Log.d(ThreadUtil.class.getSimpleName(), "Thread name: "
//							+ _threadName);
					if (_threadName.contains(threadName)) {
//						Log.d(ThreadUtil.class.getSimpleName(),
//								"Stop thread name: " + _threadName + "...");
						Thread.sleep(5000);
						t.interrupt();
//						Log.d(ThreadUtil.class.getSimpleName(), "Thread name: "
//								+ _threadName + " stoped");
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Stop all threads
	 */
	public static void stopAllThread() {
		Thread[] threads = new Thread[Thread.activeCount()];
		Thread.enumerate(threads);
		String _threadName = "";

		for (Thread t : threads) {
			
			try {
				_threadName = t.getName();
				Thread.sleep(1000);
				t.interrupt();
//				Log.d(ThreadUtil.class.getSimpleName(), "Thread name: "
//						+ _threadName + " stoped");

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
