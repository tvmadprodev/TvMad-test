package com.tvmad.hdmitest;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

public class OverlayService extends Service {
    private static final String TAG = "TvMadHdmiTest";
    private WindowManager wm;
    private View overlay;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (overlay == null) showOverlay();
        return START_STICKY;
    }

    private void showOverlay() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        TextView logo = new TextView(this);
        logo.setText("TvMad Galaxy\nHDMI connected");
        logo.setTextColor(Color.rgb(100, 210, 255));
        logo.setTextSize(30f);
        logo.setGravity(Gravity.CENTER);
        root.addView(logo, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.OPAQUE);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.setTitle("TvMad-HDMI-SkipScreenshot-Test");

        overlay = root;
        wm.addView(overlay, lp);

        overlay.postDelayed(this::applySkipScreenshot, 700);
    }

    private void applySkipScreenshot() {
        try {
            Method getViewRootImpl = View.class.getDeclaredMethod("getViewRootImpl");
            getViewRootImpl.setAccessible(true);
            Object viewRoot = getViewRootImpl.invoke(overlay);
            if (viewRoot == null) throw new IllegalStateException("ViewRootImpl == null");

            Method getSurfaceControl = viewRoot.getClass().getDeclaredMethod("getSurfaceControl");
            getSurfaceControl.setAccessible(true);
            Object surfaceControl = getSurfaceControl.invoke(viewRoot);
            if (surfaceControl == null) throw new IllegalStateException("SurfaceControl == null");

            Class<?> scClass = Class.forName("android.view.SurfaceControl");
            Class<?> txClass = Class.forName("android.view.SurfaceControl$Transaction");
            Object tx = txClass.getDeclaredConstructor().newInstance();

            Method setSkip = txClass.getDeclaredMethod("setSkipScreenshot", scClass, boolean.class);
            setSkip.setAccessible(true);
            setSkip.invoke(tx, surfaceControl, true);

            Method apply = txClass.getDeclaredMethod("apply");
            apply.setAccessible(true);
            apply.invoke(tx);

            Log.i(TAG, "SUCCESS: SKIP_SCREENSHOT applied to overlay SurfaceControl");
        } catch (Throwable t) {
            Log.e(TAG, "FAILED to apply SKIP_SCREENSHOT", t);
        }
    }

    @Override
    public void onDestroy() {
        if (wm != null && overlay != null) {
            try { wm.removeView(overlay); } catch (Throwable ignored) {}
        }
        overlay = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
