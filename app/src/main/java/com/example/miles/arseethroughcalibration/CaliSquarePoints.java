package com.example.miles.arseethroughcalibration;

import android.opengl.GLES10;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/11/12.
 */
public class CaliSquarePoints {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private float[] squareCoords = new float[]{
            45/2f, 45/2f, 0,
            -45/2f, 45/2f, 0,
            -45/2f, -45/2f, 0,
            45/2f, -45/2f, 0
    };
    private float color[] = new float[]{
            1, 0, 0, 1,
            1, 0, 0, 1,
            1, 0, 0, 1,
            1, 0, 0, 1,
    };


    public CaliSquarePoints() {
        vertexBuffer = RenderUtils.buildFloatBuffer(squareCoords);
        colorBuffer = RenderUtils.buildFloatBuffer(color);
    }
    public void draw(GL10 unused) {

        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_NORMAL_ARRAY);

        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, squareCoords.length / 3);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_NORMAL_ARRAY);

    }
}
