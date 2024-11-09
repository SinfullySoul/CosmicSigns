package com.github.sinfullysoul.mixins;

import com.github.sinfullysoul.ClientInitializer;
import finalforeach.cosmicreach.settings.Controls;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Controls.class)
public class ControlsMixin {
    @Inject(method = "forwardPressed", at = @At("HEAD"), cancellable = true)
    private static void disableForward(CallbackInfoReturnable<Float> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(0f);
    }
    @Inject(method = "backwardPressed", at = @At("HEAD"), cancellable = true)
    private static void disableBackward(CallbackInfoReturnable<Float> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(0f);
    }
    @Inject(method = "leftPressed", at = @At("HEAD"), cancellable = true)
    private static void disableLeft(CallbackInfoReturnable<Float> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(0f);
    }
    @Inject(method = "rightPressed", at = @At("HEAD"), cancellable = true)
    private static void disableRight(CallbackInfoReturnable<Float> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(0f);
    }
    @Inject(method = "jumpPressed", at = @At("HEAD"), cancellable = true)
    private static void disableJump(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "jumpJustPressed", at = @At("HEAD"), cancellable = true)
    private static void disableJumpJust(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "crouchPressed", at = @At("HEAD"), cancellable = true)
    private static void disableCrouch(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "sprintPressed", at = @At("HEAD"), cancellable = true)
    private static void disableSprint(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "pronePressed", at = @At("HEAD"), cancellable = true)
    private static void disableProne(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "inventoryPressed", at = @At("HEAD"), cancellable = true)
    private static void disableInventory(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "dropItemPressed", at = @At("HEAD"), cancellable = true)
    private static void disableDropItem(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) {
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "pickBlockPressed", at = @At("HEAD"), cancellable = true)
    private static void disablePickBlock(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "chatOpened", at = @At("HEAD"), cancellable = true)
    private static void disableChatOpened(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "cycleItemLeft", at = @At("HEAD"), cancellable = true)
    private static void disableCycleItemLeft(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "cycleItemRight", at = @At("HEAD"), cancellable = true)
    private static void disableCycleItemRight(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "attackBreakPressed", at = @At("HEAD"), cancellable = true)
    private static void disableAttackBreakPressed(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "usePlacePressed", at = @At("HEAD"), cancellable = true)
    private static void disableUsePlacePressed(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "attackBreakJustPressed", at = @At("HEAD"), cancellable = true)
    private static void disableAttackBreakJust(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "usePlaceJustPressed", at = @At("HEAD"), cancellable = true)
    private static void disableUsePlaceJust(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
    @Inject(method = "cycleSwapGroupItemJustPressed", at = @At("HEAD"), cancellable = true)
    private static void disableCycleSwapGroup(CallbackInfoReturnable<Boolean> cir){
        if(ClientInitializer.getDisableKeyboardInput()) cir.setReturnValue(false);
    }
}
