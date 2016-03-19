package org.shikato.infodumper.dump;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.facebook.stetho.dumpapp.DumpException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class LastUpdateDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "LAST UPDATE";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        return null;
    }

    @Override
    public List<String> getDumpList(Context context) throws DumpException {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            List<String> dumps = new ArrayList<>();
            dumps.add(new Date(packageInfo.lastUpdateTime).toString());
            return dumps;
        } catch (PackageManager.NameNotFoundException e) {
            throw new DumpException(e.getMessage());
        }
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
