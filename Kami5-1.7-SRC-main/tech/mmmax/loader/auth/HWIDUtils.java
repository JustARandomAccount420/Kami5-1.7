/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  oshi.SystemInfo
 *  oshi.hardware.HardwareAbstractionLayer
 */
package tech.mmmax.loader.auth;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import tech.mmmax.loader.hooks.NativeUtils;

public class HWIDUtils {
    SystemInfo info = new SystemInfo();
    HardwareAbstractionLayer hardware = this.info.getHardware();
    public static HWIDUtils INSTANCE = new HWIDUtils();

    public String getUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public UUID getHWID() {
        return this.hash(System.getProperty("user.name") + System.getProperty("os.arch") + NativeUtils.getMemorySize() + NativeUtils.getProcessorID());
    }

    public UUID hash(String toHash) {
        return UUID.nameUUIDFromBytes(toHash.getBytes());
    }
}

