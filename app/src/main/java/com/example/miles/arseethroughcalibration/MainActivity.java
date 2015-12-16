package com.example.miles.arseethroughcalibration;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

public class MainActivity extends ARActivity {

    private FrameLayout aRlayout;
    private RelativeLayout aRParentLayout;
    private Button button;
    private SeekBar angleSeekBar;

    private boolean GL_TRANSLUCENT = true;
    public int CALI_STATE = 0;

    public static float[] firstMatrix;
    public static float[] secondMatrix;
    public static float[] resultMatrix;

    public static float viewAngle = 15.0f;
    public float viewDistance = 500.0f;
    public static boolean calibrateTF = false;

    int width;
    int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aRlayout = (FrameLayout) findViewById(R.id.ARLayout);
        aRParentLayout = (RelativeLayout) findViewById(R.id.ARParentLayout);
        button = (Button) findViewById(R.id.button);
        angleSeekBar = (SeekBar) findViewById(R.id.angleSeekBar);

        //****Full Screen****//
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = (int) (width * 3.0f / 4.0f);

        RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        ll.setMargins(0, (int) -((height - size.y) / 2.0f), 0, (int) -((height - size.y) / 2.0f));
        aRParentLayout.setLayoutParams(ll);
        //****Full Screen****//

        aRlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translucent();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstMatrix(15);
                calibration();
            }
        });

        //TODO: SeekBar adjust
        angleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                viewAngle = progress / 10.0f;
                setFirstMatrix(viewAngle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(seekBar.getContext(), viewAngle + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected ARRenderer supplyRenderer() {
        return new SimpleRenderer();
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return aRlayout;
    }

    protected void translucent() {
        GL_TRANSLUCENT = !GL_TRANSLUCENT;
        if (GL_TRANSLUCENT) {
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            Log.e("GL", "GL_TRANSLUCENT");
        } else {
            glView.getHolder().setFormat(PixelFormat.RGB_565);
            Log.e("GL", "RGB_565");
        }
    }

    protected void calibration() {
        if (ARToolKit.getInstance().queryMarkerVisible(SimpleRenderer.markerID)) {
            switch (CALI_STATE) {
                case 0: {
                    setFirstMatrix(15);
                    Log.e("get firstMatrix", "get: " + firstMatrix[0]);
                    calibrateTF = false;
                }
                break;

                case 1: {
                    float[] temp = new float[16];
                    secondMatrix = ARToolKit.getInstance().queryMarkerTransformation(SimpleRenderer.markerID);
                    Log.e("get secondMatrix", "get: " + secondMatrix[0]);
                    Matrix.invertM(temp, 0, secondMatrix, 0);
                    resultMatrix = new float[16];
                    Matrix.multiplyMM(resultMatrix, 0, firstMatrix, 0, temp, 0);
                    firstMatrix = new float[0];
                    calibrateTF = true;
                }
                break;
            }
        }
        CALI_STATE = (CALI_STATE + 1) % 2;
    }

    private void setFirstMatrix(float angle) {
        firstMatrix = new float[16];
        Matrix.setIdentityM(firstMatrix, 0);
        float l = (float) (viewDistance * Math.tan(15.0 / 180 * Math.PI));
        Matrix.translateM(firstMatrix, 0, 0, 0, (float) (-viewDistance + (l / Math.tan(15.0f / 180.0 * Math.PI)) - (l / Math.tan(angle / 180.0 * Math.PI))));
        Matrix.rotateM(firstMatrix, 0, 20, 1, 0, 0);
    }
}
