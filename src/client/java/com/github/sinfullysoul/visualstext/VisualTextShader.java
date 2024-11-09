package com.github.sinfullysoul.visualstext;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.github.puzzle.game.resources.PuzzleGameAssetLoader;
import com.github.sinfullysoul.Constants;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import finalforeach.cosmicreach.util.Identifier;

@SuppressWarnings("ALL")
public class VisualTextShader extends GameShader {
    public static VisualTextShader TEXT_SHADER;
    private static VertexAttribute posAttrib = VertexAttribute.Position();
    private static VertexAttribute texCoordsAttrib = VertexAttribute.TexCoords(0);
    private static VertexAttribute color = new VertexAttribute(2, 4, 5126, false, "color");
    private static VertexAttribute ambientColor = new VertexAttribute(3, 4, 5126, false, "ambientColor");
    private static VertexAttribute outlineColor = new VertexAttribute(4, 4, 5126, false, "outlineColor");
    private static VertexAttribute texWidth = new VertexAttribute(5, 1, 5126, false, "texWidth");
    private static VertexAttribute texHeight = new VertexAttribute(6, 1, 5126, false, "texHeight");
    private static VertexAttribute funcMask = new VertexAttribute(7, 1, 5125, false, "funcMask");

    public VisualTextShader(Identifier vertexShader, Identifier fragmentShader) {
        super();
        FileHandle textVert = PuzzleGameAssetLoader.locateAsset(vertexShader);
        FileHandle textFrag = PuzzleGameAssetLoader.locateAsset(fragmentShader);
        this.shader = new ShaderProgram(textVert, textFrag);
        this.allVertexAttributesObj = new VertexAttributes(new VertexAttribute[]{posAttrib, texCoordsAttrib, color, ambientColor, outlineColor, texWidth, texWidth, texHeight, funcMask});
    }

    public static void initTextShader() {
        TEXT_SHADER = new VisualTextShader(Identifier.of(Constants.MOD_ID,"shaders/text.vert.glsl"), Identifier.of(Constants.MOD_ID,"shaders/text.frag.glsl"));
    }

    @Override
    public void bind(Camera worldCamera) {
        super.bind(worldCamera);
        this.shader.setUniformMatrix("u_projViewTrans", worldCamera.combined);
    }

    public void unbind() {
    }
}
