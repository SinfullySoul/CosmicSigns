package com.github.sinfullysoul;

import com.github.puzzle.core.PuzzleRegistries;
import com.github.puzzle.game.block.DataModBlock;
import com.github.puzzle.game.events.OnRegisterBlockEvent;
import com.github.puzzle.game.ui.screens.BasePuzzleScreen;
import com.github.puzzle.loader.entrypoint.interfaces.ModInitializer;
import com.github.sinfullysoul.block_entities.SignBlockEntity;
import com.github.sinfullysoul.screens.SignScreen;
import finalforeach.cosmicreach.util.Identifier;
import org.greenrobot.eventbus.Subscribe;

public class CosmicSigns implements ModInitializer {

    private static Boolean disablekeyboard = false;

    @Override
    public void onInit() {
        PuzzleRegistries.EVENT_BUS.register(this);

        SignBlockEntity.register();
        BasePuzzleScreen.registerScreen(SignBlockEntity.id, new SignScreen());
    }

    @Subscribe
    public void onEvent(OnRegisterBlockEvent event) {
        event.registerBlock(() -> new DataModBlock(Identifier.of(Constants.MOD_ID, "sign.json")));
    }

    public static void setDisableKeyboardInput(boolean state){
        disablekeyboard = state;
    }

    public static boolean getDisableKeyboardInput() {
        return disablekeyboard;
    }
}
