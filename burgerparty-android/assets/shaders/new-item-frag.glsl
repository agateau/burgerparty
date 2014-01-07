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
uniform vec4 bgColor1;
uniform vec4 bgColor2;
uniform vec4 fgColor;
uniform float modulationSpeed;

const float ANGLE1_EDGE = 5.0;
const float ANGLE2_EDGE = 12.0;
const float EDGE_DELTA = 0.5;
const float ANGLE_PERIOD = 20.0;
const float ANGLE_MODULATION = 4.0;
const float MODULATION_SPEED = 12.0;

const vec2 CENTER = vec2(0.6, 0.4);

float mystep(float edge, float x) {
    return smoothstep(edge - EDGE_DELTA / 2.0, edge + EDGE_DELTA / 2.0, x);
}

void main()
{
    vec2 position = (gl_FragCoord.xy / resolution.xy) - CENTER;
    float len = length(position);
    vec4 bgColor = mix(bgColor1, bgColor2, len);

    float angle = degrees(atan(position.y, position.x));
    float modulation = ANGLE_MODULATION * sin(radians(
        angle * modulationSpeed
        ));
    angle = mod(angle + startAngle, ANGLE_PERIOD);

    float rayAlpha = mystep(ANGLE1_EDGE - modulation / 2.0, angle)
        * mystep(angle, ANGLE2_EDGE + modulation / 2.0);
    gl_FragColor = mix(bgColor, fgColor, rayAlpha * len);
}
