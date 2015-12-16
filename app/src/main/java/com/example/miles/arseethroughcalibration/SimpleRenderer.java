package com.example.miles.arseethroughcalibration;

import android.opengl.GLU;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class SimpleRenderer extends ARRenderer {

	public static int markerID = -1;
	private CaliSquarePoints caliSquareCube = new CaliSquarePoints();
	public CaliCube caliCube = new CaliCube();
	private float rate;
	/**
	 * Markers can be configured here.
	 */
	@Override
	public boolean configureARScene() {

		markerID = ARToolKit.getInstance().addMarker("single;Data/patt.hiro;40");
		if (markerID < 0) return false;
		return true;	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		super.onSurfaceChanged(gl, w, h);
		rate = (float) w / (float) h;
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

		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, MainActivity.viewAngle, rate, 0.1f, 100000.0f);
		gl.glMatrixMode(gl.GL_MODELVIEW);
		gl.glLoadIdentity();

	
		gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);        
    	gl.glFrontFace(GL10.GL_CCW);
    			
		// If the marker is visible, apply its transformation, and draw a cube
		if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
			caliSquareCube.draw(gl);

			if (MainActivity.firstMatrix != null)
			{
				gl.glLoadMatrixf(MainActivity.firstMatrix, 0);
				caliSquareCube.draw(gl);
				caliCube.draw(gl);
			}
			if(MainActivity.resultMatrix != null)
			{
				gl.glLoadIdentity();
				gl.glMultMatrixf(MainActivity.resultMatrix, 0);
				gl.glMultMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);
				caliCube.draw(gl);
			}
		}

	}


}