/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 */
package tech.mmmax.kami.api.utils.player;

import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import tech.mmmax.kami.api.management.FriendManager;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class TargetUtils
implements IMinecraft {
    public static EntityLivingBase getTarget(double targetRange) {
        return TargetUtils.mc.world.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityPlayer).filter(TargetUtils::isAlive).filter(entity -> entity.getEntityId() != TargetUtils.mc.player.getEntityId()).filter(entity -> !FriendManager.INSTANCE.isFriend((Entity)entity)).filter(entity -> (double)TargetUtils.mc.player.getDistance(entity) <= targetRange).min(Comparator.comparingDouble(entity -> TargetUtils.mc.player.getDistance(entity))).orElse(null);
    }

    public static boolean isAlive(Entity entity) {
        return TargetUtils.isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0f;
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }
}

