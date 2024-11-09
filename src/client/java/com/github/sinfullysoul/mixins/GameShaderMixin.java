package com.github.sinfullysoul.mixins;

import com.github.sinfullysoul.visualstext.VisualTextShader;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameShader.class)
public class GameShaderMixin {

    @Inject(method = "initShaders()V", at = @At("TAIL")) // making it head should populate the mods assets with the replacement shaders
    static private void initTextShaders(CallbackInfo ci) {
        VisualTextShader.initTextShader();
    }
}
