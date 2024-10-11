package com.github.sinfullysoul.block_entities.models;


import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.github.puzzle.game.ui.font.CosmicReachFont;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.rendering.shaders.EntityShader;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import finalforeach.cosmicreach.world.Zone;

public class TextModelInstance {
    private static final BlockPosition tmpBlockPos1 = new BlockPosition(null, 0, 0, 0);
    private static final BlockPosition tmpBlockPos2 = new BlockPosition(null, 0, 0, 0);
    public Color modelLightColor;

    public final Color textColor = new Color(Color.BLACK);
    Vector3 position;
    private Zone zone;
    private Mesh mesh;
    private Texture texture;
    private Color tintColor;
    public boolean glowing = false;
    private float fontSize;
    private Matrix4 modelMat = new Matrix4();
    public float rotationY; //for now just the y rotation can add more in future

    public GameShader shader = EntityShader.ENTITY_SHADER; //im pretty sure this can just use entity shader until it changes



    public TextModelInstance( Zone zone, Vector3 pos, Color textColor,float fontSize) {
        this.zone = zone;
        this.position = pos;
        this.modelLightColor = new Color(Color.WHITE);
        this.textColor.set(textColor);
        this.mesh = null;
        this.texture = CosmicReachFont.FONT.getRegion().getTexture();
        this.tintColor = new Color(Color.RED);
        this.fontSize = fontSize;
        CHAR_UV_X = 16f / CosmicReachFont.FONT.getRegion().getRegionWidth(); //TODO CHANGE THIS
        CHAR_UV_Y = 16f / CosmicReachFont.FONT.getRegion().getRegionHeight() ;
    }
    public void update() {
        this.modelMat.idt();

        modelMat.rotate(new Vector3(0,1,0), this.rotationY);
        float fontsize = this.fontSize;
        float FONT_SCALE = 1.0f / fontsize;
        modelMat.scale(FONT_SCALE,FONT_SCALE,1.0f);
        modelMat.trn(position.x + 0.5f,position.y + 0.5f,position.z + 0.5f);
    }
    public void render(Camera worldCamera) {

        if(mesh == null) {
            return;
        }
        Entity.setLightingColor(zone,position,textColor,tintColor,tmpBlockPos1,tmpBlockPos2); //TODO doesnt quite work with colors
        if (this.glowing) {
            this.tintColor.set(this.textColor);
        } else {
            this.tintColor.mul(textColor);
        }
        this.shader.bind(worldCamera);
        this.shader.bindOptionalTexture("texDiffuse", this.texture, 0);
        this.shader.bindOptionalUniform4f("tintColor", this.tintColor.mul(1.0f));
        this.shader.bindOptionalMatrix4("u_modelMat", modelMat);
        this.mesh.render(this.shader.shader, GL20.GL_TRIANGLES);
        this.shader.unbind();
    }

    public void dispose() {
        if (mesh != null) {
            mesh.dispose();
        }
    }


    public void setTextColor(Color color) {
        this.textColor.set(color);
    }
    public void buildTextMesh(String[] texts, float xStart, float yStart, float zStart, float fontSize, boolean centered) {
        if (mesh!= null) {
            mesh.dispose();
        }
        this.fontSize = fontSize;
        this.isCentered = centered;
        this.xStart = xStart;
        this.yStart = yStart;
        this.zStart = zStart;

        int length = 0;

        for (String text : texts) {
            length += text.length();
        }
        if (length == 0) {
            mesh = null;
            return;
        }


        FloatArray verts = new FloatArray( length * 4 * 5); //character length * vertexes * vertex attributes
        ShortArray indicies = new ShortArray(length * 6);
        charCounter = 0;
        if(centered) {
            this.yStart =  0.1f + (texts.length / (fontSize * 2f) - (1f / fontSize) );
        }
        for (int l = 0; l<texts.length; l++) {
            if(isCentered) {
                int stringPixelLength =0;

                for(int x = 0; x < texts[l].length(); x++) {

                    stringPixelLength+= CosmicReachFont.FONT.getData().getGlyph(texts[l].charAt(x)).xadvance;

                }
                this.xStart = -xStart -  (stringPixelLength / 16f) / (2f *  fontSize) - 0.5f / fontSize ; // subtract one half font size character to center on block center

            }
            float charPos = 0;
            for(int i = 0; i < texts[l].length(); i++ ) {
              charPos =  addCharacterQuad(verts, indicies, texts[l].charAt(i),charPos, l);

            }
        }
        mesh = new Mesh(false, verts.size, indicies.size,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );
        mesh.setVertices(verts.items);
        mesh.setIndices(indicies.items);
    }
    float CHAR_UV_X;
    float CHAR_UV_Y;
    short charCounter;
    float xStart,  yStart,  zStart;
    boolean isCentered = true;
    private float addCharacterQuad(FloatArray verts, ShortArray indices, char c, float pos, int line) {


        float u = CHAR_UV_X * (float)(c % 16); // i think this just has to be hardcoded
        float v = CHAR_UV_Y * (float)(c / 16);


        BitmapFont.Glyph glyph = CosmicReachFont.FONT.getData().getGlyph(c);
//        Constants.LOGGER.info("U {} U2 {} Width: {}", glyph.u, glyph.u2, glyph.width);
//        Constants.LOGGER.info("curernt U {} U2 {} ", u, u + CHAR_UV_X);
//        Constants.LOGGER.info("X advance {}, srcx {}", glyph.xadvance,glyph.srcX);
        float advance = glyph.xadvance / 2.0f;


        float x = fontSize * xStart + (pos + advance) / 16f;//divide by the char size  pos is in
        float y = fontSize * yStart - line ; //TODO add line offset
        float z = zStart;
//        Constants.LOGGER.info("CHAR {}", c);
//        Constants.LOGGER.info("NEW QUAD X POS {} Y POS {}",x,y);


        verts.add( x); // x1
        verts.add( y); // y1
        verts.add( z);
        verts.add( u); // u1
        verts.add( v + CHAR_UV_Y); // v1 //done like this because the image is flipped

        verts.add( x + 1f); // x2
        verts.add( y); // y2
        verts.add( z);
        verts.add( u + CHAR_UV_X); // u2
        verts.add( v + CHAR_UV_Y); // v2

        verts.add( x + 1f); // x3
        verts.add( y + 1f); // y3
        verts.add( z);
        verts.add( u + CHAR_UV_X); // u3
        verts.add( v ); // v3

        verts.add( x); // x4
        verts.add( y + 1f); // y4
        verts.add( z);
        verts.add( u ); // u4
        verts.add( v ); // v4


        short offset =  (charCounter);
        indices.add(offset);
        indices.add((short) (1 + offset)); //indices for each quad is the start idx in array + 0 1 2 2 3 0 for the indices
        indices.add((short) (2 + offset));
        indices.add((short) (2 + offset));
        indices.add((short) (3 + offset));
        indices.add(offset);
        charCounter+= 4;


        return pos + advance * 2.0f;
    }
}
