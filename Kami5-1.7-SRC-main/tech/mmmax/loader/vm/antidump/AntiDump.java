/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.Launch
 *  net.minecraft.launchwrapper.LaunchClassLoader
 */
package tech.mmmax.loader.vm.antidump;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import tech.mmmax.loader.hooks.NativeUtils;
import tech.mmmax.loader.notify.Message;
import tech.mmmax.loader.vm.antidump.DummyClass;
import tech.mmmax.loader.vm.antidump.ProvideUnsafe;

public class AntiDump {
    private static ClassLoader classLoader;
    private static Method findNative;
    public static final String[] NAUGHTY_FLAGS;

    public static void provideDummyClasses() {
        try {
            Field field = LaunchClassLoader.class.getDeclaredField("resourceCache");
            field.setAccessible(true);
            Map resourceCache = (Map)field.get(Launch.classLoader);
            for (Map.Entry entry : resourceCache.entrySet()) {
                try {
                    if (!((String)entry.getKey()).contains("mmmax") || ((String)entry.getKey()).contains("mixin") || ((String)entry.getKey()).contains("Event")) continue;
                    System.out.println("Provided dummy class: " + (String)entry.getKey());
                    entry.setValue(DummyClass.makeDummyClass((String)entry.getKey()));
                }
                catch (Exception exception) {}
            }
            for (int i = 0; i < 20000; ++i) {
                String string = "tech/mmmax/" + i + "                                                                                              a";
                resourceCache.put(string, DummyClass.makeDummyClass(string));
            }
        }
        catch (Exception e) {
            new Message(Message.MessageType.Error, e.getMessage()).send();
        }
    }

    public static void loadAllClasses() {
        try {
            Field field = LaunchClassLoader.class.getDeclaredField("resourceCache");
            field.setAccessible(true);
            Map resourceCache = (Map)field.get(Launch.classLoader);
            for (Map.Entry entry : resourceCache.entrySet()) {
                try {
                    if (!((String)entry.getKey()).contains("mmmax") || ((String)entry.getKey()).contains("mixin") || ((String)entry.getKey()).contains("Event")) continue;
                    Launch.classLoader.loadClass((String)entry.getKey());
                }
                catch (Throwable error) {
                    error.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            new Message(Message.MessageType.Error, e.getMessage()).send();
        }
    }

    public static void checkLaunchFlags() {
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String illigalArg : NAUGHTY_FLAGS) {
            for (String arg : inputArguments) {
                if (!arg.contains(illigalArg)) continue;
                new Message(Message.MessageType.Important, "Illigal argument " + arg).send();
                NativeUtils.exitHard(0);
            }
        }
    }

    public static void maliciousClassFilter() {
        byte[] dummyClass = DummyClass.makeDummyClass("tech/mmmax/MaliciousClassFilter");
        ProvideUnsafe.UNSAFE.defineClass("tech.mmmax.MaliciousClassFilter", dummyClass, 0, dummyClass.length, null, null);
        System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "rina.onepop.club.MaliciousClassFilter");
    }

    private static void resolveClassLoader() throws NoSuchMethodException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            String vmName = System.getProperty("java.vm.name");
            String dll = vmName.contains("Client VM") ? "/bin/client/jvm.dll" : "/bin/server/jvm.dll";
            try {
                System.load(System.getProperty("java.home") + dll);
            }
            catch (UnsatisfiedLinkError e) {
                throw new RuntimeException(e);
            }
            classLoader = AntiDump.class.getClassLoader();
        } else {
            classLoader = null;
        }
        findNative = ClassLoader.class.getDeclaredMethod("findNative", ClassLoader.class, String.class);
        try {
            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            ProvideUnsafe.UNSAFE.putObjectVolatile(cls, ProvideUnsafe.UNSAFE.staticFieldOffset(logger), null);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        findNative.setAccessible(true);
    }

    private static long getSymbol(String symbol) throws InvocationTargetException, IllegalAccessException {
        long address = (Long)findNative.invoke(null, classLoader, symbol);
        if (address == 0L) {
            throw new NoSuchElementException(symbol);
        }
        return ProvideUnsafe.UNSAFE.getLong(address);
    }

    static {
        NAUGHTY_FLAGS = new String[]{"-javaagent", "-Xdebug", "-agentlib", "-Xrunjdwp", "-Xnoagent", "-verbose", "-DproxySet", "-DproxyHost", "-DproxyPort", "-Djavax.net.ssl.trustStore", "-Djavax.net.ssl.trustStorePassword"};
    }
}

