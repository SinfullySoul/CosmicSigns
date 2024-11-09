package com.github.sinfullysoul;

import com.github.puzzle.core.loader.launch.Piece;
import com.github.puzzle.core.loader.meta.EnvType;
import com.github.puzzle.core.loader.provider.mod.entrypoint.impls.ModInitializer;
import com.github.puzzle.game.PuzzleRegistries;
import com.github.puzzle.game.block.DataModBlock;
import com.github.puzzle.game.events.OnRegisterBlockEvent;
import com.github.puzzle.game.ui.screens.handlers.GenericServerScreenHandler;
import com.github.sinfullysoul.blockentities.ServerSignBlockEntity;
import com.github.sinfullysoul.network.packets.SignsEntityPacket;
import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.orbit.EventHandler;

public class CosmicSigns implements ModInitializer {

    @Override
    public void onInit() {
        PuzzleRegistries.EVENT_BUS.subscribe(this);
        GamePacket.registerPacket(SignsEntityPacket.class);

        if(Piece.getSide() == EnvType.SERVER) {
            ServerSignBlockEntity.register();
            GenericServerScreenHandler.register(ServerSignBlockEntity.id);
        }
    }

    @EventHandler
    public void onEvent(OnRegisterBlockEvent event) {
        event.registerBlock(() -> new DataModBlock(Identifier.of(Constants.MOD_ID, "sign.json")));
    }
}
