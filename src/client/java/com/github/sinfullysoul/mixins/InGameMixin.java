package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.github.sinfullysoul.api.IRenderable;
import com.github.sinfullysoul.api.ZoneBlockEntityRenderInterface;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.items.screens.BaseItemScreen;
import finalforeach.cosmicreach.world.Sky;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGame.class)
public class InGameMixin {
    @Shadow
    private static PerspectiveCamera rawWorldCamera;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lfinalforeach/cosmicreach/BlockSelection;render(Lcom/badlogic/gdx/graphics/Camera;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderPlayerZone(CallbackInfo ci, Zone playerZone, Sky sky) {
        for (BlockEntity m : ((ZoneBlockEntityRenderInterface) playerZone).getRenderableBlockEntities()) {
            if (m instanceof IRenderable renderable) {
                renderable.onRender(rawWorldCamera);
            }
        }
    }

    @Inject(method = "addBaseItemScreen", at=@At(value = "TAIL"))
    private void onShowScreen(BaseItemScreen screen, CallbackInfo ci){
        screen.onShow();
    }
}
