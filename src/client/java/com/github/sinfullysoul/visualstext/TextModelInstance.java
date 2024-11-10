package com.github.sinfullysoul.visualstext;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.github.puzzle.game.ui.font.CosmicReachFont;
import com.github.sinfullysoul.Constants;
import finalforeach.cosmicreach.world.Sky;
import finalforeach.cosmicreach.world.Zone;

public class TextModelInstance {

    private final VisualTextShader shader = VisualTextShader.TEXT_SHADER;
    private Texture texture;

    public Zone zone;
    public  Vector3 position = new Vector3();
    public float rotationY = 0f;
    public boolean isCentered = true;

    private Color textColor = Color.BLACK.cpy();
    private Color borderColor = Color.GOLD.cpy();
    private int funcMask = 0;
    private float fontSize = 8f;
    private Matrix4 modelMat = new Matrix4();
    private Mesh mesh;

    private float CHAR_UV_X;
    private float CHAR_UV_Y;
    private short charCounter;
    private float xStart,  yStart,  zStart;

    public TextModelInstance(Zone zone, Vector3 position, BitmapFont font, float CharWidth, float CharHeight) {
        this.texture = font.getRegion().getTexture();
        this.CHAR_UV_X = CharWidth / font.getRegion().getRegionWidth();
        this.CHAR_UV_Y = CharHeight / font.getRegion().getRegionHeight() ;
        new TextModelInstance(zone, position);
    }

    public TextModelInstance(Zone zone, Vector3 position) {
        this.zone = zone;
        this.position.set(position);
        if(this.texture == null) { // set the default font to CR font
            this.texture = CosmicReachFont.FONT.getRegion().getTexture();
            this.CHAR_UV_X = 16f / CosmicReachFont.FONT.getRegion().getRegionWidth();
            this.CHAR_UV_Y = 16f / CosmicReachFont.FONT.getRegion().getRegionHeight();
        }
    }

    public void render(Camera worldCamera) {
        if(mesh == null) {
            return;
        }
        this.shader.bind(worldCamera);
        this.shader.bindOptionalTexture("texDiffuse", this.texture, 0);
        this.shader.bindOptionalUniform4f("color", this.textColor);
        this.shader.bindOptionalUniform4f("ambientColor", Sky.currentSky.currentAmbientColor);
        this.shader.bindOptionalUniform4f("outlineColor", borderColor);
        this.shader.bindOptionalMatrix4("u_modelMat", modelMat);
        this.shader.bindOptionalFloat("texWidth", (float) this.texture.getWidth());
        this.shader.bindOptionalFloat("texHeight", (float) this.texture.getHeight());
        this.shader.bindOptionalInt("funcMask", this.funcMask);
        this.mesh.render(this.shader.shader, GL20.GL_TRIANGLES);
        this.shader.unbind();
    }

    public void dispose() {
        if (mesh != null) {
            mesh.dispose();
        }
    }

    public void update() {
        this.modelMat.idt();
        modelMat.rotate(Vector3.Y, this.rotationY);
        modelMat.scale(this.fontSize / 102f, this.fontSize / 102f,1.0f);
        modelMat.trn(position.x,position.y,position.z);
    }

    public void isGlowing(boolean is){
        if(is) this.funcMask |= 1;
        else this.funcMask &= ~1;
    }

    public void hasBorder(boolean is){
        if(is) this.funcMask |= 2;
        else this.funcMask &= ~2;
    }


    public void setFontSize(float size){
        if(size < 0 || size > 102) return;
        this.fontSize = size;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void setColor(Color color) {
        this.textColor = color;
    }

    public Color getColor() {
         return this.textColor;
    }

    public void setRotationY(float y) {
        this.rotationY = y;
    }

    public void buildTextMesh(String[] texts, float xStart, float yStart, float zStart, boolean centered) {
        if (mesh!= null) {
            mesh.dispose();
        }
        float invertedTextSize = this.getFontSize() / 102f;
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
       // float ySpacing = Math.max((float)(22 - fontSize) / 7f , 1.0f); //TODO dont make this hardcoded
        float ySpacing = Math.max(15f/(float)Math.pow(fontSize,1.5)+0.7428f ,1.0f); //this is specific to the current sign font range of 7-15
        //ySpacing is used so smaller fonts arent bunched up on the sign and have more spacing between lines
        if(centered) {
            this.yStart = yStart+  ( texts.length / ( 2f) - (1f ) + (ySpacing - 1f));
            Constants.LOGGER.info("YSTART {}: YSPACING {}", this.yStart, ySpacing);
        }
        for (int l = 0; l<texts.length; l++) {
            if(isCentered) {
                int stringPixelLength =0;
                for(int x = 0; x < texts[l].length(); x++) {
                    stringPixelLength += CosmicReachFont.FONT.getData().getGlyph(texts[l].charAt(x)).xadvance; //this is in pixels each char is
                }
                this.xStart = -(stringPixelLength / 16f) / (2f ) - 0.5f  ; // subtract one half font size character to center on block center
            }
            float charPos = 0;
            for(int i = 0; i < texts[l].length(); i++ ) {
                charPos =  addCharacterQuad(verts, indicies, texts[l].charAt(i),charPos, (float)l * ySpacing, invertedTextSize);
            }
        }
        mesh = new Mesh(false, verts.size, indicies.size,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );
        mesh.setVertices(verts.items);
        mesh.setIndices(indicies.items);
    }

    private float addCharacterQuad(FloatArray verts, ShortArray indices, char c, float pos, float line, float invertedTextSize) {
        float u = CHAR_UV_X * (float)(c % 16); // i think this just has to be hardcoded
        float v = CHAR_UV_Y * (float)(c / 16);

        BitmapFont.Glyph glyph = CosmicReachFont.FONT.getData().getGlyph(c);
        float advance = glyph.xadvance / 2.0f;

        float x =  xStart + (pos + advance) / 16f ;//divide by the char size  pos is in
        float y =  yStart - line  ;
        float z = zStart;

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

        short offset = (charCounter);
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
