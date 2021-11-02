/*
 * Decompiled with CFR 0.151.
 */
package tech.mmmax.loader.vm.antidump;

import java.lang.reflect.Field;
import sun.misc.Unsafe;
import tech.mmmax.loader.hooks.NativeUtils;

public class ProvideUnsafe {
    public static Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe)f.get(null);
        }
        catch (Exception e) {
            e.printStackTrace();
            NativeUtils.exitHard(0);
        }
    }
}

