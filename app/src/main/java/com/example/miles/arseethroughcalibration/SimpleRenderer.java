package com.example.miles.arseethroughcalibration;

import android.opengl.GLU;
import android.util.Log;

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
    private CaliCube caliCube = new CaliCube();
    private float rate;
    private float drawingScale;

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
        float GLDRW = (float) (2.0f * 0.1 * Math.tan(MainActivity.viewAngle / 2.0 / 180.0 * Math.PI) * 4.0f / 3.0f);
        float WOTRW = (float) (10.668 / Math.sqrt(337) * 16.0f);
        drawingScale = WOTRW / GLDRW;


        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CCW);

        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // If the marker is visible, apply its transformation, and draw a cube
        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {

            if (!MainActivity.calibrateTF) {
                gl.glPushMatrix();
                gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
                //gl.glScalef(drawingScale, drawingScale, drawingScale);
                caliSquarePoints.draw(gl);
                caliLine.draw(gl);
                gl.glPopMatrix();
            }
            if (MainActivity.calibrateTF) {
                gl.glLoadIdentity();
                gl.glMultMatrixf(MainActivity.resultMatrix, 0);
                gl.glMultMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
                caliCube.draw(gl);
            } else if (MainActivity.firstMatrix != null) {
                gl.glLoadMatrixf(MainActivity.firstMatrix, 0);
                caliSquarePoints.draw(gl);
            }
        }

    }


}