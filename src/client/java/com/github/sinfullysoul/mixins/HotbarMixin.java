package com.github.sinfullysoul.mixins;

import com.github.sinfullysoul.ClientInitializer;
import finalforeach.cosmicreach.items.Hotbar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hotbar.class)
public class HotbarMixin {

    @Inject(method = "keyDown", at = @At("HEAD"), cancellable = true)
    private void disableKeyDown(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
}
