#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec2 resolution;
uniform float startAngle;

const float ANGLE_EDGE = 20.0;
const float ANGLE_PERIOD = 30.0;

const vec2 CENTER = vec2(0.6, 0.4);

void main()
{
    vec4 c = texture2D(u_texture, v_texCoords);
    vec2 position = (gl_FragCoord.xy / resolution.xy) - CENTER;
    float angle = degrees(atan(position.y, position.x));
    angle = mod(angle + startAngle, ANGLE_PERIOD);
    float len = length(position);
    gl_FragColor = vec4(vec3(step(ANGLE_EDGE, angle) * len), c.a);
}
