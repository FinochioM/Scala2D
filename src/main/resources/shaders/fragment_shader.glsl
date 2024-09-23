#version 330 core

out vec4 FragColor;

in vec2 TexCoord;

// Samplers para atlas y texturas individuales
uniform sampler2D atlasTexture;
uniform sampler2D individualTexture;

// Variable para decidir qué sampler usar
uniform bool useAtlas;

void main() {
    if(useAtlas) {
        FragColor = texture(atlasTexture, TexCoord);
    } else {
        FragColor = texture(individualTexture, TexCoord);
    }
}
