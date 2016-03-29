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

    private float HECDMatrix[] = new float[]{
            -0.6390f, -0.7177f, 0.2767f, 0,
            -0.4724f, -0.6500f, -0.5952f, 0,
            0.6071f, -0.2496f, 0.7544f, 0,
            6.9699f, 0, 55.3345f, 1.0000f};

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
            gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
            gl.glMultMatrixf(HECDMatrix, 0);
            caliSquarePoints.draw(gl);

//            gl.glLoadMatrixf(HECDMatrix, 0);
//            gl.glMultMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
//            caliSquarePoints.draw(gl);

            switch (MainActivity.CALI_STATE) {
                //**angle**//
                case 0: {
                    gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
                    caliSquarePoints.draw(gl);
                    caliLine.draw(gl);
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
                case 2: {
                    gl.glLoadIdentity();
                    gl.glTranslatef(20, 0, -800);
                    gl.glRotatef(45, 0, 1, 0);
                    caliSquarePoints.draw(gl);

                }
                break;

                case 3: {
                    gl.glLoadIdentity();
                    gl.glTranslatef(-20, 0, -800);
                    gl.glRotatef(-45, 0, 1, 0);
                    caliSquarePoints.draw(gl);
                }
                break;

                case 4: {
                    gl.glLoadIdentity();
                    gl.glTranslatef(0, 0, -800);
                    //gl.glRotatef(-45, 1, 0, 0);
                    caliSquarePoints.draw(gl);
                }
                break;
                //**Matrix**//

                case 5: {

                }
                break;
            }
        }

    }


}