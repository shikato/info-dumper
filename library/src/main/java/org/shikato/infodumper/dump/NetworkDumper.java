package org.shikato.infodumper.dump;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.facebook.stetho.dumpapp.DumpException;

import java.util.LinkedHashMap;
import java.util.List;

public class NetworkDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "NETWORK";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        int permissionInfo =
                context.getPackageManager().checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, context.getPackageName());

        if (permissionInfo == PackageManager.PERMISSION_DENIED) {
            return null;
        }

        ConnectivityManager connectivity =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivity.getActiveNetworkInfo();

        LinkedHashMap<String, String> dumps = new LinkedHashMap<>();

        if (network == null) {
            dumps.put("NetworkInfo", "NetworkInfo is null.");
        } else if (!network.isAvailable()) {
            dumps.put("NetworkInfo", "connection not available.");
        } else if (!network.isConnectedOrConnecting()) {
            dumps.put("NetworkInfo", "not connect.");
        } else {
            dumps.put("NetworkInfo", network.getTypeName());
        }

        return dumps;
    }

    @Override
    public List<String> getDumpList(Context context) throws DumpException {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return "Need a permission: android.permission.ACCESS_NETWORK_STATE";
    }
}
