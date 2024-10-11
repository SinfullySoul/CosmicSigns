package com.github.sinfullysoul.entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.github.puzzle.game.ui.font.CosmicReachFont;
import com.github.sinfullysoul.Constants;
import com.github.sinfullysoul.block_entities.SignBlockEntity;
import com.github.sinfullysoul.rendering.shaders.TextShader;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.rendering.shaders.EntityShader;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import finalforeach.cosmicreach.world.Zone;

import java.io.Console;
import java.util.Arrays;

public class TextModelInstance {
    private static final BlockPosition tmpBlockPos1 = new BlockPosition(null, 0, 0, 0);
    private static final BlockPosition tmpBlockPos2 = new BlockPosition(null, 0, 0, 0);
    public Color modelLightColor;

    public Color textColor;
    Vector3 position;
    private Zone zone;
    private Mesh mesh;
    private Texture texture;
    private Color tintColor;
    public boolean glowing = false;
    private float fontSize;

    public GameShader shader = EntityShader.ENTITY_SHADER; //im pretty sure this can just use entity shader until it changes



    public TextModelInstance( Zone zone, Vector3 pos, Color textColor) { //I can let BlockEntity handle unloading loading and updating text this just has to be saved in an array in a playerzone in inGame to render all the textModels
        this.zone = zone;
        this.position = pos;
        this.modelLightColor = Color.WHITE;
        this.textColor = textColor;
        this.mesh = null;
        this.texture = CosmicReachFont.FONT.getRegion().getTexture();
        this.tintColor = Color.RED;
       // Entity.setLightingColor(); //run each time chunk is updated but not sure when to call that so i could just do it every render
        CHAR_UV_X = 16f / CosmicReachFont.FONT.getRegion().getRegionWidth(); //TODO CHANGE THIS

        CHAR_UV_Y = 16f / CosmicReachFont.FONT.getRegion().getRegionHeight() ;

    }
    public void render(Camera worldCamera, Matrix4 modelMat) { //im gonna keep this out of this class so the block entity has the potential to do ticking stuff with it ex rotating sign/hologram?

        if(mesh == null) {
            return;
        }
        Entity.setLightingColor(zone,position,textColor,tintColor,tmpBlockPos1,tmpBlockPos2); //TODO doesnt quite work with colors
        //this.modelLightColor =
//        if (this.glowing) {
//            this.tintColor.set(this.textColor);
//        } else {
//            mixTint(this.tintColor, this.textColor, this.modelLightColor);
//        }
        this.shader.bind(worldCamera);
        this.shader.bindOptionalTexture("texDiffuse", this.texture, 0);
        this.shader.bindOptionalUniform4f("tintColor", this.tintColor.mul(1.5f));
        this.shader.bindOptionalMatrix4("u_modelMat", modelMat);
        this.mesh.render(this.shader.shader, GL20.GL_TRIANGLES);
        this.shader.unbind();
    }
    public void setTint(float r, float g, float b, float a) {
        this.tintColor.set(r, g, b, a);
    }
    public void mixTint(Color c0, Color c1, Color c2) {
        c0.r = c1.r * c2.r;
        c0.g = c1.g * c2.g;
        c0.b = c1.b * c2.b;
        c0.a = c1.a * c2.a;
    }

    public void dispose() {
        if (mesh != null) {
            mesh.dispose();
        }

    }

    //border at -0.35 * font x
    //0.2f * font y

    public void buildCenteredTextMesh(String[] texts, float zStart, float fontSize) { //centered at 0,0
        buildTextMesh(texts,0.0f,0.0f,zStart,fontSize, isCentered);
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

        Constants.LOGGER.info(Arrays.toString(texts));
        Constants.LOGGER.info(length);
        FloatArray verts = new FloatArray( length * 4 * 5); //character length * vertexes * vertex attributes
        ShortArray indicies = new ShortArray(length * 6);
        charCounter = 0;
        if(centered) {
            this.yStart =  0.1f + (texts.length / (fontSize * 2f) - (1f / fontSize) );
        }
        for (int l = 0; l<texts.length; l++) {
            if(isCentered) {
                int stringPixelLength =0;
                boolean firstPass = true;
                for(int x = 0; x < texts[l].length(); x++) {

                    stringPixelLength+= CosmicReachFont.FONT.getData().getGlyph(texts[l].charAt(x)).xadvance;
//                    if(firstPass) {
//                        stringPixelLength+= stringPixelLength / 2 ;
//                        firstPass = false;
//                    }
                }


               // this.xStart = -xStart -  texts[l].length() / (2f *  fontSize)  ;
                this.xStart = -xStart -  (stringPixelLength / 16f) / (2f *  fontSize) - 0.5f / fontSize ; // subtract one half font size character to center on block
                Constants.LOGGER.info("Pixel Length {} XSTART {} ", stringPixelLength,this.xStart);
            }
            float charPos = 0;
            for(int i = 0; i < texts[l].length(); i++ ) {
                //need to calculate the offset for each line x for centered

              charPos =  addCharacterQuad(verts, indicies, texts[l].charAt(i),charPos, l);
              Constants.LOGGER.info("CHARPOS {}", charPos);

            }
        }


        mesh = new Mesh(false, verts.size, indicies.size,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );
        Constants.LOGGER.info("TEXTMODEL {}", verts.items.length);
        Constants.LOGGER.info(Arrays.toString(verts.items));

        mesh.setVertices(verts.items);


        mesh.setIndices(indicies.items);
        Constants.LOGGER.info(mesh.getNumVertices());
    }
    float CHAR_UV_X;
    float CHAR_UV_Y;
    short charCounter;
    float xStart,  yStart,  zStart;
    float lineSpacing; //would depend on the font scale

    boolean isCentered = true;
    private float addCharacterQuad(FloatArray verts, ShortArray indices, char c, float pos, int line) {


        float u = CHAR_UV_X * (float)(c % 16); // i think this just has to be hardcoded
        float v = CHAR_UV_Y * (float)(c / 16);


        BitmapFont.Glyph glyph = CosmicReachFont.FONT.getData().getGlyph(c);
        Constants.LOGGER.info("U {} U2 {} Width: {}", glyph.u, glyph.u2, glyph.width);
        Constants.LOGGER.info("curernt U {} U2 {} ", u, u + CHAR_UV_X);
        Constants.LOGGER.info("X advance {}, srcx {}", glyph.xadvance,glyph.srcX);
        float advance = glyph.xadvance / 2.0f;

//        float u = glyph.u;
//        float v = glyph.v;
//        float u2 = glyph.u2;
//        float v2 = glyph.v2;
        float x = fontSize * xStart + (pos + advance) / 16f;//divide by the char size  pos is in
        float y = fontSize * yStart - line ; //TODO add line offset
        float z = zStart;
        Constants.LOGGER.info("CHAR {}", c);
        Constants.LOGGER.info("NEW QUAD X POS {} Y POS {}",x,y);


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

//        if(advance <= 4) {
//            advance += 8;
//        }
        return pos + advance * 2.0f; //add a constant 2 for spacing
    }
}
