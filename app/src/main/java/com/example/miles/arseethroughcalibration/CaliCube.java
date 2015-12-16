package com.example.miles.arseethroughcalibration;

import android.opengl.GLES10;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/12/8.
 */
public class CaliCube {

    private float sl = 45/2f;
    private float sh = 75/2f;

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;

    final float[] cubePositionData =
            {
                    // Front face
                    -sl, sl, sh-sh,
                    -sl, -sl, sh-sh,
                    sl, sl, sh-sh,
                    -sl, -sl, sh-sh,
                    sl, -sl, sh-sh,
                    sl, sl, sh-sh,

                    // Right face
                    sl, sl, sh-sh,
                    sl, -sl, sh-sh,
                    sl, sl, -sh-sh,
                    sl, -sl, sh-sh,
                    sl, -sl, -sh-sh,
                    sl, sl, -sh-sh,

                    // Back face
                    sl, sl, -sh-sh,
                    sl, -sl, -sh-sh,
                    -sl, sl, -sh-sh,
                    sl, -sl, -sh-sh,
                    -sl, -sl, -sh-sh,
                    -sl, sl, -sh-sh,

                    // Left face
                    -sl, sl, -sh-sh,
                    -sl, -sl, -sh-sh,
                    -sl, sl, sh-sh,
                    -sl, -sl, -sh-sh,
                    -sl, -sl, sh-sh,
                    -sl, sl, sh-sh,

                    // Top face
                    -sl, sl, -sh-sh,
                    -sl, sl, sh-sh,
                    sl, sl, -sh-sh,
                    -sl, sl, sh-sh,
                    sl, sl, sh-sh,
                    sl, sl, -sh-sh,

                    // Bottom face
                    sl, -sl, -sh-sh,
                    sl, -sl, sh-sh,
                    -sl, -sl, -sh-sh,
                    sl, -sl, sh-sh,
                    -sl, -sl, sh-sh,
                    -sl, -sl, -sh-sh,
            };

    float[] color1 = new float[]{0.0f, 0.5f, 0.5f};
    float[] color2 = new float[]{0.5f, 0.0f, 0.5f};
    float[] color3 = new float[]{0.5f, 0.0f, 0.5f};
    float[] color4 = new float[]{0.5f, 0.0f, 0.5f};
    float[] color5 = new float[]{0.5f, 0.0f, 0.5f};
    float[] color6 = new float[]{0.5f, 0.0f, 0.5f};

    final float[] cubeColorData =
            {
                    // Front face
                    color1[0], color1[1], color1[2], 0.5f,
                    color1[0], color1[1], color1[2], 0.5f,
                    color1[0], color1[1], color1[2], 0.5f,
                    color1[0], color1[1], color1[2], 0.5f,
                    color1[0], color1[1], color1[2], 0.5f,
                    color1[0], color1[1], color1[2], 0.5f,

                    // Right face
                    color2[0], color2[1], color2[2], 0.5f,
                    color2[0], color2[1], color2[2], 0.5f,
                    color2[0], color2[1], color2[2], 0.5f,
                    color2[0], color2[1], color2[2], 0.5f,
                    color2[0], color2[1], color2[2], 0.5f,
                    color2[0], color2[1], color2[2], 0.5f,

                    // Back face
                    color3[0], color3[1], color3[2], 0.5f,
                    color3[0], color3[1], color3[2], 0.5f,
                    color3[0], color3[1], color3[2], 0.5f,
                    color3[0], color3[1], color3[2], 0.5f,
                    color3[0], color3[1], color3[2], 0.5f,
                    color3[0], color3[1], color3[2], 0.5f,

                    // Left face
                    color4[0], color4[1], color4[2], 0.5f,
                    color4[0], color4[1], color4[2], 0.5f,
                    color4[0], color4[1], color4[2], 0.5f,
                    color4[0], color4[1], color4[2], 0.5f,
                    color4[0], color4[1], color4[2], 0.5f,
                    color4[0], color4[1], color4[2], 0.5f,

                    // Top face
                    color5[0], color5[1], color5[2], 0.5f,
                    color5[0], color5[1], color5[2], 0.5f,
                    color5[0], color5[1], color5[2], 0.5f,
                    color5[0], color5[1], color5[2], 0.5f,
                    color5[0], color5[1], color5[2], 0.5f,
                    color5[0], color5[1], color5[2], 0.5f,

                    // Bottom face
                    color6[0], color6[1], color6[2], 0.5f,
                    color6[0], color6[1], color6[2], 0.5f,
                    color6[0], color6[1], color6[2], 0.5f,
                    color6[0], color6[1], color6[2], 0.5f,
                    color6[0], color6[1], color6[2], 0.5f,
                    color6[0], color6[1], color6[2], 0.5f
            };


    final float[] cubeNormalData =
            {
                    // Front face
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,

                    // Right face
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,

                    // Left face
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,

                    // Top face
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,

                    // Bottom face
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f
            };

    public CaliCube() {
        vertexBuffer = RenderUtils.buildFloatBuffer(cubePositionData);
        normalBuffer = RenderUtils.buildFloatBuffer(cubeNormalData);
        colorBuffer = RenderUtils.buildFloatBuffer(cubeColorData);
    }

    public void draw(GL10 unused) {
        GLES10.glPushMatrix();
        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);
        GLES10.glNormalPointer(GLES10.GL_FLOAT, 0, normalBuffer);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_NORMAL_ARRAY);

        GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, cubePositionData.length / 3);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_NORMAL_ARRAY);
        GLES10.glPopMatrix();

    }
}
