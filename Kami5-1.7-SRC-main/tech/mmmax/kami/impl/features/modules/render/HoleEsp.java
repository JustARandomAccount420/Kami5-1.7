/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  org.lwjgl.opengl.GL11
 */
package tech.mmmax.kami.impl.features.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.utils.world.HoleUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.gui.ClickGui;

public class HoleEsp
extends Module {
    Value<String> glowMode = new ValueBuilder().withDescriptor("Glow Mode").withValue("Fade").withModes("Fade", "Fog").register(this);
    Value<Number> lineWidth = new ValueBuilder().withDescriptor("Line Width").withValue(Float.valueOf(2.0f)).withRange((Float)0.1, (Float)5).register(this);
    Value<Number> height = new ValueBuilder().withDescriptor("Height").withValue(1).withRange(0, 2).register(this);
    Value<Number> range = new ValueBuilder().withDescriptor("Range").withValue(5.0).withRange(1.0, 30.0).register(this);
    Value<Boolean> doubles = new ValueBuilder().withDescriptor("Doubles").withValue(true).register(this);
    Value<Color> bedrockFill = new ValueBuilder().withDescriptor("Bedrock Fill").withValue(new Color(0, 255, 0, 100)).register(this);
    Value<Color> bedrockLine = new ValueBuilder().withDescriptor("Bedrock Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Color> obbyFill = new ValueBuilder().withDescriptor("Obby Fill").withValue(new Color(0, 255, 218, 100)).register(this);
    Value<Color> obbyLine = new ValueBuilder().withDescriptor("Obby Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Color> doubleFill = new ValueBuilder().withDescriptor("Double Fill").withValue(new Color(255, 0, 11, 100)).register(this);
    Value<Color> doubleLine = new ValueBuilder().withDescriptor("Double Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Color> bedrockFill2 = new ValueBuilder().withDescriptor("Bedrock Fill2").withValue(new Color(0, 255, 0, 0)).register(this);
    Value<Color> bedrockLine2 = new ValueBuilder().withDescriptor("Bedrock Line2").withValue(new Color(255, 255, 255, 0)).register(this);
    Value<Color> obbyFill2 = new ValueBuilder().withDescriptor("Obby Fill2").withValue(new Color(0, 255, 218, 0)).register(this);
    Value<Color> obbyLine2 = new ValueBuilder().withDescriptor("Obby Line2").withValue(new Color(255, 255, 255, 0)).register(this);
    Value<Color> doubleFill2 = new ValueBuilder().withDescriptor("Double Fill2").withValue(new Color(255, 0, 11, 0)).register(this);
    Value<Color> doubleLine2 = new ValueBuilder().withDescriptor("Double Line2").withValue(new Color(255, 255, 255, 0)).register(this);
    ExecutorService service = Executors.newCachedThreadPool();
    volatile List<HoleUtils.Hole> holes = new ArrayList<HoleUtils.Hole>();

    public HoleEsp() {
        super("Hole ESP", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        this.service.submit(() -> {
            this.holes = HoleUtils.getHoles(this.range.getValue().floatValue(), HoleEsp.mc.player.getPosition(), this.doubles.getValue());
        });
    }

    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {
        for (HoleUtils.Hole hole : this.holes) {
            Color outlineColor2;
            Color outlineColor;
            Color fillColor2;
            Color fillColor;
            Color mainOutline = ClickGui.CONTEXT.getColorScheme().getOutlineColor();
            GL11.glLineWidth((float)RenderUtil.getInterpolatedLinWid((float)HoleEsp.mc.player.getDistance((double)hole.pos1.getX(), (double)hole.pos1.getY(), (double)hole.pos1.getZ()), this.lineWidth.getValue().floatValue(), this.lineWidth.getValue().floatValue()));
            AxisAlignedBB holeBB = hole.doubleHole ? new AxisAlignedBB((double)hole.pos1.getX(), (double)hole.pos1.getY(), (double)hole.pos1.getZ(), (double)(hole.pos2.getX() + 1), (double)(hole.pos2.getY() + 1), (double)(hole.pos2.getZ() + 1)) : new AxisAlignedBB(hole.pos1);
            holeBB = new AxisAlignedBB(holeBB.minX, holeBB.minY, holeBB.minZ, holeBB.maxX, holeBB.minY + this.height.getValue().doubleValue(), holeBB.maxZ);
            Color color = hole.bedrock ? this.bedrockFill.getValue() : (fillColor = hole.doubleHole ? this.doubleFill.getValue() : this.obbyFill.getValue());
            Color color2 = hole.bedrock ? this.bedrockFill2.getValue() : (fillColor2 = hole.doubleHole ? this.doubleFill2.getValue() : this.obbyFill2.getValue());
            Color color3 = hole.bedrock ? this.bedrockLine.getValue() : (outlineColor = hole.doubleHole ? this.doubleLine.getValue() : this.obbyLine.getValue());
            Color color4 = hole.bedrock ? this.bedrockLine2.getValue() : (outlineColor2 = hole.doubleHole ? this.doubleLine2.getValue() : this.obbyLine2.getValue());
            if (this.glowMode.getValue().equals("Fade")) {
                RenderUtil.renderBB(7, holeBB, fillColor, fillColor2);
            } else if (this.glowMode.getValue().equals("Fog")) {
                RenderUtil.renderBBFog(holeBB, fillColor, fillColor2);
            }
            RenderUtil.renderBB(3, holeBB, outlineColor, outlineColor2);
        }
    }
}

