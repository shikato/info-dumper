package org.shikato.infodumper.dump;

import android.app.ActivityManager;
import android.content.Context;

import com.facebook.stetho.dumpapp.DumpException;

import java.util.LinkedHashMap;
import java.util.List;

public class ErrorDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "ERROR";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        ActivityManager activityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.ProcessErrorStateInfo> errorStateInfo = activityManager.getProcessesInErrorState();

        if (errorStateInfo == null) return null;

        LinkedHashMap<String, String> dumps = new LinkedHashMap<>();

        for (ActivityManager.ProcessErrorStateInfo error : errorStateInfo) {
            dumps.put("Error.condition", Integer.toString(error.condition)); // CRASHED,NOT_RESPONDING,NO_ERROR
            dumps.put("Error.longMsg", error.longMsg);
            dumps.put("Error.shortMsg", error.shortMsg);
            dumps.put("Error.pid", Integer.toString(error.pid));
            dumps.put("Error.processName", error.processName);
            dumps.put("Error.stackTrace", error.stackTrace);
            dumps.put("Error.tag", error.tag);
            dumps.put("Error.uid", Integer.toString(error.uid));
        }

        return dumps;
    }

    @Override
    public List<String> getDumpList(Context context) throws DumpException {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return "No Error";
    }
}
