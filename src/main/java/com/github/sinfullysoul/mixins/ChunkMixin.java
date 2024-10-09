package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.graphics.Camera;
import com.github.sinfullysoul.Constants;
import com.github.sinfullysoul.api.IRenderable;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.util.IPoint3DMap;
import finalforeach.cosmicreach.world.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Chunk.class)
public class ChunkMixin implements IRenderable {

    @Shadow private IPoint3DMap<BlockEntity> blockEntities;

    @Override
    public void onRender(Camera camera) {
        if(this.blockEntities != null) {
            for (int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
                for (int y = 0; y < Chunk.CHUNK_WIDTH; y++) {
                    for (int z = 0; z < Chunk.CHUNK_WIDTH; z++) {
                        if (blockEntities.get(x, y, z) instanceof IRenderable renderable) {
                            renderable.onRender(camera);
                        }
                    }
                }
            }
        }
    }
}
