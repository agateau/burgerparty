#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform vec2 resolution;
uniform float startAngle;
uniform vec3 bgColor;
uniform vec3 fgColor;

const float ANGLE1_EDGE = 5.0;
const float ANGLE2_EDGE = 12.0;
const float EDGE_DELTA = 0.5;
const float ANGLE_PERIOD = 20.0;

const vec2 CENTER = vec2(0.6, 0.4);

float mystep(float edge, float x) {
    return smoothstep(edge - EDGE_DELTA / 2.0, edge + EDGE_DELTA / 2.0, x);
}

void main()
{
    vec2 position = (gl_FragCoord.xy / resolution.xy) - CENTER;
    float angle = degrees(atan(position.y, position.x));
    angle = mod(angle + startAngle, ANGLE_PERIOD);

    float len = length(position);
    float rayAlpha = mystep(ANGLE1_EDGE, angle) * mystep(angle, ANGLE2_EDGE);
    gl_FragColor = vec4(mix(bgColor, fgColor, rayAlpha * len), 1.0);
}
