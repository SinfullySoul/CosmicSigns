package com.github.sinfullysoul.block_entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.github.puzzle.game.ui.font.CosmicReachFont;
import com.github.puzzle.game.ui.font.FontTexture;
import com.github.sinfullysoul.Constants;
import com.github.sinfullysoul.api.IRenderable;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.Threads;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.io.CRBinDeserializer;
import finalforeach.cosmicreach.io.CRBinSerializer;
import finalforeach.cosmicreach.ui.FontRenderer;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.Zone;

import java.util.ArrayList;

import static com.github.puzzle.game.ui.font.CosmicReachFont.createCosmicReachFont;

public class SignBlockEntity extends BlockEntity implements IRenderable {

    public final static Identifier id = Identifier.of(Constants.MOD_ID, "sign_entity");
    private static Label.LabelStyle signbasefont;

    public boolean runTexture = true;
    public String[] texts = new String[]{"", "", ""};
    public float textSize = 14f;
    public Color fontcolor = Color.BLACK;

    private Texture texture;
    private static ShaderProgram shader = null;
    private Matrix4 modelMatrix;
    private Mesh mesh;
    private FrameBuffer fbo;
    private int dir;
    private int flip = 0;


    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(id.toString(), (blockState, zone, x, y, z) -> {
            return new SignBlockEntity(zone, x, y, z);
        });
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
        if (mesh != null) {
            Gdx.app.postRunnable(() -> {
                mesh.dispose();
                //texture.dispose();
                //fbo.dispose();
            });
        }
    }

    @Override
    public void onCreate(BlockState blockState) {
        super.onCreate(blockState);
        dir = blockState.rotXZ;
        dir -= 90;
        Gdx.app.postRunnable(this::generateTextMesh);

    }

    @Override
    public String getBlockEntityId() {
        return id.toString();
    }
private Vector2 getCharUv(char c) {

        Vector2 tmp = new Vector2();


    tmp.x = (float)(c  % 16); // i think this just has to be hardcoded
    tmp.y = (float)(c / 16) ;

    return tmp;
}
 float CHAR_SIZE_X;
    float CHAR_SIZE_Y;
    int line;
