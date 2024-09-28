package com.github.sinfullysoul.block_entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.Zone;

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
                texture.dispose();
                fbo.dispose();
            });
        }
    }

    @Override
    public void onCreate(BlockState blockState) {
        super.onCreate(blockState);
        dir = blockState.rotXZ;
        dir -= 90;
    }

    @Override
    public String getBlockEntityId() {
        return id.toString();
    }

    @Override
    public void onRender(Camera camera) {
        if(camera == null) return;
        if(mesh == null) {
            mesh = new Mesh(false, 4, 6,
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                    new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords")
            );

            modelMatrix = new Matrix4();
            modelMatrix.idt();
            modelMatrix.translate(this.getGlobalX(), this.getGlobalY(), this.getGlobalZ());
            modelMatrix.rotate(Vector3.Y, dir);

            float[] vertices = {
                    0.13f, 0.38f, 0f, 0f, 0f,  // Bottom-left (position and UV)
                    0.87f, 0.38f, 0f, 1f, 0f,  // Bottom-right
                    0.87f, 0.81f, 0f, 1f, 1f,  // Top-right
                    0.13f, 0.81f, 0f, 0f, 1f   // Top-left
            };
            short[] indices;
            if(dir == 0) {
                indices = new short[]{0, 1, 2, 2, 3, 0};
                modelMatrix.translate(new Vector3(0f,0f,0.568f));
                flip = 1;
            }
            else if(dir == 90) {
                indices = new short[]{2, 1, 0, 0, 3, 2};
                modelMatrix.translate(new Vector3(-1f,0f,0.43f));
            }
            else if (dir == 180) {
                indices = new short[]{0, 1, 2, 2, 3, 0};
                modelMatrix.translate(new Vector3(-1f,0f,-0.43f));
                flip = 1;
            }
            else if(dir == 270) {
                indices = new short[]{0, 1, 2, 2, 3, 0};
                modelMatrix.translate(new Vector3());
            }
            else {
                indices = new short[]{2, 1, 0, 0, 3, 2};
                modelMatrix.translate(new Vector3(0f,0f,-0.568f));
            }
            mesh.setVertices(vertices);
            mesh.setIndices(indices);

            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 1000, 1000, false);
        }
        if(runTexture) texture = buildTexture();
        shader.begin();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        shader.setUniformMatrix("u_modelMatrix", modelMatrix);
        shader.setUniformi("u_flipX", flip);
        texture.bind(0);
        shader.setUniformi("u_texture", 0);
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
    }



    @Override
    public void onRemove() {
        super.onRemove();
        mesh.dispose();
        texture.dispose();
        fbo.dispose();
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
                        "gl_FragColor = texture2D(u_texture, flippedTexCoord); } \n";
        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            String log = SignBlockEntity.shader.getLog();
            throw new RuntimeException( "Sign Shader is not compiled!\n" + log);
        }
    }
}
