#version 150

in vec2 v_texCoord0;

uniform sampler2D texDiffuse;
uniform vec4 color;
uniform vec4 ambientColor;
uniform vec4 outlineColor;
uniform float texWidth;
uniform float texHeight;
uniform int funcMask;

void main() 
{
    vec4 texColor = texture(texDiffuse, v_texCoord0);
    if(texColor.a == 0)  discard;

    vec4 newColor;
    int isglowing = funcMask & 0x01;
    int hasboarder = funcMask & 0x02;

    vec2 texOffset = vec2(0.6 / texWidth, 0.6 / texHeight);
    vec4 left = texture2D(texDiffuse, v_texCoord0 - vec2(texOffset.x, 0.0));
    vec4 right = texture2D(texDiffuse, v_texCoord0 + vec2(texOffset.x, 0.0));
    vec4 up = texture2D(texDiffuse, v_texCoord0 + vec2(0.0, texOffset.y));
    vec4 down = texture2D(texDiffuse, v_texCoord0 - vec2(0.0, texOffset.y));

    if (hasboarder == 2 && texColor.a > 0.0 && (left.a == 0.0 || right.a == 0.0 || up.a == 0.0 || down.a == 0.0)) newColor = outlineColor;
    else newColor = color;
    if(isglowing != 1) newColor = newColor * ambientColor;

    gl_FragColor = texColor * newColor;
}