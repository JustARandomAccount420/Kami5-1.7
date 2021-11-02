/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Text
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package tech.mmmax.kami.impl.features.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.feature.hud.HudComponent;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.KamiMod;
import tech.mmmax.kami.impl.features.modules.client.HudColors;
import tech.mmmax.kami.impl.gui.ClickGui;

public class Welcomer
extends HudComponent {
    Value<Boolean> autoPos = new ValueBuilder().withDescriptor("Auto Pos").withValue(true).withAction(s -> {
        this.xPos.setActive((Boolean)s.getValue() == false);
        this.yPos.setActive((Boolean)s.getValue() == false);
    }).register(this);

    public Welcomer() {
        super("Welcomer");
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        String string = this.getWelcomeString();
        if (this.autoPos.getValue().booleanValue()) {
            ScaledResolution sr = new ScaledResolution(mc);
            this.xPos.setValue((sr.getScaledWidth() - ClickGui.CONTEXT.getRenderer().getTextWidth(string)) / 2);
            this.yPos.setValue(1);
        }
        ClickGui.CONTEXT.getRenderer().renderText(this.getWelcomeString(), ((Number)this.xPos.getValue()).floatValue(), ((Number)this.yPos.getValue()).floatValue(), HudColors.getTextColor(((Number)this.yPos.getValue()).intValue()), ClickGui.CONTEXT.getColorScheme().doesTextShadow());
    }

    String getWelcomeString() {
        return "Welcome to " + KamiMod.NAME_VERSION_COLORED + " " + ChatFormatting.RESET + Welcomer.mc.player.getName();
    }
}

