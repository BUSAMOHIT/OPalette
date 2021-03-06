precision highp float;

varying vec4 v_Position;
varying vec4 v_WorldPos;
varying vec2 v_TexCoordinate;

uniform sampler2D u_Texture0;

uniform vec2 u_dimension;
uniform float u_cellSize;
uniform float u_margin;
uniform float u_numb;
uniform vec2 u_activeDim;

void main() {

    vec2 pos = gl_FragCoord.xy / u_dimension;

    if (pos.x >= 0. && pos.y >= 0. && pos.x <= u_activeDim.x && pos.y <= u_activeDim.y) {
        float n = floor(gl_FragCoord.y / u_cellSize);

        vec2 margin = vec2(
            (n == 0.) ? u_margin : u_margin * .5,
            (n == u_numb - 1.) ? u_margin : u_margin * .5
        );

        vec2 st_ed = vec2(n, n + 1.) * u_cellSize;
        vec2 mrgnB = vec2(st_ed.x, st_ed.x + margin.x);
        vec2 mrgnT = vec2(st_ed.y - margin.y, st_ed.y);

        if ((gl_FragCoord.y >= mrgnB.x && gl_FragCoord.y <= mrgnB.y) ||
            (gl_FragCoord.y >= mrgnT.x && gl_FragCoord.y <= mrgnT.y))
                gl_FragColor = vec4(0.);
        else if (texture2D(u_Texture0, pos).a != 0.) {
            vec3 color = vec3(0.);
            for (float i = st_ed.x; i < st_ed.y; i += 1.) {
                vec2 point = vec2(pos.x, i / u_dimension.y);
                color += texture2D(u_Texture0, point).rgb;
            }
            gl_FragColor = vec4(color / u_cellSize, 1.);
        } else gl_FragColor = vec4(0.);
    } else gl_FragColor = vec4(0.);
}
