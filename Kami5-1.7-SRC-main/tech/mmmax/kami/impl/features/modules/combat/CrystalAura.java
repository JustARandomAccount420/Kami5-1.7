/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemSword
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  org.lwjgl.opengl.GL11
 */
package tech.mmmax.kami.impl.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.player.RotationUtil;
import tech.mmmax.kami.api.utils.player.TargetUtils;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.utils.world.BlockUtils;
import tech.mmmax.kami.api.utils.world.CrystalUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class CrystalAura
extends Module {
    public static CrystalAura INSTANCE;
    Timer placeTimer = new Timer();
    Timer breakTimer = new Timer();
    Value<Number> lethalHealth = new ValueBuilder().withDescriptor("Lethal Health").withValue(18).withRange(0, 36).register(this);
    Value<Number> lethalMinDmg = new ValueBuilder().withDescriptor("Lethal Min Damage").withValue(2).withRange(0, 36).register(this);
    Value<Number> lethalMaxSelfDmg = new ValueBuilder().withDescriptor("Lethal Max Self DMG").withValue(36).withRange(0, 36).register(this);
    Value<Boolean> antiSuicide = new ValueBuilder().withDescriptor("Anti Suicide").withValue(false).register(this);
    Value<Number> antiSuicideHealth = new ValueBuilder().withDescriptor("Anti Suicide Health").withValue(36).withRange(0, 36).register(this);
    Value<Number> antiSuicideFactor = new ValueBuilder().withDescriptor("Anti Suicide Factor").withValue(4).withRange(0, 10).register(this);
    Value<Number> minDamage = new ValueBuilder().withDescriptor("Min Damage").withValue(4.0).withRange(0.0, 20.0).register(this);
    Value<Number> maxSelfDamage = new ValueBuilder().withDescriptor("Max Self Damage").withValue(15.0).withRange(0.0, (Double)36).register(this);
    Value<Number> targetRange = new ValueBuilder().withDescriptor("Target Range").withValue(7.0).withRange(3.0, 20.0).register(this);
    Value<Number> range = new ValueBuilder().withDescriptor("Range").withValue(5).withRange(1, 10).register(this);
    Value<Number> wallsRange = new ValueBuilder().withDescriptor("Walls Range").withValue(3).withRange(1, 5).register(this);
    Value<Number> raytraceHits = new ValueBuilder().withDescriptor("Raytrace Hits").withValue(2).withRange(1, 9).register(this);
    Value<Number> shrinkFactor = new ValueBuilder().withDescriptor("Shrink Factor").withValue(0.3).withRange((Double)0, (Double)1).register(this);
    Value<Number> breakDelay = new ValueBuilder().withDescriptor("Break Delay").withValue(1).withRange(0, 1000).withAction(setting -> this.breakTimer.setDelay(((Number)setting.getValue()).longValue())).register(this);
    Value<Number> placeDelay = new ValueBuilder().withDescriptor("Place Delay").withValue(1).withRange(0, 1000).withAction(setting -> this.placeTimer.setDelay(((Number)setting.getValue()).longValue())).register(this);
    Value<Boolean> inhibit = new ValueBuilder().withDescriptor("Inhibit").withValue(true).register(this);
    Value<Boolean> fastBreak = new ValueBuilder().withDescriptor("Fast Break").withValue(true).register(this);
    Value<Number> noStuckChain = new ValueBuilder().withDescriptor("No Stuck Chain").withValue(3).withRange(0, 10).register(this);
    Value<Boolean> breakCrystals = new ValueBuilder().withDescriptor("Break").withValue(true).register(this);
    Value<Boolean> breakRotate = new ValueBuilder().withDescriptor("Break Rotate").withValue(true).register(this);
    Value<Number> breakAttempts = new ValueBuilder().withDescriptor("Break Attempts").withValue(1).withRange(1, 10).register(this);
    Value<Number> onlyOwnHealth = new ValueBuilder().withDescriptor("Only Own Health").withValue(36).withRange(0, 36).register(this);
    Value<Boolean> setDead = new ValueBuilder().withDescriptor("Set Dead").withValue(true).register(this);
    Value<String> swingMode = new ValueBuilder().withDescriptor("Swing Mode").withValue("Auto").withModes("Auto", "Mainhand", "Offhand", "None").register(this);
    Value<Boolean> placeCrystals = new ValueBuilder().withDescriptor("Place").withValue(true).register(this);
    Value<Boolean> fastTickPlace = new ValueBuilder().withDescriptor("Fast Tick Place").withValue(false).register(this);
    Value<Boolean> one13 = new ValueBuilder().withDescriptor("1.13").withValue(false).register(this);
    Value<Boolean> packetPlace = new ValueBuilder().withDescriptor("Packet Place").withValue(true).register(this);
    Value<Boolean> placeRotate = new ValueBuilder().withDescriptor("Place Rotate").withValue(true).register(this);
    Value<Boolean> switchToSlot = new ValueBuilder().withDescriptor("Switch").withValue(false).register(this);
    Value<Boolean> ghostSwitch = new ValueBuilder().withDescriptor("Ghost Switch").withValue(false).register(this);
    Value<Number> placeAttempts = new ValueBuilder().withDescriptor("Place Attempts").withValue(2).withRange(1, 5).register(this);
    Value<Boolean> placeInhibit = new ValueBuilder().withDescriptor("Place Inhibit").withValue(false).register(this);
    Value<Boolean> placeBlocks = new ValueBuilder().withDescriptor("Place Blocks").withValue(false).register(this);
    Value<Number> moveFactor = new ValueBuilder().withDescriptor("Force Power").withValue(0.2).withRange(0.0, 5.0).register(this);
    Value<Boolean> fastTickBreak = new ValueBuilder().withDescriptor("Fast Tick B`reak").withValue(false).register(this);
    Value<Boolean> predict = new ValueBuilder().withDescriptor("Predict").withValue(false).register(this);
    Value<Boolean> antiWeakness = new ValueBuilder().withDescriptor("Anti Weakness").withValue(true).register(this);
    Value<Number> breakPredictAttempts = new ValueBuilder().withDescriptor("Predict Attempts").withValue(3).withRange(1, 10).register(this);
    Value<Boolean> autoSkip = new ValueBuilder().withDescriptor("Auto Skip").withValue(true).register(this);
    Value<Number> skip = new ValueBuilder().withDescriptor("Predict Skip").withValue(2).withRange(1, 10).register(this);
    Value<Number> startFactor = new ValueBuilder().withDescriptor("Predict Start").withValue(2).withRange(0, 10).register(this);
    Value<Number> add = new ValueBuilder().withDescriptor("Predict Add").withValue(2).withRange(0, 10).register(this);
    Value<Boolean> smartPredict = new ValueBuilder().withDescriptor("Smart Predict").withValue(true).register(this);
    Value<Boolean> fade = new ValueBuilder().withDescriptor("Fade").withValue(false).register(this);
    Value<Number> fadeTime = new ValueBuilder().withDescriptor("Fade Time").withValue(1000).withRange(100, 2000).register(this);
    Value<Boolean> pulse = new ValueBuilder().withDescriptor("Pulse").withValue(false).register(this);
    Value<Number> pulseAmount = new ValueBuilder().withDescriptor("Pulse Amount").withValue(10).withRange(5, 50).register(this);
    Value<Number> pulseTime = new ValueBuilder().withDescriptor("Pulse Time").withValue(100).withRange(50, 1000).register(this);
    Value<Color> fillColorS = new ValueBuilder().withDescriptor("Fill Color").withValue(new Color(0, 0, 0, 100)).register(this);
    Value<Color> lineColorS = new ValueBuilder().withDescriptor("Outline Color").withValue(new Color(255, 255, 255, 255)).register(this);
    EntityLivingBase target;
    CrystalUtil.Crystal placePos;
    List<CrystalUtil.Crystal> oldPlacements = new ArrayList<CrystalUtil.Crystal>();
    int highestID;
    int lastSkip;
    int curAlpha = this.fillColorS.getValue().getAlpha();
    boolean shouldPredict = false;
    int currStuck = 0;
    long lastBroke = System.currentTimeMillis();

    public CrystalAura() {
        super("Crystal Aura", Feature.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.breakTimer.resetDelay();
        this.breakTimer.setPaused(false);
        this.placeTimer.resetDelay();
        this.placeTimer.setPaused(true);
        this.highestID = 0;
        this.currStuck = 0;
        if (NullUtils.nullCheck()) {
            return;
        }
        RotationUtil.INSTANCE.rotating = false;
        RotationUtil.INSTANCE.resetRotations();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (NullUtils.nullCheck()) {
            return;
        }
        RotationUtil.INSTANCE.rotating = false;
        RotationUtil.INSTANCE.resetRotations();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (RotationUtil.INSTANCE.rotatedYaw == RotationUtil.INSTANCE.yaw && RotationUtil.INSTANCE.rotatedPitch == RotationUtil.INSTANCE.pitch) {
            RotationUtil.INSTANCE.rotating = false;
            RotationUtil.INSTANCE.resetRotations();
        }
        this.target = TargetUtils.getTarget(this.targetRange.getValue().doubleValue());
        if (this.target == null) {
            return;
        }
        this.doCrystalAura(event);
    }

    public void doCrystalAura(TickEvent event) {
        int i;
        if (this.placeTimer.isPassed() && this.placeCrystals.getValue().booleanValue()) {
            if (this.fastTickPlace.getValue().booleanValue() || event instanceof TickEvent.ClientTickEvent) {
                int obbySlot = InventoryUtils.getInventoryItemSlot(Item.getItemFromBlock((Block)Blocks.OBSIDIAN));
                boolean lethal = (double)(this.target.getHealth() + this.target.getAbsorptionAmount()) <= this.lethalHealth.getValue().doubleValue();
                this.placePos = CrystalUtil.getPlacePos((Entity)this.target, this.range.getValue().doubleValue(), this.wallsRange.getValue().doubleValue(), this.one13.getValue(), this.moveFactor.getValue().doubleValue(), this.antiSuicide.getValue() != false && (double)(CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount()) <= this.antiSuicideHealth.getValue().doubleValue(), this.antiSuicideFactor.getValue().doubleValue(), lethal ? this.lethalMinDmg.getValue().doubleValue() : this.minDamage.getValue().doubleValue(), lethal ? this.lethalMaxSelfDmg.getValue().doubleValue() : this.maxSelfDamage.getValue().doubleValue(), this.placeInhibit.getValue(), this.placeBlocks.getValue() != false && obbySlot != -1, this.raytraceHits.getValue().intValue(), this.shrinkFactor.getValue().doubleValue());
                if (this.placePos != null) {
                    if (this.oldPlacements.contains(this.placePos)) {
                        this.oldPlacements.remove(this.placePos);
                    }
                    this.oldPlacements.add(this.placePos);
                    int oldSlot = CrystalAura.mc.player.inventory.currentItem;
                    if (this.placePos.blockUnder) {
                        int old = CrystalAura.mc.player.inventory.currentItem;
                        InventoryUtils.switchToSlotGhost(obbySlot);
                        BlockUtils.placeBlock(this.placePos.crystalPos.add(0, -1, 0), true);
                        InventoryUtils.switchToSlotGhost(old);
                    }
                    if (CrystalUtil.getCrystalHand() == EnumHand.MAIN_HAND) {
                        if (this.switchToSlot.getValue().booleanValue()) {
                            InventoryUtils.switchToSlot(Items.END_CRYSTAL);
                        } else if (this.ghostSwitch.getValue().booleanValue()) {
                            int crystalSlot = InventoryUtils.getHotbarItemSlot(Items.END_CRYSTAL);
                            if (crystalSlot == -1) {
                                return;
                            }
                            InventoryUtils.switchToSlotGhost(crystalSlot);
                        }
                    }
                    if (CrystalAura.mc.player.getHeldItem(CrystalUtil.getCrystalHand()).getItem() == Items.END_CRYSTAL || this.ghostSwitch.getValue().booleanValue()) {
                        EnumHand hand = this.swingMode.getValue().equals("None") ? null : (this.swingMode.getValue().equals("Auto") ? CrystalUtil.getCrystalHand() : (this.swingMode.getValue().equals("Offhand") ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                        for (i = 0; i < this.placeAttempts.getValue().intValue(); ++i) {
                            CrystalUtil.placeCrystal(this.placePos.crystalPos, this.packetPlace.getValue(), hand);
                        }
                        this.shouldPredict = true;
                    }
                    if (CrystalUtil.getCrystalHand() == EnumHand.MAIN_HAND && this.ghostSwitch.getValue().booleanValue() && !this.switchToSlot.getValue().booleanValue()) {
                        InventoryUtils.switchToSlotGhost(oldSlot);
                    }
                }
            }
            this.placeTimer.resetDelay();
            this.breakTimer.setPaused(false);
            this.placeTimer.setPaused(true);
            ++this.currStuck;
            if (this.currStuck > this.noStuckChain.getValue().intValue()) {
                this.currStuck = 0;
                return;
            }
        }
        if (this.breakTimer.isPassed()) {
            if (this.breakCrystals.getValue().booleanValue() && (this.fastTickBreak.getValue().booleanValue() || event instanceof TickEvent.ClientTickEvent)) {
                int i2;
                EntityEnderCrystal crystal = CrystalUtil.getCrystalToBreak(this.inhibit.getValue(), this.range.getValue().doubleValue());
                int swordSlot = -1;
                int oldSlotWeak = CrystalAura.mc.player.inventory.currentItem;
                for (i2 = 0; i2 < 9; ++i2) {
                    if (!(CrystalAura.mc.player.inventory.getStackInSlot(i2).getItem() instanceof ItemSword)) continue;
                    swordSlot = i2;
                }
                if (this.antiWeakness.getValue().booleanValue() && swordSlot != -1 && CrystalAura.mc.player.isPotionActive(MobEffects.WEAKNESS) && crystal != null) {
                    InventoryUtils.switchToSlotGhost(swordSlot);
                }
                if (this.predict.getValue().booleanValue()) {
                    if (!this.smartPredict.getValue().booleanValue() || this.shouldPredict) {
                        int start = this.highestID + this.startFactor.getValue().intValue();
                        for (i = 0; i < this.breakPredictAttempts.getValue().intValue(); ++i) {
                            int crystalID = start + i * (this.autoSkip.getValue() != false ? this.lastSkip : this.skip.getValue().intValue());
                            if (CrystalUtil.hitCrystals.contains(crystalID)) continue;
                            CrystalUtil.breakCrystal(crystalID);
                        }
                        this.highestID += this.add.getValue().intValue();
                        this.shouldPredict = false;
                    }
                } else if (crystal != null) {
                    if (this.breakRotate.getValue().booleanValue()) {
                        RotationUtil.INSTANCE.rotating = true;
                        RotationUtil.INSTANCE.rotate(crystal.getPositionVector());
                    }
                    for (i2 = 0; i2 < this.breakAttempts.getValue().intValue(); ++i2) {
                        CrystalUtil.breakCrystal(crystal);
                    }
                    this.curAlpha = this.fillColorS.getValue().getAlpha() + this.pulseAmount.getValue().intValue();
                    this.lastBroke = System.currentTimeMillis();
                }
                if (this.antiWeakness.getValue().booleanValue() && swordSlot != -1 && CrystalAura.mc.player.isPotionActive(MobEffects.WEAKNESS) && crystal != null) {
                    InventoryUtils.switchToSlotGhost(oldSlotWeak);
                }
            }
            this.breakTimer.resetDelay();
            this.placeTimer.setPaused(false);
            this.breakTimer.setPaused(true);
            ++this.currStuck;
            if (this.currStuck > this.noStuckChain.getValue().intValue()) {
                this.currStuck = 0;
                return;
            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        SPacketSpawnObject packet;
        if (NullUtils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSpawnObject) {
            packet = (SPacketSpawnObject)event.getPacket();
            if (this.fastBreak.getValue().booleanValue() && this.target != null && packet.getType() == 51 && CrystalAura.mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= this.range.getValue().doubleValue() && ((double)CrystalAura.mc.player.getHealth() >= this.onlyOwnHealth.getValue().doubleValue() || this.isSelfCrystal(new BlockPos(packet.getX(), packet.getY(), packet.getZ())))) {
                for (int i = 0; i < this.breakAttempts.getValue().intValue(); ++i) {
                    CrystalUtil.breakCrystal(packet.getEntityID());
                }
                this.curAlpha = this.fillColorS.getValue().getAlpha() + this.pulseAmount.getValue().intValue();
                this.lastBroke = System.currentTimeMillis();
                this.breakTimer.resetDelay();
                this.placeTimer.setPaused(false);
                this.breakTimer.setPaused(true);
            }
            this.checkID(packet.getEntityID());
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            packet = (SPacketSoundEffect)event.getPacket();
            if (this.setDead.getValue().booleanValue() && packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity c : CrystalUtil.getLoadedCrystalsInRange(this.range.getValue().doubleValue())) {
                    if (!(c.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0)) continue;
                    c.setDead();
                }
            }
        }
    }

    boolean isSelfCrystal(BlockPos pos) {
        double targetDamage = CrystalUtil.calculateDamage((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, (Entity)this.target, 0.0);
        double selfDamage = CrystalUtil.calculateDamage((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, (Entity)CrystalAura.mc.player, 0.0);
        boolean lethal = (double)(this.target.getHealth() + this.target.getAbsorptionAmount()) <= this.lethalHealth.getValue().doubleValue();
        double minDMG = lethal ? this.lethalMinDmg.getValue().doubleValue() : this.minDamage.getValue().doubleValue();
        double maxDMG = lethal ? this.lethalMaxSelfDmg.getValue().doubleValue() : this.maxSelfDamage.getValue().doubleValue();
        return this.placePos.crystalPos.equals((Object)pos) || targetDamage > minDMG && selfDamage < maxDMG && targetDamage > selfDamage;
    }

    void checkID(int id) {
        if (id > this.highestID) {
            this.lastSkip = this.highestID - id;
            this.highestID = id;
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.target == null || this.placePos == null) {
            return;
        }
        if (this.oldPlacements.size() > 0) {
            this.oldPlacements = this.oldPlacements.stream().filter(Objects::nonNull).distinct().filter(crystal -> System.currentTimeMillis() - crystal.getStartTime() < this.fadeTime.getValue().longValue()).collect(Collectors.toList());
        }
        if (System.currentTimeMillis() - this.lastBroke > this.pulseTime.getValue().longValue() || !this.pulse.getValue().booleanValue()) {
            this.curAlpha = this.fillColorS.getValue().getAlpha();
        } else {
            double value = this.normalize(System.currentTimeMillis() - this.lastBroke, 0.0, this.pulseTime.getValue().doubleValue());
            value = MathHelper.clamp((double)value, (double)0.0, (double)1.0);
            value = -value;
            this.curAlpha = (int)MathHelper.clamp((double)((double)this.fillColorS.getValue().getAlpha() + (value += 1.0) * this.pulseAmount.getValue().doubleValue()), (double)0.0, (double)255.0);
        }
        Color fillColor = ColorUtil.newAlpha(this.fillColorS.getValue(), this.curAlpha);
        Color lineColor = this.lineColorS.getValue();
        GL11.glLineWidth((float)1.7f);
        RenderUtil.renderBB(7, new AxisAlignedBB(this.placePos.crystalPos), fillColor, fillColor);
        RenderUtil.renderBB(3, new AxisAlignedBB(this.placePos.crystalPos), lineColor, lineColor);
        if (this.fade.getValue().booleanValue()) {
            for (CrystalUtil.Crystal crystal2 : this.oldPlacements) {
                if (this.placePos != null && crystal2.crystalPos.equals((Object)this.placePos.crystalPos)) continue;
                double normal = this.normalize(System.currentTimeMillis() - crystal2.getStartTime(), 0.0, this.fadeTime.getValue().doubleValue());
                Color fillFade = ColorUtil.interpolate((float)normal, ColorUtil.newAlpha(fillColor, 0), fillColor);
                Color outlineFade = ColorUtil.interpolate((float)normal, ColorUtil.newAlpha(lineColor, 0), lineColor);
                RenderUtil.renderBB(7, new AxisAlignedBB(crystal2.crystalPos), fillFade, fillFade);
                RenderUtil.renderBB(3, new AxisAlignedBB(crystal2.crystalPos), outlineFade, outlineFade);
            }
        }
    }

    @Override
    public String getHudInfo() {
        return this.target != null ? this.target.getName() + ", " + (double)(System.currentTimeMillis() - this.breakTimer.getStartTime()) / 10.0 + (this.placePos != null ? ", " + (this.antiSuicide.getValue() != false ? this.placePos.getEnemyDamage() - this.placePos.getSelfDamage() : this.placePos.getEnemyDamage()) : "") : "";
    }

    double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}

