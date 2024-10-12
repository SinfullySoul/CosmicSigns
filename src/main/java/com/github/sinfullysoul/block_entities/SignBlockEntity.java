package com.github.sinfullysoul.block_entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.puzzle.game.ui.font.CosmicReachFont;
import com.github.sinfullysoul.Constants;
import com.github.sinfullysoul.api.IRenderable;
import com.github.sinfullysoul.block_entities.models.TextModelInstance;
import com.github.sinfullysoul.mixins.BlockEntityInterface;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.Threads;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.io.CRBinDeserializer;
import finalforeach.cosmicreach.io.CRBinSerializer;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.Zone;

import static com.github.puzzle.game.ui.font.CosmicReachFont.createCosmicReachFont;

public class SignBlockEntity extends BlockEntity implements IRenderable {

    public final static Identifier id = Identifier.of(Constants.MOD_ID, "sign_entity");
    private static Label.LabelStyle signbasefont;

    public boolean runTexture = true;
    public String[] texts = new String[]{"", "", ""};
    public float textSize = 8f;
    public Color fontcolor = new Color(Color.BLACK);
    private int dir;
    private TextModelInstance textModel;


    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(id.toString(), (blockState, zone, x, y, z) -> {
            return new SignBlockEntity(zone, x, y, z);
        });
    }
    public void onTick() { //used to update the text tint so its not glowing todo find a better way to do this
        super.onTick();
        textModel.updateLight();
    }

    @Override
    public void onInteract(Player player, Zone zone) {
        GameSingletons.openBlockEntityScreen(player, zone, this);
    }

    public SignBlockEntity(Zone zone, int globalX, int globalY, int globalZ) {
        super(zone, globalX, globalY, globalZ);
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
        this.texts = deserial.readStringArray("lines");
        this.textSize = deserial.readFloat("textsize", 14);
        this.fontcolor = new Color();
        Color.rgba8888ToColor(this.fontcolor, deserial.readInt("textcolor", Color.rgba8888(Color.BLACK)));
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
        serial.writeStringArray("lines", texts);
        serial.writeFloat("textsize", this.textSize);
        serial.writeInt("textcolor", Color.rgba8888(this.fontcolor));
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

    }

    @Override
    public String getBlockEntityId() {
        return id.toString();
    }


private void buildMesh() {
    if (this.textModel == null) {
        this.textModel = new TextModelInstance( ((BlockEntityInterface)this).getZone(), new Vector3(this.getGlobalX(),this.getGlobalY(), this.getGlobalZ()), new Color(Color.BLUE ),22f - this.textSize   );
    }
    float rotation ;
    if (dir == -90 ) {
        rotation =90;
    } else if(dir == 90) {
        rotation = 270;
    } else {
        rotation = dir;
    }

    this.textModel.rotationY = rotation;
    this.textModel.update();
    textModel.setTextColor(this.fontcolor);



    float invertedTextSize =22f -this.textSize ; //in textModel smaller numbers result in bigger fonts so im inverting them
    this.textModel.buildTextMesh(this.texts, 0f,0.1f,0.075f, invertedTextSize, true);
}




    @Override
    public void onRender(Camera camera) {
        if(camera == null) return;

        if (runTexture) {
            runTexture = false;
            //generateTextMesh();
            buildMesh();
        }

        //Gdx.gl.glDisable(GL20.GL_CULL_FACE);

        textModel.render(camera);
        //Gdx.gl.glEnable(GL20.GL_CULL_FACE);
    }



    @Override
    public void onRemove() {
        super.onRemove();
        ((ZoneBlockEntityRenderInterface) ((BlockEntityInterface)this).getZone()).removeRenderableBlockEntity(this);
        textModel.dispose();
    }



    static {
        Threads.runOnMainThread(() -> {
            BitmapFont font = createCosmicReachFont();
            font.getData().setScale(14);
            signbasefont = new Label.LabelStyle(font, new Color(Color.BLACK));
        });
    }



    public int isTextMaxSize( String newString) { // just easier to recalculate the length each time definitly possible to keep track of the length as the string grows and shrinks
        int stringPixelLength =0;
        for(int x = 0; x < newString.length(); x++) {
            stringPixelLength+= CosmicReachFont.FONT.getData().getGlyph(newString.charAt(x)).xadvance;
            float MAX_TEXT_LENGTH = 11f;
            if (stringPixelLength / (22F -this.textSize) > MAX_TEXT_LENGTH) {
                Constants.LOGGER.info("String {} , pixelLength {}, Out {}",newString, stringPixelLength, x);
                return x; //return the index of the character that exceeds the font max length
            }
        }
        return -1; //return -1 if it doesnt exceed the limit

    }
}
