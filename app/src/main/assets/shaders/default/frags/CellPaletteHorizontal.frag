precision mediump float;

varying vec4 v_Position;
varying vec4 v_WorldPos;
varying vec2 v_TexCoordinate;

uniform sampler2D u_Texture0;

uniform vec2 u_dimension;
uniform float u_cellSize;
uniform float u_margin;
uniform float u_numb;

void main() {

    vec2 pos = gl_FragCoord.xy / u_dimension;
    float n = floor(gl_FragCoord.x / u_cellSize);

    vec2 margin = vec2(
        (n == 0.) ? u_margin : u_margin * .5,
        (n == u_numb - 1.) ? u_margin : u_margin * .5
    );

    vec2 st_ed = vec2(n, n + 1.) * u_cellSize;
    vec2 mrgnL = vec2(st_ed.x, st_ed.x + margin.x);
    vec2 mrgnR = vec2(st_ed.y - margin.y, st_ed.y);


    gl_FragColor = texture2D(u_Texture0, pos);

    if ((gl_FragCoord.x >= mrgnL.x && gl_FragCoord.x <= mrgnL.y) ||
        (gl_FragCoord.x >= mrgnR.x && gl_FragCoord.x <= mrgnR.y)) {
        gl_FragColor = vec4(0.);
    }


}