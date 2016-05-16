package com.example.miles.arseethroughcalibration;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import com.example.miles.arseethroughcalibration.CalibrationDrawing.CaliLine;
import com.example.miles.arseethroughcalibration.CalibrationDrawing.CaliSquarePoints;
import com.example.miles.arseethroughcalibration.CalibrationDrawing.CaliSquarePointsDD;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class SimpleRenderer extends ARRenderer {

    public static int markerID = -1;
    private CaliLine caliLine = new CaliLine();
    private CaliSquarePoints caliSquarePoints = new CaliSquarePoints();
    private CaliSquarePointsDD caliSquarePointsDD = new CaliSquarePointsDD();
    public static float[] HECMatrix;
    private float rate;


    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {

        markerID = ARToolKit.getInstance().addMarker("single;Data/patt.hiro;80");
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
        gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, MainActivity.viewAngle, rate, 0.1f, 1000000.0f);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CCW);

        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // If the marker is visible, apply its transformation, and draw a cube
        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {

            //Log.d("GET?", "" + MainActivity.CALI_DONE);

            if (MainActivity.CALI_DONE) {
                gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
                gl.glMultMatrixf(HECMatrix, 0);
                caliSquarePointsDD.draw(gl);
            }


            switch (MainActivity.CALI_STATE) {
                //**angle**//
                case 0: {

                }
                break;

                case 1: {
                    gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
                    caliSquarePoints.draw(gl);
                    caliLine.draw(gl);
                }
                break;
                //**angle**//

                //**Matrix**//
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11: {
                    float[] temp = new float[16];
                    Matrix.transposeM(temp, 0, MainActivity.Base[MainActivity.CALI_STATE - 2], 0);
                    gl.glLoadMatrixf(temp, 0);
                    caliSquarePoints.draw(gl);
                }
                break;
                //**Matrix**//

                case 12: {
                }
                break;
            }
        }

    }


}