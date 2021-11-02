/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 */
package tech.mmmax.loader.hooks;

import com.sun.jna.Native;
import java.io.File;

public class NativeUtils {
    public static native void initAntiDump();

    public static native void exitHard(int var0);

    public static native String getProcessorID();

    public static native long getMemorySize();

    public static void init() {
    }

    static {
        try {
            System.setProperty("jna.tmpdir", new File("tmp/").getAbsolutePath());
            File f = Native.extractFromResourcePath((String)"kamiprotect_n");
            System.load(f.getAbsolutePath());
            System.out.println("Successfully loaded library");
        }
        catch (Throwable error) {
            error.printStackTrace();
        }
    }
}

