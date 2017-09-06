package com.hcw2175.library.qrscan.listener;
import android.app.Activity;
import android.content.DialogInterface;

/**
 * Activity结束监听器
 *
 * @author hucw
 */
public final class ActivityFinishListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

  private final Activity activity;

  public ActivityFinishListener(Activity activity) {
    this.activity = activity;
  }

  public void onCancel(DialogInterface dialogInterface) {
    run();
  }

  public void onClick(DialogInterface dialogInterface, int i) {
    run();
  }

  public void run() {
    activity.finish();
  }
}
