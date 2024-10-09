package com.github.sinfullysoul.entities;


import com.badlogic.gdx.graphics.*;
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
        CHAR_SIZE_X = 16f / CosmicReachFont.FONT.getRegion().getRegionWidth(); //TODO CHANGE THIS

        CHAR_SIZE_Y = 16f / CosmicReachFont.FONT.getRegion().getRegionHeight();

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
    public void buildTextMesh(String[] texts) {
        if (mesh!= null) {
            mesh.dispose();
        }
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
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );
        Constants.LOGGER.info("TEXTMODEL");
        Constants.LOGGER.info(Arrays.toString(verts.items));

        mesh.setVertices(verts.items);


        mesh.setIndices(indicies.items);
    }
    float CHAR_SIZE_X;
    float CHAR_SIZE_Y;
    private void addCharacterQuad(FloatArray verts, ShortArray indices, char c, int pos) {


        float u = CHAR_SIZE_X * (float)(c % 16); // i think this just has to be hardcoded
        float v = CHAR_SIZE_Y * (float)(c / 16);

        float OFFSET_X = pos * CHAR_SIZE_X * 30 ; //this has to be the size of the char * scale i think


        float x = -0.5f + OFFSET_X;
        float y = -0.5f + 0f; //TODO add line offset
        float z = 0.075f;
        Constants.LOGGER.info("NEW QUAD X POS {}",x);

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
}
