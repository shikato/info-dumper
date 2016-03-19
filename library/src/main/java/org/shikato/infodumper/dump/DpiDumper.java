package org.shikato.infodumper.dump;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.stetho.dumpapp.DumpException;

import java.util.LinkedHashMap;
import java.util.List;

public class DpiDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "DPI";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        DisplayMetrics metrics = getDisplayMetrics(context);
        int dpi = getDpi(metrics);
        String dpiType = getDpiType(dpi);

        LinkedHashMap<String, String> dumps = new LinkedHashMap<>();
        dumps.put("dpi", Integer.toString(dpi));
        dumps.put("Generalized density", dpiType);
        dumps.put("widthPixels", Integer.toString(metrics.widthPixels));
        dumps.put("heightPixels", Integer.toString(metrics.heightPixels));

        return dumps;
    }

    @Override
    public List<String> getDumpList(Context context) throws DumpException {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    private DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    private int getDpi(DisplayMetrics metrics) {
        return metrics.densityDpi;
    }

    private String getDpiType(int dpi) {
        if (dpi <= 120) {
            return "ldpi";
        } else if (dpi <= 160) {
            return "mdpi";
        } else if (dpi <= 240) {
            return "hdpi";
        } else if (dpi <= 320) {
            return "xhdpi";
        } else if (dpi <= 480) {
            return "xxhdpi";
        } else {
            return "xxxhdpi";
        }
    }
}
