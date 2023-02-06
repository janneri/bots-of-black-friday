varying vec2 vTextureCoord;
varying vec4 vColor;

uniform sampler2D uSampler;

uniform float outerStrength;

uniform vec4 glowColor;

uniform vec4 filterArea;
uniform vec4 filterClamp;
uniform float alpha;

uniform float time;

const float PI = 3.14159265358979323846264;
const float TWO_PI = PI * 2.0;

const float DIST = __DIST__;
const float ANGLE_STEP_SIZE = min(__ANGLE_STEP_SIZE__, TWO_PI);
const float ANGLE_STEP_NUM = ceil(TWO_PI / ANGLE_STEP_SIZE);

const float MAX_TOTAL_ALPHA = ANGLE_STEP_NUM * DIST * (DIST + 1.0) / 2.0;


void main(void) {
    vec2 px = vec2(1.0 / filterArea.x, 1.0 / filterArea.y);

    float totalAlpha = 0.0;

    vec2 direction;
    vec2 displaced;
    vec4 curColor;

    for (float angle = 0.0; angle < TWO_PI; angle += ANGLE_STEP_SIZE) {
        direction = vec2(cos(angle), sin(angle)) * px;

        for (float curDistance = 0.0; curDistance < DIST; curDistance++) {
            displaced = clamp(vTextureCoord + direction *
                              (curDistance + 1.0), filterClamp.xy, filterClamp.zw);

            curColor = texture2D(uSampler, displaced);

            totalAlpha += (DIST - curDistance) * curColor.a;
        }
    }

    curColor = texture2D(uSampler, vTextureCoord);

    float alphaRatio = (totalAlpha / MAX_TOTAL_ALPHA);

    vec4 innerColor = mix(curColor, glowColor, 0.0);

    float outerGlowAlpha = alphaRatio * outerStrength * (1. - curColor.a);
    float outerGlowStrength = min(1.0 - innerColor.a, outerGlowAlpha);

    vec4 outerGlowColor = outerGlowStrength * glowColor.rgba * alpha;
    gl_FragColor = innerColor + outerGlowColor;
}
