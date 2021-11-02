/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Text
 */
package tech.mmmax.kami.impl.features.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.hud.HudComponent;
import tech.mmmax.kami.api.management.FeatureManager;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.features.modules.client.HudColors;
import tech.mmmax.kami.impl.gui.ClickGui;

public class FeatureList
extends HudComponent {
    Value<String> alignment = new ValueBuilder().withDescriptor("Alignment").withValue("TopLeft").withModes("TopLeft", "BottomLeft", "TopRight", "BottomRight").register(this);

    public FeatureList() {
        super("Feature List");
    }

    @Override
    public void draw(RenderGameOverlayEvent.Text event) {
        super.draw(event);
        ArrayList<String> sorted = new ArrayList<String>();
        for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
            if (!feature.visible.getValue().booleanValue() || !feature.isEnabled()) continue;
            String displayName = feature.getDisplayName() + (!feature.getHudInfo().equals("") ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + feature.getHudInfo() + ChatFormatting.GRAY + "]" : "");
            sorted.add(displayName);
        }
        sorted.sort(Comparator.comparingInt(str -> {
            int o = ClickGui.CONTEXT.getRenderer().getTextWidth((String)str);
            return -o;
        }));
        int offset = 0;
        for (String string : sorted) {
            boolean top = this.alignment.getValue().contains("Top");
            if (this.alignment.getValue().contains("Left")) {
                ClickGui.CONTEXT.getRenderer().renderText(string, ((Number)this.xPos.getValue()).intValue(), ((Number)this.yPos.getValue()).intValue() + (top ? offset : -offset), HudColors.getTextColor(((Number)this.yPos.getValue()).intValue() + (top ? offset : -offset)), ClickGui.CONTEXT.getColorScheme().doesTextShadow());
                offset += ClickGui.CONTEXT.getRenderer().getTextHeight(string);
            }
            if (!this.alignment.getValue().contains("Right")) continue;
            ClickGui.CONTEXT.getRenderer().renderText(string, ((Number)this.xPos.getValue()).intValue() - ClickGui.CONTEXT.getRenderer().getTextWidth(string), ((Number)this.yPos.getValue()).intValue() + (top ? offset : -offset), HudColors.getTextColor(((Number)this.yPos.getValue()).intValue() + (top ? offset : -offset)), ClickGui.CONTEXT.getColorScheme().doesTextShadow());
            offset += ClickGui.CONTEXT.getRenderer().getTextHeight(string);
        }
    }
}

