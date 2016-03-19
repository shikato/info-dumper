package org.shikato.infodumper.dump;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.facebook.stetho.dumpapp.DumpException;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

public class ApplicationInfoDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "APPLICATION INFO";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            Class applicationInfo = Class.forName(appInfo.getClass().getName());

            LinkedHashMap<String, String> dumps = new LinkedHashMap<>();

            for (Field field : applicationInfo.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(appInfo);
                if (value != null) {
                    dumps.put(field.getName(), value.toString());
                } else {
                    dumps.put(field.getName(), null);
                }
            }

            return dumps;
        } catch (PackageManager.NameNotFoundException e) {
            throw new DumpException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new DumpException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new DumpException(e.getMessage());
        }
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
