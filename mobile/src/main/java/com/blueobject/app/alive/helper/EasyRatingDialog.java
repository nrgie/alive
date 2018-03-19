package com.blueobject.app.alive.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.blueobject.app.alive.R;

import java.util.Date;

public class EasyRatingDialog {
    public interface ConditionTrigger {
        boolean shouldShow();
    }

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private ConditionTrigger mCondition;
    private Dialog mDialog;

    private static final String PREFS_NAME = "erd_rating";
    private static final String KEY_WAS_RATED = "KEY_WAS_RATED";
    private static final String KEY_NEVER_REMINDER = "KEY_NEVER_REMINDER";
    private static final String KEY_FIRST_HIT_DATE = "KEY_FIRST_HIT_DATE";
    private static final String KEY_LAUNCH_TIMES = "KEY_LAUNCH_TIMES";

    public EasyRatingDialog(Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void onStart() {
        if (didRate() || didNeverReminder()) return;

        int launchTimes = mPreferences.getInt(KEY_LAUNCH_TIMES, 0);
        long firstDate = mPreferences.getLong(KEY_FIRST_HIT_DATE, -1L);

        if (firstDate == -1L) {
            registerDate();
        }

        registerHitCount(++launchTimes);
    }

    public void showAnyway() {
        tryShow(mContext);
    }

    public void showIfNeeded() {
        if (mCondition != null) {
            if (mCondition.shouldShow())
                tryShow(mContext);
        } else {
            if (shouldShow())
                tryShow(mContext);
        }
    }

    public void neverReminder() {
        mPreferences.edit().putBoolean(KEY_NEVER_REMINDER, true).apply();
    }

    public void rateNow() {
        String appPackage = mContext.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        mPreferences.edit().putBoolean(KEY_WAS_RATED, true).apply();
    }

    public void remindMeLater() {
        registerHitCount(0);
        registerDate();
    }

    public boolean isShowing() {
        return mDialog != null && mDialog.isShowing();
    }

    public boolean didRate() {
        return mPreferences.getBoolean(KEY_WAS_RATED, false);
    }

    public boolean didNeverReminder() {
        return mPreferences.getBoolean(KEY_NEVER_REMINDER, false);
    }

    public void setConditionTrigger(ConditionTrigger condition) {
        mCondition = condition;
    }

    public void setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
    }

    private void tryShow(Context context) {
        if (isShowing())
            return;

        try {
            mDialog = null;
            mDialog = createDialog(context);
            mDialog.show();
        } catch (Exception e) {
            //It prevents many Android exceptions
            //when user interactions conflicts with UI thread or Activity expired window token
            //BadTokenException, IllegalStateException ...
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
    }

    private boolean shouldShow() {
        if (mPreferences.getBoolean(KEY_NEVER_REMINDER, false))
            return false;
        if (mPreferences.getBoolean(KEY_WAS_RATED, false))
            return false;

        int launchTimes = mPreferences.getInt(KEY_LAUNCH_TIMES, 0);
        long firstDate = mPreferences.getLong(KEY_FIRST_HIT_DATE, 0L);
        long today = new Date().getTime();
        int maxLaunchTimes = mContext.getResources().getInteger(R.integer.erd_launch_times);
        int maxDaysAfter = mContext.getResources().getInteger(R.integer.erd_max_days_after);

        return daysBetween(firstDate, today) > maxDaysAfter || launchTimes > maxLaunchTimes;
    }

    private void registerHitCount(int hitCount) {
        mPreferences
                .edit()
                .putInt(KEY_LAUNCH_TIMES, Math.min(hitCount, Integer.MAX_VALUE))
                .apply();
    }

    private void registerDate() {
        Date today = new Date();
        mPreferences
                .edit()
                .putLong(KEY_FIRST_HIT_DATE, today.getTime())
                .apply();
    }

    private long daysBetween(long firstDate, long lastDate) {
        return (lastDate - firstDate) / (1000 * 60 * 60 * 24);
    }

    private android.app.Dialog createDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.erd_title)
                .setMessage(R.string.erd_message)
                .setNegativeButton(R.string.erd_no_thanks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        neverReminder();
                    }
                })
                .setNeutralButton(R.string.erd_remind_me_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        remindMeLater();
                    }
                })
                .setPositiveButton(R.string.erd_rate_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rateNow();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        remindMeLater();
                    }
                }).create();
    }
}
