package org.shikato.infodumper.dump;

import android.app.ActivityManager;
import android.content.Context;

import com.facebook.stetho.dumpapp.DumpException;

import java.util.LinkedHashMap;
import java.util.List;

public class MemoryDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "MEMORY";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        ActivityManager activityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        LinkedHashMap<String, String> dumps = new LinkedHashMap<>();

        dumps.put("MemoryInfo availMem", Long.toString(memoryInfo.availMem));
        dumps.put("MemoryInfo lowMemory", Boolean.toString(memoryInfo.lowMemory));
        dumps.put("MemoryInfo threshold", Long.toString(memoryInfo.threshold));

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
}
