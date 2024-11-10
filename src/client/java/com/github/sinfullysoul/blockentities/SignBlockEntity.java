package com.github.sinfullysoul.blockentities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.game.ui.font.CosmicReachFont;
import com.github.sinfullysoul.Constants;
import com.github.sinfullysoul.api.IRenderable;
import com.github.sinfullysoul.api.ISignBlockEntity;
import com.github.sinfullysoul.api.ZoneBlockEntityRenderInterface;
import com.github.sinfullysoul.mixins.BlockEntityInterface;
import com.github.sinfullysoul.visualstext.TextModelInstance;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.Threads;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.Zone;

public class SignBlockEntity extends BlockEntity implements IRenderable, ISignBlockEntity {

    public final static Identifier id = Identifier.of(Constants.MOD_ID, "sign_entity");

    public boolean runTexture = true;

    private String[] texts = new String[]{"", "", ""};
    private int dir;
    private TextModelInstance textModel;

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(id.toString(), (blockState, zone, x, y, z) -> {
            return new SignBlockEntity(zone, x, y, z);
        });
    }

    public SignBlockEntity(Zone zone, int globalX, int globalY, int globalZ) {
        super(zone, globalX, globalY, globalZ);
    }

    @Override
    public void onInteract(Player player, Zone zone) {
        GameSingletons.openBlockEntityScreen(player, zone, this);
    }

    public void setTextToLine(int index, String text) {
        if(index >= this.texts.length) throw new RuntimeException("The provide index is greater than total amount of lines!");
        this.texts[index] = text;
    }

    public String[] getText() {
        return this.texts;
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        if(this.textModel == null) {
            this.textModel = new TextModelInstance(((BlockEntityInterface)this).getZone(),
                    new Vector3(this.getGlobalX(), this.getGlobalY(), this.getGlobalZ()).add(0.5f, 0.6f, 0.5f));
            this.textModel.isGlowing(false);
            this.textModel.hasBorder(true);
        } else {
            this.runTexture = true;
        }
        this.texts = deserial.readStringArray("lines");
        this.textModel.setFontSize(deserial.readFloat("textsize", 8f));
        Color.rgba8888ToColor(this.textModel.getColor(), deserial.readInt("textcolor", Color.rgba8888(Color.BLACK)));
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
        serial.writeStringArray("lines", texts);
        serial.writeFloat("textsize", this.textModel.getFontSize());
        serial.writeInt("textcolor", Color.rgba8888(this.textModel.getColor()));
    }

    @Override
    public void onUnload() {
        this.loaded = false;
        ((ZoneBlockEntityRenderInterface) ((BlockEntityInterface)this).getZone()).removeRenderableBlockEntity(this);
        Gdx.app.postRunnable(() -> {
            this.textModel.dispose();
        }) ;
    }

    @Override
    public void onCreate(BlockState blockState) {
        super.onCreate(blockState);
        dir = blockState.rotXZ;
        dir -= 90;
        ((ZoneBlockEntityRenderInterface) ((BlockEntityInterface)this).getZone()).addRenderableBlockEntity(this);
        if(this.textModel == null) this.textModel =
                new TextModelInstance(((BlockEntityInterface)this).getZone(),
                        new Vector3(this.getGlobalX(), this.getGlobalY(), this.getGlobalZ()).add(0.5f, 0.6f, 0.5f));
    }

    @Override
    public String getBlockEntityId() {
        return id.toString();
    }

    private void buildMesh() {
        if(this.textModel == null) return;

        float rotation ;
        if (dir == -90 ) {
            rotation =90;
        } else if(dir == 90) {
            rotation = 270;
        } else {
            rotation = dir;
        }

        this.textModel.setRotationY(rotation);
        this.textModel.update();
        //in textModel smaller numbers result in bigger fonts so im inverting them
        this.textModel.buildTextMesh(this.texts, 0f,0f,0.075f, true);
    }

    public void setFontSize(float size){
        this.textModel.setFontSize(size);
    }

    public float getFontSize() {
        return this.textModel.getFontSize();
    }

    public void setTextColor(Color color) {
        this.textModel.setColor(color);
    }

    @Override
    public void onRender(Camera camera) {
        if (runTexture) {
            runTexture = false;
            buildMesh();
        }
        textModel.render(camera);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        Threads.runOnMainThread(()->{
            ((ZoneBlockEntityRenderInterface) ((BlockEntityInterface)this).getZone()).removeRenderableBlockEntity(this);
            textModel.dispose();
        });
    }


    public int isTextMaxSize( String newString) { // just easier to recalculate the length each time definitly possible to keep track of the length as the string grows and shrinks
        int stringPixelLength = 0;
        for(int x = 0; x < newString.length(); x++) {
            stringPixelLength+= CosmicReachFont.FONT.getData().getGlyph(newString.charAt(x)).xadvance;
            float MAX_TEXT_LENGTH = 11f;
            if (stringPixelLength / (22F -this.textModel.getFontSize()) > MAX_TEXT_LENGTH) {
                Constants.LOGGER.info("String {} , pixelLength {}, Out {}",newString, stringPixelLength, x);
                return x; //return the index of the character that exceeds the font max length
            }
        }
        return -1; //return -1 if it doesnt exceed the limit
    }
}
