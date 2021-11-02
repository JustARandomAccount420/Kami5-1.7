/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDownloadTerrain
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 */
package tech.mmmax.kami.impl.features.modules.player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.player.PlayerUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.mixin.mixins.access.ISPacketPlayerPosLook;

public class PacketFly
extends Module {
    public static PacketFly INSTANCE;
    Value<String> phaseMode = new ValueBuilder().withDescriptor("Phase Mode").withValue("NoClip").withModes("NoClip", "Sand", "Packet", "Skip").register(this);
    Value<Boolean> tpAccept = new ValueBuilder().withDescriptor("TP Accept", "tpAccept").withValue(true).register(this);
    Value<Number> speed = new ValueBuilder().withDescriptor("Speed").withValue(0.03).withRange(0.01, 0.1).register(this);
    Value<Number> jitterAmount = new ValueBuilder().withDescriptor("Jitter Amount").withValue(1.0).withRange(0.0, 3.0).register(this);
    Value<Boolean> bound = new ValueBuilder().withDescriptor("Bound").withValue(true).register(this);
    Value<Number> boundAmount = new ValueBuilder().withDescriptor("Bound Amount").withValue(-1000).withRange(-3000, 3000).register(this);
    Value<Boolean> cancelPacket = new ValueBuilder().withDescriptor("Cancel Packet").withValue(true).register(this);
    Value<Boolean> noRubberband = new ValueBuilder().withDescriptor("No Rubberband").withValue(true).register(this);
    Value<Boolean> edgeEnable = new ValueBuilder().withDescriptor("Edge Enable").withValue(false).register(this);
    Value<Number> slide = new ValueBuilder().withDescriptor("Slide").withValue(0.5).withRange((Double)0, (Double)1).register(this);
    Value<Boolean> debug = new ValueBuilder().withDescriptor("Debug").withValue(false).register(this);
    boolean cancelling = true;
    int teleportId;
    List<CPacketPlayer> packets = new ArrayList<CPacketPlayer>();

    public PacketFly() {
        super("PacketFly", Feature.Category.Player);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.phaseMode.getValue().equalsIgnoreCase("NoClip")) {
            PacketFly.mc.player.noClip = false;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.phaseMode.getValue().equalsIgnoreCase("NoClip")) {
            PacketFly.mc.player.noClip = true;
        }
        this.teleportId = 0;
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.shouldFly()) {
            this.cancelling = false;
            double[] forward = PlayerUtils.forward(this.getSpeed() * this.getJitter());
            double up = PacketFly.mc.gameSettings.keyBindJump.isKeyDown() ? 0.0233 : (PacketFly.mc.gameSettings.keyBindSneak.isKeyDown() ? -0.0233 : 1.0E-6);
            double[] playerPos = this.toPlayerPos(forward[0], up, forward[1]);
            PacketFly.mc.player.setVelocity(forward[0], up, forward[1]);
            CPacketPlayer.Position packetPlayer = new CPacketPlayer.Position(playerPos[0], playerPos[1], playerPos[2], PacketFly.mc.player.onGround);
            PacketFly.mc.player.connection.sendPacket((Packet)packetPlayer);
            this.packets.add((CPacketPlayer)packetPlayer);
            PacketFly.mc.player.setPosition(playerPos[0], playerPos[1], playerPos[2]);
            if (this.bound.getValue().booleanValue()) {
                CPacketPlayer.Position bounds = new CPacketPlayer.Position(playerPos[0], this.boundAmount.getValue().doubleValue(), playerPos[2], PacketFly.mc.player.onGround);
                PacketFly.mc.player.connection.sendPacket((Packet)bounds);
                this.packets.add((CPacketPlayer)bounds);
            }
            ++this.teleportId;
            if (this.tpAccept.getValue().booleanValue()) {
                PacketFly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId - 1));
                PacketFly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId));
                PacketFly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId + 1));
            }
            this.cancelling = true;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (event.getTime() == PacketEvent.Time.Send) {
            CPacketConfirmTeleport packetPlayer;
            if (event.getPacket() instanceof CPacketPlayer.Position && this.cancelPacket.getValue().booleanValue()) {
                packetPlayer = (CPacketPlayer.Position)event.getPacket();
                if (this.cancelling) {
                    event.setCanceled(true);
                }
            }
            if (event.getPacket() instanceof CPacketConfirmTeleport) {
                packetPlayer = (CPacketConfirmTeleport)event.getPacket();
            }
        }
        if (event.getTime() == PacketEvent.Time.Receive && event.getPacket() instanceof SPacketPlayerPosLook && this.noRubberband.getValue().booleanValue()) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
            ISPacketPlayerPosLook inter = (ISPacketPlayerPosLook)packet;
            if (PacketFly.mc.player.isEntityAlive() && PacketFly.mc.world.isBlockLoaded(new BlockPos(PacketFly.mc.player.posX, PacketFly.mc.player.posY, PacketFly.mc.player.posZ)) && !(PacketFly.mc.currentScreen instanceof GuiDownloadTerrain)) {
                if (this.teleportId <= 0) {
                    this.teleportId = packet.getTeleportId();
                } else {
                    inter.setX(MathHelper.clampedLerp((double)Math.min(PacketFly.mc.player.posX, packet.getX()), (double)Math.max(PacketFly.mc.player.posX, packet.getX()), (double)this.slide.getValue().doubleValue()));
                    inter.setY(MathHelper.clampedLerp((double)Math.min(PacketFly.mc.player.getEntityBoundingBox().minY, packet.getY()), (double)Math.max(PacketFly.mc.player.getEntityBoundingBox().minY, packet.getY()), (double)this.slide.getValue().doubleValue()));
                    inter.setZ(MathHelper.clampedLerp((double)Math.min(PacketFly.mc.player.posZ, packet.getZ()), (double)Math.max(PacketFly.mc.player.posZ, packet.getZ()), (double)this.slide.getValue().doubleValue()));
                }
            }
        }
    }

    public void move() {
    }

    double getSpeed() {
        return !PacketFly.mc.gameSettings.keyBindJump.isKeyDown() && !PacketFly.mc.gameSettings.keyBindSneak.isKeyDown() ? this.speed.getValue().doubleValue() : 0.0;
    }

    double[] toPlayerPos(double x, double y, double z) {
        return new double[]{PacketFly.mc.player.posX + x, PacketFly.mc.player.posY + y, PacketFly.mc.player.posZ + z};
    }

    double getJitter() {
        return Math.floor(Math.random() * (this.jitterAmount.getValue().doubleValue() - this.jitterAmount.getMin().doubleValue() + 1.0) + this.jitterAmount.getMin().doubleValue());
    }

    boolean isPhasing() {
        return PacketFly.mc.world.getBlockState(PacketFly.mc.player.getPosition()).getMaterial().isReplaceable() || PacketFly.mc.world.getBlockState(PacketFly.mc.player.getPosition().add(0, 1, 0)).getMaterial().isReplaceable();
    }

    boolean isOnEdge() {
        boolean verticalFlying = PacketFly.mc.gameSettings.keyBindJump.isKeyDown() || PacketFly.mc.gameSettings.keyBindSneak.isKeyDown();
        return PacketFly.mc.player.collidedHorizontally || verticalFlying;
    }

    boolean shouldFly() {
        return this.edgeEnable.getValue() == false || this.isOnEdge();
    }

    public void doFly(double x, double y, double z, boolean onGround) {
        CPacketPlayer.Position pos = new CPacketPlayer.Position(PacketFly.mc.player.posX + x, PacketFly.mc.player.posY + y, PacketFly.mc.player.posZ + z, onGround);
        this.packets.add((CPacketPlayer)pos);
        PacketFly.mc.player.connection.sendPacket((Packet)pos);
        ++this.teleportId;
        if (this.tpAccept.getValue().booleanValue()) {
            PacketFly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId - 1));
            PacketFly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId));
            PacketFly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId + 1));
        }
    }
}

