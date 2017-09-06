package com.hcw2175.library.qrscan.listener;

import android.app.Activity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Activity生命周期任务延时执行器
 *
 * @author hucw
 */
public final class ActivityLiveTaskTimer {

	private static final int INACTIVITY_DELAY_SECONDS = 5 * 60;

	private final ScheduledExecutorService activityLiveTaskTimer;
	private final Activity activity;
	private ScheduledFuture<?> inactivityFuture = null;

	public ActivityLiveTaskTimer(Activity activity) {
        // 单例任务执行器
        activityLiveTaskTimer = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        this.activity = activity;
		init();
	}

	public void init() {
		cancel();
        inactivityFuture = activityLiveTaskTimer.schedule(new ActivityFinishListener(activity), INACTIVITY_DELAY_SECONDS, TimeUnit.SECONDS);
	}

	private void cancel() {
		if (inactivityFuture != null) {
			inactivityFuture.cancel(true);
			inactivityFuture = null;
		}
	}

	public void shutdown() {
		cancel();
		activityLiveTaskTimer.shutdown();
	}

	private static final class DaemonThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		}
	}

}
