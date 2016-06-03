package com.v2tech.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.v2tech.service.LiveMessageHandler;
import com.v2tech.service.LiveStatusHandler;
import com.v2tech.service.LiveWathcingHandler;

public class GlobalPresenterManager {

	private static GlobalPresenterManager instance;

	private Vector<WeakReference<BasePresenter>> list = new Vector<WeakReference<BasePresenter>>();
	private Vector<WeakReference<LiveStatusHandler>> liveStatusHandlerList = new Vector<WeakReference<LiveStatusHandler>>();
	private Vector<WeakReference<LiveMessageHandler>> liveMessageHandlerList = new Vector<WeakReference<LiveMessageHandler>>();
	private Vector<WeakReference<LiveWathcingHandler>> liveWatchingHandlerList = new Vector<WeakReference<LiveWathcingHandler>>();
	

	private GlobalPresenterManager() {
	}

	public synchronized static GlobalPresenterManager getInstance() {
		if (instance == null) {
			instance = new GlobalPresenterManager();
		}
		return instance;
	}

	public void onPresenterCreated(BasePresenter presenter) {
		list.add(new WeakReference<BasePresenter>(presenter));
		if (presenter instanceof LiveStatusHandler) {
			onLiveStatusHandlerCreated((LiveStatusHandler) presenter);
		} 
		if (presenter instanceof LiveMessageHandler) {
			onLiveMessageHandlerCreated((LiveMessageHandler) presenter);
		} 
		if (presenter instanceof LiveWathcingHandler) {
			onLiveWathcingHandlerCreated((LiveWathcingHandler) presenter);
		}
	}

	public void onPresenterDestroyed(BasePresenter presenter) {
		synchronized (list) {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				WeakReference<BasePresenter> w = list.get(i);
				BasePresenter act = w.get();
				if (act == presenter) {
					list.remove(i);

					if (act instanceof LiveStatusHandler) {
						onLiveStatusHandlerDestroyed((LiveStatusHandler) presenter);
					} 
					if (act instanceof LiveWathcingHandler) {
						onLiveWathcingHandlerDestroyed((LiveWathcingHandler) presenter);
					} 
					if (act instanceof LiveMessageHandler) {
						onLiveMessageHandlerDestroyed((LiveMessageHandler) presenter);
					}

					break;
				}
			}
		}

	}

	public void onLiveMessageHandlerCreated(LiveMessageHandler lsh) {
		liveMessageHandlerList.add(new WeakReference<LiveMessageHandler>(lsh));
	}

	public void onLiveMessageHandlerDestroyed(LiveMessageHandler lsh) {
		synchronized (liveMessageHandlerList) {
			int size = liveMessageHandlerList.size();

			for (int i = 0; i < size; i++) {
				WeakReference<LiveMessageHandler> w = liveMessageHandlerList
						.get(i);
				LiveMessageHandler act = w.get();
				if (act != null) {
					liveMessageHandlerList.remove(i);
				}
			}
		}
	}
	
	
	public void onLiveStatusHandlerCreated(LiveStatusHandler lsh) {
		liveStatusHandlerList.add(new WeakReference<LiveStatusHandler>(lsh));
	}

	public void onLiveStatusHandlerDestroyed(LiveStatusHandler lsh) {
		synchronized (liveStatusHandlerList) {
			int size = liveStatusHandlerList.size();

			for (int i = 0; i < size; i++) {
				WeakReference<LiveStatusHandler> w = liveStatusHandlerList
						.get(i);
				LiveStatusHandler act = w.get();
				if (act != null) {
					liveStatusHandlerList.remove(i);
				}
			}
		}
	}

	public List<LiveStatusHandler> getLiveStatusHandler() {
		synchronized (liveStatusHandlerList) {
			int size = liveStatusHandlerList.size();
			List<LiveStatusHandler> handlers = new ArrayList<LiveStatusHandler>(
					size);
			for (int i = 0; i < size; i++) {
				WeakReference<LiveStatusHandler> w = liveStatusHandlerList
						.get(i);
				LiveStatusHandler act = w.get();
				if (act != null) {
					handlers.add(act);
				}
			}
			return handlers;
		}
	}
	
	
	
	public List<LiveMessageHandler> getLiveMessageHandler() {
		synchronized (liveMessageHandlerList) {
			int size = liveMessageHandlerList.size();
			List<LiveMessageHandler> handlers = new ArrayList<LiveMessageHandler>(
					size);
			for (int i = 0; i < size; i++) {
				WeakReference<LiveMessageHandler> w = liveMessageHandlerList
						.get(i);
				LiveMessageHandler act = w.get();
				if (act != null) {
					handlers.add(act);
				}
			}
			return handlers;
		}
	}
	
	
	
	
	public void onLiveWathcingHandlerCreated(LiveWathcingHandler lsh) {
		liveWatchingHandlerList.add(new WeakReference<LiveWathcingHandler>(lsh));
	}

	public void onLiveWathcingHandlerDestroyed(LiveWathcingHandler lsh) {
		synchronized (liveWatchingHandlerList) {
			int size = liveWatchingHandlerList.size();

			for (int i = 0; i < size; i++) {
				WeakReference<LiveWathcingHandler> w = liveWatchingHandlerList
						.get(i);
				LiveWathcingHandler act = w.get();
				if (act != null) {
					liveWatchingHandlerList.remove(i);
				}
			}
		}
	}
	
	
	public List<LiveWathcingHandler> getLiveWathcingHandler() {
		synchronized (liveWatchingHandlerList) {
			int size = liveWatchingHandlerList.size();
			List<LiveWathcingHandler> handlers = new ArrayList<LiveWathcingHandler>(
					size);
			for (int i = 0; i < size; i++) {
				WeakReference<LiveWathcingHandler> w = liveWatchingHandlerList
						.get(i);
				LiveWathcingHandler act = w.get();
				if (act != null) {
					handlers.add(act);
				}
			}
			return handlers;
		}
	}


}
