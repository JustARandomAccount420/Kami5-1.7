/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 */
package tech.mmmax.kami.impl.features.modules.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tech.mmmax.kami.api.event.MoveEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.player.PlayerUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class PhaseWalk
extends Module {
    Timer timer = new Timer();
    Value<Boolean> edgeEnable = new ValueBuilder().withDescriptor("Edge Enable").withValue(false).register(this);
    Value<String> mode = new ValueBuilder().withDescriptor("Mode").withValue("Clip").withModes("Clip", "Smooth").register(this);
    Value<Number> delay = new ValueBuilder().withDescriptor("Delay").withValue(200).withRange(0, 1000).withAction(s -> this.timer.setDelay(((Number)s.getValue()).longValue())).register(this);
    Value<Number> attempts = new ValueBuilder().withDescriptor("Attempts").withValue(5).withRange(0, 10).register(this);
    Value<Boolean> cancelPlayer = new ValueBuilder().withDescriptor("Cancel").withValue(true).register(this);
    Value<String> handleTeleport = new ValueBuilder().withDescriptor("Handle Teleport").withValue("All").withModes("All", "Below", "Above", "NoBand", "Last", "Cancel", "None").register(this);
    Value<Number> limitAmount = new ValueBuilder().withDescriptor("Limit Amount").withValue(0.3).withRange((Double)0, (Double)1).register(this);
    Value<Number> speed = new ValueBuilder().withDescriptor("Speed").withValue(3).withRange(1, 10).register(this);
    Value<Boolean> autoSpeed = new ValueBuilder().withDescriptor("Auto Speed").withValue(true).register(this);
    boolean cancel = false;
    int teleportID = 0;

    public PhaseWalk() {
        super("PhaseWalk", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer && this.cancel && this.cancelPlayer.getValue().booleanValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport && this.handleTeleport.getValue().equals("Cancel")) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook)event.getPacket()).getTeleportId();
            if (this.handleTeleport.getValue().equals("All")) {
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID - 1));
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID));
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID + 1));
            }
            if (this.handleTeleport.getValue().equals("Below")) {
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID + -1));
            }
            if (this.handleTeleport.getValue().equals("Above")) {
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID + 1));
            }
            if (this.handleTeleport.getValue().equals("NoBand")) {
                mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(0.0, 1337.0, 0.0, PhaseWalk.mc.player.onGround));
                mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID + 1));
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        PhaseWalk.mc.player.motionX = 0.0;
        PhaseWalk.mc.player.motionY = 0.0;
        PhaseWalk.mc.player.motionZ = 0.0;
        if (this.mode.getValue().equals("Clip")) {
            if (this.shouldPacket()) {
                if (this.timer.isPassed()) {
                    double[] forward = PlayerUtils.forward(this.getSpeed());
                    for (int i = 0; i < this.attempts.getValue().intValue(); ++i) {
                        this.sendPackets(PhaseWalk.mc.player.posX + forward[0], PhaseWalk.mc.player.posY + this.getUpMovement(), PhaseWalk.mc.player.posZ + forward[1]);
                    }
                    this.timer.resetDelay();
                }
            } else {
                this.cancel = false;
            }
        }
    }

    double getUpMovement() {
        return (double)(PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown() ? 1 : (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown() ? -1 : 0)) * this.getSpeed();
    }

    public void sendPackets(double x, double y, double z) {
        this.cancel = false;
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(x, y, z, PhaseWalk.mc.player.onGround));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(0.0, 1337.0, 0.0, PhaseWalk.mc.player.onGround));
        this.cancel = true;
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.shouldPacket()) {
            if (this.mode.getValue().equals("Smooth")) {
                double[] forward = PlayerUtils.forward(this.getSpeed());
                for (int i = 0; i < this.attempts.getValue().intValue(); ++i) {
                    this.sendPackets(PhaseWalk.mc.player.posX + forward[0], PhaseWalk.mc.player.posY + this.getUpMovement(), PhaseWalk.mc.player.posZ + forward[1]);
                }
            }
            event.x = 0.0;
            event.y = 0.0;
            event.z = 0.0;
        }
    }

    double getSpeed() {
        return this.autoSpeed.getValue() != false ? PlayerUtils.getDefaultMoveSpeed() / 10.0 : this.speed.getValue().doubleValue() / 100.0;
    }

    boolean shouldPacket() {
        return this.edgeEnable.getValue() == false || PhaseWalk.mc.player.collidedHorizontally;
    }

    @Override
    public String getHudInfo() {
        return this.mode.getValue();
    }
}