private void addCharacterQuad(FloatArray verts, ShortArray indices, char c, int pos) {


    float u = CHAR_SIZE_X * (float)(c % 16); // i think this just has to be hardcoded
    float v = CHAR_SIZE_Y * (float)(c / 16);

    float OFFSET_X = pos * CHAR_SIZE_X * 30 ; //this has to be the size of the char * scale i think


    float x = -0.5f + OFFSET_X;
    float y = -0.5f + 0f; //TODO add line offset
    float z = 0.075f;
    Constants.LOGGER.info("QUAD X POS {}",x);

    verts.add( x); // x1
    verts.add( y); // y1
    verts.add( z);
    verts.add( u); // u1
    verts.add( v + CHAR_SIZE_Y); // v1 //done like this because the image is flipped

    verts.add( x + 1f); // x2
    verts.add( y); // y2
    verts.add( z);
    verts.add( u + CHAR_SIZE_X); // u2
    verts.add( v + CHAR_SIZE_Y); // v2

    verts.add( x + 1f); // x3
    verts.add( y + 1f); // y3
    verts.add( z);
    verts.add( u + CHAR_SIZE_X); // u3
    verts.add( v ); // v3

    verts.add( x); // x4
    verts.add( y + 1f); // y4
    verts.add( z);
    verts.add( u ); // u4
    verts.add( v ); // v4

    short offset = (short) (pos * 4);
    indices.add(offset);
    indices.add((short) (1 + offset)); //indices for each quad is the start idx in array + 0 1 2 2 3 0 for the indices
    indices.add((short) (2 + offset));
    indices.add((short) (2 + offset));
    indices.add((short) (3 + offset));
    indices.add(offset);

}
private void createTextLineMesh() {

    String line;
    line = texts[0];
    int length = line.length();
    if (length == 0) {
        mesh = null;
        return;
    }
    FloatArray verts = new FloatArray( length * 4 * 5); //character length * vertexes * vertex attributes
    ShortArray indicies = new ShortArray(length * 6);


    for(int i = 0; i < length; i++ ) {
        addCharacterQuad(verts, indicies, line.charAt(i),i);

    }

    mesh = new Mesh(false, verts.size, indicies.size,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords")
    );

    mesh.setVertices(verts.items);


    mesh.setIndices(indicies.items);
    //Constants.LOGGER.info("NEW {}", verts.items);

}
    private void generateTextMesh() {
        int gx = this.getGlobalX();
        int gy = this.getGlobalY();
        int gz = this.getGlobalZ();

        float[] verts = new float[20];

        int i = 0;
         float SIZE_X = 16f / CosmicReachFont.FONT.getRegion().getRegionWidth();
         CHAR_SIZE_X = SIZE_X;
         float SIZE_Y = 16f / CosmicReachFont.FONT.getRegion().getRegionHeight();
         CHAR_SIZE_Y = SIZE_Y;
         //SIZE_Y = 1.0f;
         //SIZE_X = 1.0f;
        Vector2 uv = getCharUv('h');
        //uv.x = 0f;
        //uv.y = 0f;
        uv.x = uv.x * SIZE_X;
        uv.y = uv.y * SIZE_Y;
        //uv.y = 1 - uv.y;
        Constants.LOGGER.info(uv);
        Constants.LOGGER.info("{} SIZE{}", SIZE_X, SIZE_Y);
        short[] indices;
        modelMatrix = new Matrix4().idt();
        float rotation ;
        if (dir == -90 ) {
            rotation =90;
        } else if(dir == 90) {
            rotation = 270;
        } else {
            rotation = dir;
        }

        modelMatrix.rotate(new Vector3(0,1,0), rotation);
        float FONT_SCALE = 0.1f;
        modelMatrix.scale(FONT_SCALE,FONT_SCALE,1.0f);
        modelMatrix.trn(gx + 0.5f,gy + 0.5f,gz + 0.5f); // add 0.5f to center on the block

        Vector3 pos = new Vector3(1,1,1).mul(modelMatrix); //model matrix time vertex position
        Constants.LOGGER.info("ROTATION {}", dir);


        Constants.LOGGER.info(modelMatrix);

        Constants.LOGGER.info(pos);
        indices = new short[]{0, 1, 2, 2, 3, 0};
        float X_OFFSET = 0.0f;
        float Y_OFFSET = 0.5f; //offset is affected by the scale in model Matrix
       float x = -0.5f + X_OFFSET;
        float y = -0.5f + Y_OFFSET;
        float z = 0.075f; //TODO maybe find a better way to do this so there isnt z fighting but not to far off the sign

        verts[i++] = x; // x1
        verts[i++] = y; // y1
        verts[i++] = z;
        verts[i++] = uv.x; // u1
        verts[i++] = uv.y + SIZE_Y; // v1

        verts[i++] = x + 1f; // x2
        verts[i++] = y; // y2
        verts[i++] = z;
        verts[i++] = uv.x + SIZE_X; // u2
        verts[i++] = uv.y + SIZE_Y; // v2

        verts[i++] = x + 1f; // x3
        verts[i++] = y + 1f; // y3
        verts[i++] = z;
        verts[i++] = uv.x + SIZE_X; // u3
        verts[i++] = uv.y ; // v3

        verts[i++] = x; // x4
        verts[i++] = y + 1f; // y4
        verts[i++] = z;
        verts[i++] = uv.x ; // u4
        verts[i++] = uv.y ; // v4


        Constants.LOGGER.info("OLD {}", verts);
        Constants.LOGGER.info("MESH AT " + x + " " + y + " " + z);
        mesh = new Mesh(false, 4, 6,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords")
        );
        mesh.setVertices(verts);


        mesh.setIndices(indices);
        createTextLineMesh();
        TextureRegion tr = CosmicReachFont.FONT.getRegion();
        texture = tr.getTexture();

    }



    @Override
    public void onRender(Camera camera) {
        if(camera == null) return;
        if (runTexture) {
            runTexture = false;
            generateTextMesh();
        }
        if(mesh == null) {
            return;
            //generateTextMesh();
        }
        Matrix4 tmp = new Matrix4().idt();
       // if(runTexture) texture = buildTexture();
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        shader.begin();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        shader.setUniformMatrix("u_modelMatrix", modelMatrix);
        shader.setUniformi("u_flipX", 0);
        texture.bind(0);
        shader.setUniformi("u_texture", 0);
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
    }



    @Override
    public void onRemove() {
        super.onRemove();
        mesh.dispose();
        //texture.dispose();
        //fbo.dispose();
    }

    public Texture buildTexture() {
        OrthographicCamera cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.up.set(0.0F, 1.0F, 0.0F);
        cam.direction.set(0.0F, 0.0F, 1.0F);
        ExtendViewport viewport = new ExtendViewport(1000, 1000, cam);
        viewport.apply(true);
        Stage stage = new Stage(viewport, InGame.batch);
        Batch batch = stage.getBatch();
        stage.addActor(getActors());
        TextureRegion fboRegion = new TextureRegion(fbo.getColorBufferTexture(), 0, 0, fbo.getWidth(), fbo.getHeight());
        fbo.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        //Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setBlendFunction(-1, -1);
        Gdx.gl20.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
        stage.draw();
        fbo.end();
        batch.setProjectionMatrix(InGame.IN_GAME.getWorldCamera().combined);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        InGame.currentGameState.newUiViewport.apply(false);
        stage.dispose();
        runTexture = false;
        return fboRegion.getTexture();
    }

    private Actor getActors() {
        Table base = new Table();
        base.setFillParent(true);

        Label.LabelStyle signfont = new Label.LabelStyle(signbasefont);
        signfont.font.getData().setScale(textSize);
        signfont.fontColor = fontcolor;

        Label label1 = new Label(texts[0], signfont);
        Label label2 = new Label(texts[1], signfont);;
        Label label3 = new Label(texts[2], signfont);

        label1.setAlignment(Align.bottom);
        label2.setAlignment(Align.bottom);
        label3.setAlignment(Align.bottom);

        base.add(label1).height(1000f/3).padBottom(10).expandX().fill().row();
        base.add(label2).height(1000f/3).padBottom(10).expandX().fill().row();
        base.add(label3).height(1000f/3).padBottom(10).expandX().fill();
        return base;
    }

    static {
        Threads.runOnMainThread(() -> {
            BitmapFont font = createCosmicReachFont();
            font.getData().setScale(14);
            signbasefont = new Label.LabelStyle(font, Color.BLACK);
        });
    }
    public static void initSignShader() {
        String vertexShader = "attribute vec3 a_position; \n" +
                "attribute vec2 a_texCoords; \n" +
                "uniform mat4 u_projTrans; \n" +
                "uniform mat4 u_modelMatrix; \n" +
                "varying vec2 v_texCoords;  \n" +
                "void main() { \n" +
                "    v_texCoords = a_texCoords; \n" +
                "    gl_Position = u_projTrans * u_modelMatrix * vec4(a_position, 1.0); \n" +
                "}";
        String fragmentShader =
                "uniform int u_flipX;" +
                        "varying vec2 v_texCoords; \n" +
                        "uniform sampler2D u_texture; \n" +
                        "void main() { \n" +
                        "   vec2 flippedTexCoord = v_texCoords;"+
                        "   if (u_flipX == 1) {\n" +
                        "        flippedTexCoord.x = 1.0 - v_texCoords.x;\n" +
                        "   }" +
                        "vec4 color = texture(u_texture, flippedTexCoord);" +
                        "if (color.a < 0.1) {discard;}" +
                        "gl_FragColor = color; } \n";
        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            String log = SignBlockEntity.shader.getLog();
            throw new RuntimeException( "Sign Shader is not compiled!\n" + log);
        }
    }
}
