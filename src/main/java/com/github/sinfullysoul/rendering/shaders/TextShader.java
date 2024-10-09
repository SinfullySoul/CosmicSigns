package com.github.sinfullysoul.rendering.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import finalforeach.cosmicreach.util.Identifier;

public class TextShader extends GameShader {
    public static TextShader TEXT_SHADER;
    private static VertexAttribute posAttrib = VertexAttribute.Position();
    private static VertexAttribute texCoordsAttrib = VertexAttribute.TexCoords(0);

    public TextShader(Identifier vertexShader, Identifier fragmentShader) {
        super(vertexShader, fragmentShader);
        this.allVertexAttributesObj = new VertexAttributes(posAttrib, texCoordsAttrib);
    }

    @Override
    public void bind(Camera worldCamera) {
        super.bind(worldCamera);
        this.shader.setUniformMatrix("u_projViewTrans", worldCamera.combined);
    }

    public static void initEntityShader() {
        TEXT_SHADER = new TextShader(Identifier.of("cosmic-signs","shaders/text.vert.glsl"), Identifier.of("cosmic-signs","shaders/text.frag.glsl"));
    }
}
