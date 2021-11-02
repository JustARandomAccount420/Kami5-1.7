/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumHand
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package tech.mmmax.kami.impl.features.modules.misc;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class MultiTask
extends Module {
    public static MultiTask INSTANCE;
    public Value<Boolean> cancel = new ValueBuilder().withDescriptor("Cancel").withValue(false).register(this);

    public MultiTask() {
        super("MultiTask", Feature.Category.Misc);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        CPacketPlayerDigging packet;
        if (event.getPacket() instanceof CPacketPlayerDigging && this.cancel.getValue().booleanValue() && (packet = (CPacketPlayerDigging)event.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && MultiTask.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.GOLDEN_APPLE) {
            event.setCanceled(true);
        }
    }
}

