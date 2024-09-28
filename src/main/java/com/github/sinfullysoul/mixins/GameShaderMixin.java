package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.github.sinfullysoul.Constants;
import com.github.sinfullysoul.block_entities.SignBlockEntity;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameShader.class)
public abstract class GameShaderMixin {

    @Inject(method = "initShaders", at = @At("TAIL"))
    private static void createSignShader(CallbackInfo ci) {
        SignBlockEntity.initSignShader();
    }
}
