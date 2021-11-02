/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package tech.mmmax.kami.impl.features.modules.render;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.event.RenderEntityEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Chams
extends Module {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    Value<Boolean> players = new ValueBuilder().withDescriptor("Living").withValue(false).register(this);
    Value<Color> livingFill = new ValueBuilder().withDescriptor("Living Fill").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Color> livingLine = new ValueBuilder().withDescriptor("Living Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Boolean> livingGlint = new ValueBuilder().withDescriptor("Living Glint").withValue(true).register(this);
    Value<Color> livingGlintColor = new ValueBuilder().withDescriptor("Living Glint Color").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Boolean> crystals = new ValueBuilder().withDescriptor("Crystals").withValue(true).register(this);
    Value<Number> crystalRotateSpeed = new ValueBuilder().withDescriptor("Rotate Speed").withValue(1).withRange(0, 10).register(this);
    Value<Number> crystalScale = new ValueBuilder().withDescriptor("Crystal Scale").withValue(1).withRange(0, 3).register(this);
    Value<Color> crystalFill = new ValueBuilder().withDescriptor("Crystal Fill").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Color> crystalLine = new ValueBuilder().withDescriptor("Crystal Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Boolean> crystalGlint = new ValueBuilder().withDescriptor("Crystal Glint").withValue(true).register(this);
    Value<Color> crystalGlintColor = new ValueBuilder().withDescriptor("Crystal Glint Color").withValue(new Color(255, 255, 255, 255)).register(this);
    Value<Number> lineWidth = new ValueBuilder().withDescriptor("Line Width").withValue(Float.valueOf(2.0f)).withRange((Float)0.1, (Float)5).register(this);
    Value<Number> lineWidthInterp = new ValueBuilder().withDescriptor("Line Width Interp").withValue(Float.valueOf(5.0f)).withRange((Float)0.1, (Float)15).register(this);
    Value<Boolean> customBlendFunc = new ValueBuilder().withDescriptor("Blend Func").withValue(true).register(this);

    public Chams() {
        super("Chams", Feature.Category.Render);
    }

    @SubscribeEvent
    public void renderEntity(RenderEntityEvent event) {
        boolean nullCheck;
        boolean bl = nullCheck = Chams.mc.player == null || Chams.mc.world == null || event.entityIn == null;
        if (event.entityIn instanceof EntityEnderCrystal && !this.crystals.getValue().booleanValue()) {
            return;
        }
        if (event.entityIn instanceof EntityLivingBase && !this.players.getValue().booleanValue()) {
            return;
        }
        RenderUtil.prepare();
        GL11.glPushAttrib((int)1048575);
        if (this.customBlendFunc.getValue().booleanValue()) {
            GL11.glBlendFunc((int)770, (int)32772);
        }
        GL11.glEnable((int)2881);
        GL11.glEnable((int)2848);
        Color line = event.entityIn instanceof EntityLivingBase ? this.livingLine.getValue() : this.crystalLine.getValue();
        Color fill = event.entityIn instanceof EntityLivingBase ? this.livingFill.getValue() : this.crystalFill.getValue();
        boolean texture = event.entityIn instanceof EntityLivingBase ? this.livingGlint.getValue().booleanValue() : this.crystalGlint.getValue().booleanValue();
        Color textureColor = event.entityIn instanceof EntityLivingBase ? this.livingGlintColor.getValue() : this.crystalGlintColor.getValue();
        float limbSwingAmt = event.entityIn instanceof EntityEnderCrystal ? event.limbSwingAmount * this.crystalRotateSpeed.getValue().floatValue() : event.limbSwingAmount;
        float scale = event.entityIn instanceof EntityEnderCrystal ? this.crystalScale.getValue().floatValue() : event.scale;
        GlStateManager.glLineWidth((float)(nullCheck ? this.lineWidth.getValue().floatValue() : RenderUtil.getInterpolatedLinWid(Chams.mc.player.getDistance(event.entityIn) + 1.0f, this.lineWidth.getValue().floatValue(), this.lineWidthInterp.getValue().floatValue())));
        GlStateManager.disableAlpha();
        if (texture) {
            mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
            GL11.glTexCoord3d((double)1.0, (double)1.0, (double)1.0);
            GL11.glEnable((int)3553);
            GL11.glBlendFunc((int)768, (int)771);
            ColorUtil.glColor(textureColor);
            event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
            if (this.customBlendFunc.getValue().booleanValue()) {
                GL11.glBlendFunc((int)770, (int)32772);
            } else {
                GL11.glBlendFunc((int)770, (int)771);
            }
        }
        ColorUtil.glColor(fill);
        GL11.glDisable((int)3553);
        event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
        GL11.glPolygonMode((int)1032, (int)6913);
        ColorUtil.glColor(line);
        event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
        GL11.glPolygonMode((int)1032, (int)6914);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.popAttrib();
        RenderUtil.release();
        event.setCanceled(true);
    }
}

