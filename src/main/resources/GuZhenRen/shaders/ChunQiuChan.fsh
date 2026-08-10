#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;

void main() {
    vec2 uv = v_texCoords;

    vec2 p = uv - vec2(0.5);

    float aspect = u_resolution.x / u_resolution.y;
    vec2 pos = vec2(p.x * aspect, p.y);

    float r = length(pos);
    float a = atan(pos.y, pos.x);

    float swirl = a + u_time * 3.0 - r * (10.0 + u_time * 2.0);
    float ripple = r * 30.0 - u_time * 8.0;

    float waveStrength = 0.025 * min(u_time * 1.2, 1.0);
    float displacement = (sin(swirl) + sin(ripple) * 0.4) * waveStrength;

    vec2 dir = (r > 0.0001) ? normalize(pos) : vec2(0.0);
    vec2 uvOffset = vec2(dir.x / aspect, dir.y) * displacement;

    vec2 warpedUV = uv + uvOffset;
    warpedUV = clamp(warpedUV, 0.0, 1.0);

    vec4 screenColor = texture2D(u_texture, warpedUV);

    float vignette = smoothstep(0.8, 0.2, r);
    screenColor.rgb *= mix(1.0, vignette, 0.25);

    vec3 orangeGlow      = vec3(1.0, 0.70, 0.05);
    vec3 yellowGreenGlow = vec3(0.65, 0.95, 0.05);

    float edgeFactor = smoothstep(0.45, 0.75, r);

    float colorRotate = sin(a * 5.0 + u_time * 3.0) * 0.5 + 0.5;
    vec3 borderGlowColor = mix(orangeGlow, yellowGreenGlow, colorRotate);

    float glowPulse = sin(u_time * 5.0) * 0.2 + 0.8;

    float glowAlpha = edgeFactor * glowPulse * smoothstep(0.0, 0.4, u_time) * 0.65;
    screenColor.rgb += borderGlowColor * glowAlpha;

    float whiteFlash = smoothstep(1.8, 2.7, u_time);
    vec3 finalColor = mix(screenColor.rgb, vec3(1.0), whiteFlash);

    float alpha = smoothstep(0.0, 0.15, u_time);

    gl_FragColor = vec4(finalColor, alpha);
}