package org.shikato.infodumper;

import android.content.Context;

import com.facebook.stetho.dumpapp.ArgsHelper;
import com.facebook.stetho.dumpapp.DumpException;
import com.facebook.stetho.dumpapp.DumpUsageException;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;

import org.shikato.infodumper.dump.ApplicationInfoDumper;
import org.shikato.infodumper.dump.BuildConfigDumper;
import org.shikato.infodumper.dump.DpiDumper;
import org.shikato.infodumper.dump.ErrorDumper;
import org.shikato.infodumper.dump.IdsDumper;
import org.shikato.infodumper.dump.InfoDumper;
import org.shikato.infodumper.dump.LastUpdateDumper;
import org.shikato.infodumper.dump.MemoryDumper;
import org.shikato.infodumper.dump.NetworkDumper;
import org.shikato.infodumper.dump.OsBuildDumper;
import org.shikato.infodumper.dump.PermissionDumper;
import org.shikato.infodumper.dump.TelDumper;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private static final String CMD_PERMISSION = "permission";
    private static final String CMD_LAST_UPDATE = "lastupdate";
    private static final String CMD_APPLICATION_INFO = "appinfo";
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

    // getNameで指定したコマンドを実行したときに呼ばれる
    @Override
    public void dump(DumperContext dumperContext) throws DumpException {
        Iterator<String> argsIterator = dumperContext.getArgsAsList().iterator();

        String command = ArgsHelper.nextOptionalArg(argsIterator, null);

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
        dumpTypeMap(dumperContext, new BuildConfigDumper());
    }

    private void dumpPermission(DumperContext dumperContext) throws DumpException {
        dumpTypeList(dumperContext, new PermissionDumper());
    }

    private void dumpLastUpdate(DumperContext dumperContext) throws DumpException {
        dumpTypeList(dumperContext, new LastUpdateDumper());
    }

    private void dumpApplicationInfo(DumperContext dumperContext) throws DumpException {
        dumpTypeMap(dumperContext, new ApplicationInfoDumper());
    }

    private void dumpOsBuild(DumperContext dumperContext) throws DumpException {
        dumpTypeMap(dumperContext, new OsBuildDumper());
    }

    private void dumpIds(DumperContext dumperContext) throws DumpException {
        dumpTypeMap(dumperContext, new IdsDumper());
    }

    private void dumpDpi(DumperContext dumperContext) throws DumpException {
        dumpTypeMap(dumperContext, new DpiDumper());
    }

    private void dumpMemory(DumperContext dumperContext) throws DumpException {
        dumpTypeMap(dumperContext, new MemoryDumper());
    }

    private void dumpError(DumperContext dumperContext) throws DumpException {
        dumpTypeMap(dumperContext, new ErrorDumper());
    }

    private void dumpNetwork(DumperContext dumperContext) throws DumpException {
        dumpTypeMap(dumperContext, new NetworkDumper());
    }

    private void dumpTel(DumperContext dumperContext) throws DumpException {
        dumpTypeMap(dumperContext, new TelDumper());
    }

    private void dumpTypeMap(DumperContext dumperContext, InfoDumper dumper) throws DumpException {
        PrintStream writer = dumperContext.getStdout();

        if (mIsAll) {
            writer.println("[" + dumper.getTitle() + "]");
        }

        LinkedHashMap<String, String> dumps = dumper.getDumpMap(mContext);
        if (dumps == null) {
            writer.println(dumper.getErrorMessage());
            if (mIsAll) {
                writer.println("");
            }
            return;
        }

        for (Map.Entry<String, String> e : dumps.entrySet()) {
            writer.println(e.getKey() + ": " + e.getValue());
        }

        if (mIsAll) {
            writer.println("");
        }

    }

    private void dumpTypeList(DumperContext dumperContext, InfoDumper dumper) throws DumpException {
        PrintStream writer = dumperContext.getStdout();

        if (mIsAll) {
            writer.println("[" + dumper.getTitle() + "]");
        }

        List<String> dumps = dumper.getDumpList(mContext);
        if (dumps == null) {
            writer.println(dumper.getErrorMessage());
            if (mIsAll) {
                writer.println("");
            }
            return;
        }

        for (String dump : dumps) {
            writer.println(dump);
        }

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
