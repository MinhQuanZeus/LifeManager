package zeus.minhquan.lifemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import zeus.minhquan.lifemanager.BuildConfig;
import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.LifeManagerApplication;

/**
 * Created by QuanT on 4/22/2017.
 */

public class GeneralUtils {
    public static void enableLinks(TextView view) {
        if (view != null) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
            GeneralUtils.stripUnderlines(view);
        }
    }

    public static Uri defaultRingtone() {
        return Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.default_ringtone);
    }

    public static void stripUnderlines(TextView textView) {
        Spannable s = (Spannable)textView.getText();
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }


    // http://stackoverflow.com/questions/4096851/remove-underline-from-links-in-textview-android
    private static class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    public static void setLockScreenFlags(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    public static void clearLockScreenFlags(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    public static void showFragment(FragmentManager fragmentManager, Fragment fragment,
                                    String fragmentTag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, fragmentTag);
        transaction.commit();
    }

    public static void showFragmentFromRight(FragmentManager fragmentManager, Fragment fragment,
                                             String fragmentTag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.fragment_container, fragment, fragmentTag);
        transaction.commit();
    }

    public static void showFragmentFromLeft(FragmentManager fragmentManager, Fragment fragment,
                                            String fragmentTag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fragment_container, fragment, fragmentTag);
        transaction.commit();
    }

    public static int getDurationSetting(int settingKeyResId,
                                         int defaultSettingStringResId,
                                         int defaultDuration) {
        Context context = LifeManagerApplication.getAppContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String durationPreference = preferences.getString(
                context.getResources().getString(settingKeyResId),
                context.getResources().getString(defaultSettingStringResId));

        int duration = defaultDuration;
        try {
            duration = Integer.parseInt(durationPreference);
        } catch (NumberFormatException e) {
            Logger.trackException(e);
        }

        return duration;
    }

    @SuppressWarnings("deprecation")
    public static boolean deviceHasFrontFacingCamera() {
        return hasDeviceCameraWithDirection(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @SuppressWarnings("deprecation")
    public static boolean deviceHasRearFacingCamera() {
        return hasDeviceCameraWithDirection(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @SuppressWarnings("deprecation")
    private static boolean hasDeviceCameraWithDirection(int cameraDirection) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {

            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraDirection) {
                return true;
            }
        }
        return false;
    }
}
