package org.shikato.infodumper.dump;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.facebook.stetho.dumpapp.DumpException;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class IdsDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "ID";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        LinkedHashMap<String, String> dumps = new LinkedHashMap<>();

        dumps.put(Settings.Secure.ANDROID_ID, getAndroidId(context));
        dumps.put("UUID", getUUID());

        String adIdKey = "AdvertisingId";
        String adOptoutKey = "isAdOptout";
        AdvertisingIdClient.Info adInfo = getAdInfo(context);
        if (adInfo == null) {
            dumps.put(adIdKey, "Getting AdvertisingId need a android.permission.INTERNET");
            dumps.put(adOptoutKey, "Getting AdvertisingId need a android.permission.INTERNET");
        } else {
            dumps.put(adIdKey, adInfo.getId());
            dumps.put(adOptoutKey, Boolean.toString(adInfo.isLimitAdTrackingEnabled()));
        }

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

    private String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private AdvertisingIdClient.Info getAdInfo(Context context) throws DumpException {
        if (context.getPackageManager().checkPermission(Manifest.permission.INTERNET, context.getPackageName()) ==
                PackageManager.PERMISSION_GRANTED) {
            try {
                return AdvertisingIdClient.getAdvertisingIdInfo(context);
            } catch (IOException e) {
                throw new DumpException(e.getMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                throw new DumpException(e.getMessage());
            } catch (GooglePlayServicesRepairableException e) {
                throw new DumpException(e.getMessage());
            }
        } else {
            return null;
        }
    }
}
