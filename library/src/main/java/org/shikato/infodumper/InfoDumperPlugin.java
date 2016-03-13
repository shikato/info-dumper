package org.shikato.infodumper;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.stetho.dumpapp.ArgsHelper;
import com.facebook.stetho.dumpapp.DumpException;
import com.facebook.stetho.dumpapp.DumpUsageException;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class InfoDumperPlugin implements DumperPlugin {

    private static final String NAME = "info";

    private static final String CMD_ALL = "all";
    private static final String CMD_BUILD_CONFIG = "buildconf";
    private static final String CMD_ID = "id";
    private static final String CMD_DPI = "dpi";
    private static final String CMD_MEMORY = "memory";
    private static final String CMD_NETWORK = "network";
    private static final String CMD_ERROR = "error";
    private static final String CMD_TEL = "tel";

    // PackageInfo系
    private static final String CMD_PERMISSION = "permission";
    private static final String CMD_LAST_UPDATE = "lastupdate";

    // AppInfo系
    // TODO: 細分化する
    private static final String CMD_APPLICATION_INFO = "appinfo";
    // OsBuild系
    // TODO: 細分化する
    private static final String CMD_OS_BUILD = "osbuild";

    private Context mContext = null;
    private boolean mIsAll = false;

    public InfoDumperPlugin(Context context) {
        mContext = context.getApplicationContext();
    }

    // getNameで返す値がdumpappp実行時のコマンドとなる
    // ex.) appdump {{NAME}}
    @Override
    public String getName() {
        return NAME;
    }

    // getNameで指定したコマンドを実行したときに呼ばれるメソッド
    @Override
    public void dump(DumperContext dumperContext) throws DumpException {
        Iterator<String> argsIter = dumperContext.getArgsAsList().iterator();

        String command = ArgsHelper.nextOptionalArg(argsIter, null);
        mIsAll = false;

        if (CMD_BUILD_CONFIG.equalsIgnoreCase(command)) {
            dumpBuildConfig(dumperContext);
        } else if (CMD_PERMISSION.equalsIgnoreCase(command)) {
            dumpPermission(dumperContext);
        } else if (CMD_LAST_UPDATE.equalsIgnoreCase(command)) {
            dumpLastUpdate(dumperContext);
        } else if (CMD_APPLICATION_INFO.equalsIgnoreCase(command)) {
            dumpApplicationInfo(dumperContext);
        } else if (CMD_ID.equalsIgnoreCase(command)) {
            dumpIds(dumperContext);
        } else if (CMD_OS_BUILD.equalsIgnoreCase(command)) {
            dumpOsBuild(dumperContext);
        } else if (CMD_DPI.equalsIgnoreCase(command)) {
            dumpDpi(dumperContext);
        } else if (CMD_MEMORY.equalsIgnoreCase(command)) {
            dumpMemory(dumperContext);
        } else if (CMD_ERROR.equalsIgnoreCase(command)) {
            dumpError(dumperContext);
        } else if (CMD_NETWORK.equalsIgnoreCase(command)) {
            dumpNetwork(dumperContext);
        } else if (CMD_TEL.equalsIgnoreCase(command)) {
            dumpTel(dumperContext);
        } else if (CMD_ALL.equalsIgnoreCase(command)) {
            mIsAll = true;
            dumpBuildConfig(dumperContext);
            dumpPermission(dumperContext);
            dumpLastUpdate(dumperContext);
            dumpApplicationInfo(dumperContext);
            dumpIds(dumperContext);
            dumpOsBuild(dumperContext);
            dumpDpi(dumperContext);
            dumpMemory(dumperContext);
            dumpError(dumperContext);
            dumpNetwork(dumperContext);
            dumpTel(dumperContext);
        } else {
            usage(dumperContext);
            if (command != null) {
                throw new DumpUsageException("Unknown command: " + command);
            }
        }
    }

    private void dumpBuildConfig(DumperContext dumperContext) throws DumpException {
        PrintStream writer = dumperContext.getStdout();

        try {
            Class buildConfig = Class.forName(mContext.getPackageName() + ".BuildConfig");

            if (mIsAll) {
                writer.println("[BUILD CONFIG]");
            }

            for (Field field : buildConfig.getDeclaredFields()) {
                field.setAccessible(true);
                writer.println(field.getName() + ": " + field.get(null));
            }

            if (mIsAll) {
                writer.println("");
            }
        } catch (ClassNotFoundException e) {
            throw new DumpException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new DumpException(e.getMessage());
        }
    }

    private void dumpPermission(DumperContext dumperContext) throws DumpException {
        PrintStream writer = dumperContext.getStdout();
        PackageManager packageManager = mContext.getPackageManager();

        try {
            PackageInfo packageInfo =
                    packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_PERMISSIONS);

            if (mIsAll) {
                writer.println("[REQUESTED PERMISSIONS]");
            }

            if (packageInfo == null || packageInfo.requestedPermissions == null) {
                writer.println("No requested permission."); 
                if (mIsAll) {
                    writer.println("");
                } 
                return;
            }

            for (String permission : packageInfo.requestedPermissions) {
                writer.println(permission);
            }

            if (mIsAll) {
                writer.println("");
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new DumpException(e.getMessage());
        }
    }

    private void dumpLastUpdate(DumperContext dumperContext) throws DumpException {
        PrintStream writer = dumperContext.getStdout();
        PackageManager packageManager = mContext.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);

            if (mIsAll) {
                writer.println("[LAST UPDATE]");
            }

            writer.println(new Date(packageInfo.lastUpdateTime));

            if (mIsAll) {
                writer.println("");
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new DumpException(e.getMessage());
        }
    }

    private void dumpApplicationInfo(DumperContext dumperContext) throws DumpException {
        PrintStream writer = dumperContext.getStdout();
        PackageManager packageManager = mContext.getPackageManager();

        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(mContext.getPackageName(), 0);
            Class applicationInfo = Class.forName(appInfo.getClass().getName());

            if (mIsAll) {
                writer.println("[APPLICATION INFO]");
            }

            for (Field field : applicationInfo.getDeclaredFields()) {
                field.setAccessible(true);
                writer.println(field.getName() + ": " + field.get(appInfo));
            }

            if (mIsAll) {
                writer.println("");
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new DumpException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new DumpException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new DumpException(e.getMessage());
        }
    }

    private void dumpOsBuild(DumperContext dumperContext) throws DumpException {
        PrintStream writer = dumperContext.getStdout();

        try {
            Class osBuild = Class.forName("android.os.Build");

            if (mIsAll) {
                writer.println("[OS BUILD]");
            }

            for (Field field : osBuild.getDeclaredFields()) {
                field.setAccessible(true);
                writer.println(field.getName() + ": " + field.get(null));
            }

            if (mIsAll) {
                writer.println("");
            }
        } catch (ClassNotFoundException e) {
            throw new DumpException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new DumpException(e.getMessage());
        }
    }

    private void dumpIds(DumperContext dumperContext) throws DumpException {
        PrintStream writer = dumperContext.getStdout();

        String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null) {
            androidId = "null";
        }
        String uuid = UUID.randomUUID().toString();
        String adId = null;
        boolean isAdOptout = false;
        boolean isGrantedNetworkPermission = false;

        int permissionInfo =
                mContext.getPackageManager().checkPermission(Manifest.permission.INTERNET, mContext.getPackageName());

        if(permissionInfo == PackageManager.PERMISSION_GRANTED) {
            isGrantedNetworkPermission = true;
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
                adId = adInfo.getId();
                isAdOptout = adInfo.isLimitAdTrackingEnabled();
            } catch (IOException e) {
                throw new DumpException(e.getMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                throw new DumpException(e.getMessage());
            } catch (GooglePlayServicesRepairableException e) {
                throw new DumpException(e.getMessage());
            }
        }

        if (mIsAll) {
            writer.println("[ID]");
        }

        writer.println(Settings.Secure.ANDROID_ID + ": " + androidId);
        writer.println("UUID: " + uuid);

        if (isGrantedNetworkPermission) {
            if (!TextUtils.isEmpty(adId)) {
                writer.println("AdvertisingId: " + adId);
                writer.println("isAdOptout: " + Boolean.toString(isAdOptout));
            }
        } else {
            writer.println("Getting AdvertisingId need a android.permission.INTERNET");
        }

        if (mIsAll) {
            writer.println("");
        }
    }

    private void dumpDpi(DumperContext dumperContext) throws DumpException {
        PrintStream writer = dumperContext.getStdout();

        WindowManager wm = (WindowManager)mContext.getSystemService( Context.WINDOW_SERVICE );
        android.view.Display disp = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        disp.getMetrics(metrics);
        int dpi = metrics.densityDpi;

        String result = "";
        if (dpi <= 120 ) {
            result = "ldpi";
        } else if (dpi <= 160) {
            result = "mdpi";
        } else if (dpi <= 240) {
            result = "hdpi";
        } else if (dpi <= 320) {
            result = "xhdpi";
        } else if (dpi <= 480) {
            result = "xxhdpi";
        } else {
            result = "xxxhdpi";
        }

        if (mIsAll) {
            writer.println("[DPI]");
        }

        writer.println("dpi: " + Integer.toString(dpi));
        writer.println("Generalized density: " + result);
        writer.println("widthPixels: " + Integer.toString(metrics.widthPixels));
        writer.println("heightPixels: " + Integer.toString(metrics.heightPixels));

        if (mIsAll) {
            writer.println("");
        }
    }

    private void dumpMemory(DumperContext dumperContext) {
        PrintStream writer = dumperContext.getStdout();

        ActivityManager activityManager = ((ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE));
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        if (mIsAll) {
            writer.println("[MEMORY]");
        }

        writer.println("MemoryInfo availMem: " + memoryInfo.availMem);
        writer.println("MemoryInfo lowMemory: " + memoryInfo.lowMemory);
        writer.println("MemoryInfo threshold: " + memoryInfo.threshold);

        if (mIsAll) {
            writer.println("");
        }
    }

    private void dumpError(DumperContext dumperContext) {
        PrintStream writer = dumperContext.getStdout();

        ActivityManager activityManager = ((ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.ProcessErrorStateInfo> errorStateInfo = activityManager.getProcessesInErrorState();

        if (mIsAll) {
            writer.println("[ERROR]");
        }

        if (errorStateInfo == null) {
            writer.println("No Error");
            if (mIsAll) {
                writer.println("");
            }
            return;
        }

        for (ActivityManager.ProcessErrorStateInfo error : errorStateInfo) {
            writer.println("Error.condition: " + error.condition); // CRASHED,NOT_RESPONDING,NO_ERROR
            writer.println("Error.longMsg: " + error.longMsg);
            writer.println("Error.shortMsg: " + error.shortMsg);
            writer.println("Error.pid: " + error.pid);
            writer.println("Error.processName: " + error.processName);
            writer.println("Error.stackTrace : " + error.stackTrace);
            writer.println("Error.tag: " + error.tag);
            writer.println("Error.uid: " + error.uid);
        }

        if (mIsAll) {
            writer.println("");
        }
    }

    private void dumpNetwork(DumperContext dumperContext) {
        PrintStream writer = dumperContext.getStdout();

        if (mIsAll) {
            writer.println("[NETWORK]");
        }

        int permissionInfo =
                mContext.getPackageManager().checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, mContext.getPackageName());

        if(permissionInfo == PackageManager.PERMISSION_DENIED) {
            writer.println("Need a permission: android.permission.ACCESS_NETWORK_STATE");
            if (mIsAll) {
                writer.println("");
            }
            return;
        }

        ConnectivityManager connectivity =
                (ConnectivityManager)mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivity.getActiveNetworkInfo();

        if (network == null) {
            writer.println("NetworkInfo: connection disactive");
        } else if (!network.isAvailable()) {
            writer.println("NetworkInfo: connection not available");
        } else if (!network.isConnectedOrConnecting()) {
            writer.println("NetworkInfo: not connect.");
        } else {
            writer.println("NetworkInfo: " + network.getTypeName());
        }

        if (mIsAll) {
            writer.println("");
        }
    }

    private void dumpTel(DumperContext dumperContext) {
        PrintStream writer = dumperContext.getStdout();

        if (mIsAll) {
            writer.println("[TEL]");
        }

        int permissionInfo =
                mContext.getPackageManager().checkPermission(Manifest.permission.READ_PHONE_STATE, mContext.getPackageName());

        if(permissionInfo == PackageManager.PERMISSION_DENIED) {
            writer.println("Need a permission: android.permission.READ_PHONE_STATE");
            if (mIsAll) {
                writer.println("");
            }
            return;
        }

        TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);

        writer.println("Line1Number: " + telephonyManager.getLine1Number());
        writer.println("DeviceId: " + telephonyManager.getDeviceId());
        writer.println("SimCountryIso: " + telephonyManager.getSimCountryIso());
        writer.println("SimOperator: " + telephonyManager.getSimOperator());
        writer.println("SimOperatorName: " + telephonyManager.getSimOperatorName());
        writer.println("SimSerialNumber: " + telephonyManager.getSimSerialNumber());
        writer.println("SimState: " + telephonyManager.getSimState());
        writer.println("VoiceMailNumber: " + telephonyManager.getVoiceMailNumber());

        if (mIsAll) {
            writer.println("");
        }
    }

    private void usage(DumperContext dumperContext) {
        PrintStream writer = dumperContext.getStdout();
        final String cmdName = "dumpapp " + NAME;
        final String usagePrefix = "Usage: " + cmdName + " ";

        writer.println(usagePrefix + "<command>");
        writer.print(usagePrefix + CMD_BUILD_CONFIG);
        writer.println();
        writer.print(usagePrefix + CMD_ID);
        writer.println();
        writer.print(usagePrefix + CMD_DPI);
        writer.println();
        writer.print(usagePrefix + CMD_MEMORY);
        writer.println();
        writer.print(usagePrefix + CMD_PERMISSION);
        writer.println();
        writer.print(usagePrefix + CMD_LAST_UPDATE);
        writer.println();
        writer.print(usagePrefix + CMD_APPLICATION_INFO);
        writer.println();
        writer.print(usagePrefix + CMD_OS_BUILD);
        writer.println();
        writer.print(usagePrefix + CMD_NETWORK);
        writer.println();
        writer.print(usagePrefix + CMD_ERROR);
        writer.println();
        writer.print(usagePrefix + CMD_TEL);
        writer.println();
        writer.print(usagePrefix + CMD_ALL);
        writer.println();
    }
}
