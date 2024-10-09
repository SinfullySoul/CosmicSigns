package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.github.sinfullysoul.api.IRenderable;
import com.github.sinfullysoul.entities.TextModelInstance;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Region;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Zone.class)
public abstract class ZoneMixin implements IRenderable {

    @Shadow public abstract Region[] getRegions();

    @Override
    public void onRender(Camera camera) {
        Region[] regions = this.getRegions();
        for(Region region : regions){
            if(region == null) continue;
            Array<Chunk> chunkArray = region.getChunks();
            for(Chunk chunk : chunkArray) {
                if(chunk == null) continue;
                if (chunk instanceof IRenderable renderable) {
                    renderable.onRender(camera);
                }
            }
        }
    }


    public Array<TextModelInstance> allTextModels;

    public void addTextModel(TextModelInstance text) {
        this.allTextModels.add(text);
    }
    public void removeTextModel(TextModelInstance text) {
        this.allTextModels.removeValue(text, true);
    }
}
