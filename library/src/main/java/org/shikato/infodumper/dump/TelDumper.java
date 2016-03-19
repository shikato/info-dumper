package org.shikato.infodumper.dump;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.facebook.stetho.dumpapp.DumpException;

import java.util.LinkedHashMap;
import java.util.List;

public class TelDumper implements InfoDumper {
    @Override
    public String getTitle() {
        return "TEL";
    }

    @Override
    public LinkedHashMap<String, String> getDumpMap(Context context) throws DumpException {
        int permissionInfo =
                context.getPackageManager().checkPermission(Manifest.permission.READ_PHONE_STATE, context.getPackageName());

        if (permissionInfo == PackageManager.PERMISSION_DENIED) {
            return null;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        LinkedHashMap<String, String> dumps = new LinkedHashMap<>();

        dumps.put("Line1Number", telephonyManager.getLine1Number());
        dumps.put("DeviceId", telephonyManager.getDeviceId());
        dumps.put("SimCountryIso", telephonyManager.getSimCountryIso());
        dumps.put("SimOperator", telephonyManager.getSimOperator());
        dumps.put("SimOperatorName", telephonyManager.getSimOperatorName());
        dumps.put("SimSerialNumber", telephonyManager.getSimSerialNumber());
        dumps.put("SimState", Integer.toString(telephonyManager.getSimState()));
        dumps.put("VoiceMailNumber", telephonyManager.getVoiceMailNumber());

        return dumps;
    }

    @Override
    public List<String> getDumpList(Context context) throws DumpException {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return "Need a permission: android.permission.READ_PHONE_STATE";
    }
}
