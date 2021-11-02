/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package tech.mmmax.kami.impl.features.modules.player;

import java.util.LinkedList;
import java.util.Queue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.gui.ClickGui;

public class ChorusManipulation
extends Module {
    Queue<CPacketPlayer> packets = new LinkedList<CPacketPlayer>();
    Queue<CPacketConfirmTeleport> tpPackets = new LinkedList<CPacketConfirmTeleport>();
    Value<Boolean> cancel = new ValueBuilder().withDescriptor("Cancel").withValue(false).register(this);
    AxisAlignedBB render;

    public ChorusManipulation() {
        super("ChorusMan", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && this.cancel.getValue().booleanValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            this.packets.add((CPacketPlayer)event.getPacket());
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport) {
            this.tpPackets.add((CPacketConfirmTeleport)event.getPacket());
            event.setCanceled(true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        while (!this.packets.isEmpty()) {
            mc.getConnection().sendPacket((Packet)this.packets.poll());
        }
        while (!this.tpPackets.isEmpty()) {
            mc.getConnection().sendPacket((Packet)this.tpPackets.poll());
        }
        this.render = null;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (this.render != null) {
            GL11.glLineWidth((float)1.0f);
            RenderUtil.renderBB(3, this.render, ClickGui.CONTEXT.getColorScheme().getMainColor(0), ClickGui.CONTEXT.getColorScheme().getMainColor(0));
        }
    }
}

