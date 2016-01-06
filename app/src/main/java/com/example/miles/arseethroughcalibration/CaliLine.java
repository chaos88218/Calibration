package com.example.miles.arseethroughcalibration;

import android.opengl.GLES10;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2016/1/6.
 */
public class CaliLine {

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    final float[] linePointData =
            {
                    47.5f, 100.0f, 0f,
                    47.5f, -100.0f, 0f,
                    -47.5f, 100.0f, 0f,
                    -47.5f, -100.0f, 0f,
            };

    final float[] lineColorData =
            {
                    255, 255, 0, 1,
                    255, 255, 0, 1,
                    255, 255, 0, 1,
                    255, 255, 0, 1,

            };

    public CaliLine() {
        vertexBuffer = RenderUtils.buildFloatBuffer(linePointData);
        colorBuffer = RenderUtils.buildFloatBuffer(lineColorData);
    }

    public void draw(GL10 unused) {
        GLES10.glPushMatrix();
        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDrawArrays(GLES10.GL_LINES, 0, linePointData.length / 3);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glPopMatrix();

    }
}
