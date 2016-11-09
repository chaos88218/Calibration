package com.example.miles.arseethroughcalibration.CalibrationDrawing;

import android.opengl.GLES10;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/11/12.
 */
public class CaliSquarePointsDD {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private float[] squareCoords = new float[]{
            40f, 40f, 0,
            -20f, 20f, 0,
            -20f, -20f, 0,
            20f, -20f, 0
    };
    private float color[] = new float[]{
            1, 1, 0, 1,
            1, 1, 0, 1,
            1, 1, 0, 1,
            1, 1, 0, 1,
    };

    private FloatBuffer vertexBuffer1;
    private FloatBuffer colorBuffer1;

    final float[] linePointData =
            {
                    20, 20, 0f,
                    -20, 20, 0f,

                    -20, 20, 0f,
                    -20, -20, 0f,

                    -20, -20, 0f,
                    20, -20, 0f,

                    20, -20, 0f,
                    20, 20, 0f
            };

    final float[] lineColorData =
            {
                    1, 1, 0, 1,
                    1, 1, 0, 1,

                    1, 1, 0, 1,
                    1, 1, 0, 1,

                    1, 1, 0, 1,
                    1, 1, 0, 1,

                    1, 1, 0, 1,
                    1, 1, 0, 1

            };


    public CaliSquarePointsDD() {
        vertexBuffer = RenderUtils.buildFloatBuffer(squareCoords);
        colorBuffer = RenderUtils.buildFloatBuffer(color);
        vertexBuffer1 = RenderUtils.buildFloatBuffer(linePointData);
        colorBuffer1 = RenderUtils.buildFloatBuffer(lineColorData);
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
        GLES10.glDisableClientState(GLES10.GL_NORMAL_ARRAY);

////**************************************************************************
        GLES10.glPushMatrix();
        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer1);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer1);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDrawArrays(GLES10.GL_LINES, 0, linePointData.length / 3);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glPopMatrix();

    }
}
