package com.example.miles.arseethroughcalibration;

import android.opengl.GLU;
import android.util.Log;
import android.view.View;

import com.example.miles.arseethroughcalibration.CalibrationDrawing.CaliLine;
import com.example.miles.arseethroughcalibration.CalibrationDrawing.CaliSquarePoints;
import com.example.miles.arseethroughcalibration.CalibrationDrawing.CaliSquarePointsDD;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class SimpleRenderer extends ARRenderer {

    public static int markerID = -1;
    private CaliLine caliLine = new CaliLine();
    private CaliSquarePoints caliSquarePoints = new CaliSquarePoints();
    private CaliSquarePointsDD caliSquarePointsDD = new CaliSquarePointsDD();
    private float rate;

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {

        markerID = ARToolKit.getInstance().addMarker("single;Data/N_ART.pat;40");
        if (markerID < 0) return false;
        return true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        super.onSurfaceChanged(gl, w, h);
        rate = (float) w / (float) h;
        Log.e("123", rate + "*");
    }

    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Apply the ARToolKit projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, MainActivity.viewAngle, rate, 1.0f, 10000.0f);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CCW);

        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // If the marker is visible, apply its transformation, and draw a cube
        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
            float[] arMatrix = ARToolKit.getInstance().queryMarkerTransformation(markerID);

            if (!MainActivity.calibrateTF) {
                gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
                caliSquarePoints.draw(gl);
            } else {
                gl.glLoadMatrixf(MainActivity.resultMatrix, 0);
                gl.glMultMatrixf(arMatrix, 0);
                caliSquarePointsDD.draw(gl);
            }

            switch (MainActivity.CALI_STATE) {
                case 0: {
                }
                break;

                case 1: {

                }
                break;

                case 2: {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, 0, -200);
                    caliSquarePoints.draw(gl);
                }
                break;

                case 3: {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, 0, -300);
                    caliSquarePoints.draw(gl);
                }
                break;

                case 4: {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, 0, -400);
                    caliSquarePoints.draw(gl);
                }
                break;
            }

        }

    }


}