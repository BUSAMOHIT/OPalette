precision mediump float;

varying vec4 v_Position;
varying vec4 v_WorldPos;
varying vec2 v_TexCoordinate;

uniform sampler2D u_Texture0;
uniform sampler2D u_Texture1;


void main() {
    vec4 col1 = texture2D(u_Texture0, v_TexCoordinate).rgba;
    vec4 col2 = texture2D(u_Texture1, v_TexCoordinate).rgba;

    gl_FragColor = vec4(vec3(col2.rgb * col1.rgb), 1);
}
