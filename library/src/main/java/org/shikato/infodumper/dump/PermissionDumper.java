package org.shikato.infodumper.dump;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.facebook.stetho.dumpapp.DumpException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PermissionDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "REQUESTED PERMISSIONS";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        return null;
    }

    @Override
    public List<String> getDumpList(Context context) throws DumpException {
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo =
                    packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);

            if (packageInfo == null || packageInfo.requestedPermissions == null) {
                return null;
            }

            List<String> dumps = new ArrayList<>();
            for (String permission : packageInfo.requestedPermissions) {
                dumps.add(permission);
            }

            return dumps;
        } catch (PackageManager.NameNotFoundException e) {
            throw new DumpException(e.getMessage());
        }
    }

    @Override
    public String getErrorMessage() {
        return "No requested permission.";
    }
}
