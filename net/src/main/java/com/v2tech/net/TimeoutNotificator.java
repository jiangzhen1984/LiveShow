package com.v2tech.net;

import com.V2.jni.util.V2Log;

import java.util.Queue;

public class TimeoutNotificator implements Runnable {

	public interface TimeoutHandler {

		public void onTimeout(LocalBind bind);
	}

	private static int idx = 1;
	private TimeoutHandler handler;
	private Queue<LocalBind> waiting;
	private Thread deamon;

	public TimeoutNotificator(TimeoutHandler handler, Queue<LocalBind> waiting) {
		super();
		this.handler = handler;
		this.waiting = waiting;
	}

	public void requestStart() {
		requestStop();
		deamon = new Thread(this);
		deamon.setName("TimeoutWatchDog" + idx++);
		deamon.start();
	}

	public void requestStop() {
		if (deamon != null && deamon.isAlive()) {
			synchronized(this) {
				deamon.interrupt();
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			
			for (LocalBind bind : waiting) {
				if (bind.timeout) {
					continue;
				}
				if (System.currentTimeMillis() - bind.sendtime > 3000) {
					V2Log.e("Timeout trigger ==> curr :" +System.currentTimeMillis()+" send-time:" + bind.sendtime +"  queue-time:"+ bind.queuetime +"  " + bind);
					bind.timeout = true;
					handler.onTimeout(bind);
				}
			}
			
			synchronized (this) {
				try {
					wait(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
