package org.shikato.infodumper.dump;

import android.content.Context;

import com.facebook.stetho.dumpapp.DumpException;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

public class BuildConfigDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "BUILD CONFIG";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        Class buildConfig;
        try {
            buildConfig = Class.forName(context.getPackageName() + ".BuildConfig");

            LinkedHashMap<String, String> dumps = new LinkedHashMap<>();

            for (Field field : buildConfig.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(null);
                if (value != null) {
                    dumps.put(field.getName(), value.toString());
                } else {
                    dumps.put(field.getName(), null);
                }
            }

            return dumps;
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
