package com.github.sinfullysoul;

import com.github.puzzle.core.loader.launch.provider.mod.entrypoint.impls.ClientModInitializer;
import com.github.puzzle.game.ui.screens.handlers.GenericClientScreenHandler;
import com.github.sinfullysoul.blockentities.SignBlockEntity;
import com.github.sinfullysoul.screens.SignScreen;
import finalforeach.cosmicreach.BlockEntityScreenInfo;
import finalforeach.cosmicreach.items.screens.BaseItemScreen;

import java.util.function.Function;

public class ClientInitializer implements ClientModInitializer {

    private static Boolean disablekeyboard = false;

    @Override
    public void onInit() {
        //PuzzleRegistries.EVENT_BUS.register(this);

        SignBlockEntity.register();
        GenericClientScreenHandler.register(SignBlockEntity.id, new Function<BlockEntityScreenInfo, BaseItemScreen>() {
            @Override
            public BaseItemScreen apply(BlockEntityScreenInfo blockEntityScreenInfo) {
                SignScreen screen = new SignScreen((SignBlockEntity) blockEntityScreenInfo.blockEntity());
                return screen;
            }
        });
    }

    public static void setDisableKeyboardInput(boolean state){
        disablekeyboard = state;
    }

    public static boolean getDisableKeyboardInput() {
        return disablekeyboard;
    }
}
