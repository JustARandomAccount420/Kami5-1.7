/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package tech.mmmax.kami.mixin.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.mmmax.kami.impl.features.modules.render.NoRender;

@Mixin(value={EntityPlayer.class})
public class MixinEntityPlayer {
    @Inject(method={"isEntityInsideOpaqueBlock"}, at={@At(value="HEAD")}, cancellable=true)
    public void isEntityInsideOpaqueBlockHook(CallbackInfoReturnable<Boolean> info) {
        if (NoRender.INSTANCE.isEnabled()) {
            info.setReturnValue(false);
        }
    }
}

