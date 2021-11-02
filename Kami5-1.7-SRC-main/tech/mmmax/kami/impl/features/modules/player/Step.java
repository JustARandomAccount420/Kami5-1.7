/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 */
package tech.mmmax.kami.impl.features.modules.player;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Step
extends Module {
    Value<Number> stepHeight = new ValueBuilder().withDescriptor("Step Height").withValue(2.1).withRange(0.1, (Double)7).register(this);
    Value<String> mode = new ValueBuilder().withDescriptor("Mode").withValue("Vanilla").withModes("Vanilla", "NCP").register(this);
    float oldVal = 0.0f;

    public Step() {
        super("Step", Feature.Category.Player);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.oldVal = 0.5f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (NullUtils.nullCheck()) {
            return;
        }
        Step.mc.player.stepHeight = this.oldVal;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        if (this.mode.getValue().equals("Vanilla")) {
            Step.mc.player.stepHeight = this.stepHeight.getValue().floatValue();
        }
    }

    @Override
    public String getHudInfo() {
        return this.mode.getValue();
    }
}

