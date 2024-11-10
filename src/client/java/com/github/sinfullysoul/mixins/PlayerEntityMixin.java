package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.github.sinfullysoul.visualstext.TextModelInstance;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    @Shadow public transient Player player;
    TextModelInstance nameTag;

    public PlayerEntityMixin(String entityTypeId) {
        super(entityTypeId);
    }

    private void spawnNameTag() {
        nameTag = new TextModelInstance(this.player.getZone(), this.getPosition());
        nameTag.setFontSize(24f);
        nameTag.setColor(Color.WHITE);
        nameTag.isGlowing(true);
        nameTag.buildTextMesh(new String[]{this.player.getAccount().getDisplayName()}, 0,0f,0, true);
    }

    @Override
    public void render(Camera worldCamera) {
        if (!GameSingletons.isClient || GameSingletons.client().getLocalPlayer() != this.player) {
            super.render(worldCamera);
            if (this.nameTag != null) {
                Vector3 pos = this.getPosition();
                this.nameTag.rotationY = (float) Math.toDegrees(Math.atan2(worldCamera.position.x - pos.x, worldCamera.position.z - pos.z));
                this.nameTag.position.set(pos.x,pos.y + 1.2f, pos.z);
                this.nameTag.update();
                this.nameTag.render(worldCamera);
            } else {
                spawnNameTag();
            }
        }
    }
}
