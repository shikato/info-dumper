package org.shikato.infodumper.dump;

import android.content.Context;

import com.facebook.stetho.dumpapp.DumpException;

import java.util.LinkedHashMap;
import java.util.List;

public interface InfoDumper {
    String getTitle();
    LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException;
    List<String> getDumpList(Context context) throws DumpException;
    String getErrorMessage();
}
