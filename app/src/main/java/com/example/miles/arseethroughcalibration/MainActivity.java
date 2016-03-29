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
import android.widget.TextView;
import android.widget.Toast;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends ARActivity {

    private FrameLayout aRlayout;
    private RelativeLayout aRParentLayout;
    private Button caliButton, saveParaButton;
    private SeekBar angleSeekBar;
    private TextView textView;

    private boolean GL_TRANSLUCENT = true;
    public static int CALI_STATE = 0;                  //0, Angle Adjustment; 1, 1st Matrix; 2 2nd Matrix; 3, cali result;

    public static float[] firstMatrix;
    public static float[] secondMatrix;
    public static float[] thirdMatrix;

    public static float[] resultMatrix1;
    public static float[] resultMatrix2;

    public static float viewAngle = 9.2f;
    public static boolean calibrateTF = false;

    int width;
    int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aRlayout = (FrameLayout) findViewById(R.id.ARLayout);
        aRParentLayout = (RelativeLayout) findViewById(R.id.ARParentLayout);
        caliButton = (Button) findViewById(R.id.button);
        saveParaButton = (Button) findViewById(R.id.button2);
        angleSeekBar = (SeekBar) findViewById(R.id.angleSeekBar);
        textView = (TextView) findViewById(R.id.textView);
        angleSeekBar.setVisibility(View.GONE);

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

        caliButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibration();
            }
        });

        saveParaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeParaFile();
            }
        });

        //TODO: SeekBar adjust
        angleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                viewAngle = progress / 10.0f;
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
                //**angle**//
                case 0: {
                    firstMatrix = new float[0];
                    secondMatrix = new float[0];
                    thirdMatrix = new float[0];

                    resultMatrix1 = new float[0];
                    resultMatrix2 = new float[0];

                    calibrateTF = false;
                    angleSeekBar.setVisibility(View.VISIBLE);
                    textView.setText("State: Adjust Angle.");
                }
                break;

                case 1: {
                    angleSeekBar.setVisibility(View.GONE);
                    textView.setText("State: Adjust FM.");
                }
                break;
                //**angle**//

                //**Matrix**//
                case 2: {
                    firstMatrix = new float[16];
                    firstMatrix = ARToolKit.getInstance().queryMarkerTransformation(SimpleRenderer.markerID);
                    Log.e("get firstMatrix", "get: " + firstMatrix[0]);
                    textView.setText("State: Confirm FM. Adjust SM");
                }
                break;

                case 3: {
                    float[] temp = new float[16];
                    secondMatrix = ARToolKit.getInstance().queryMarkerTransformation(SimpleRenderer.markerID);
                    Log.e("get secondMatrix", "get: " + secondMatrix[0]);

                    boolean aaa = Matrix.invertM(temp, 0, secondMatrix, 0);
                    resultMatrix1 = new float[16];
                    Matrix.multiplyMM(resultMatrix1, 0, firstMatrix, 0, temp, 0);
                    textView.setText("State: Confirm SM. Adjust TM " + aaa);

                }
                break;

                case 4: {
                    float[] temp = new float[16];
                    thirdMatrix = ARToolKit.getInstance().queryMarkerTransformation(SimpleRenderer.markerID);
                    Log.e("get secondMatrix", "get: " + thirdMatrix[0]);

                    boolean aaa = Matrix.invertM(temp, 0, thirdMatrix, 0);
                    resultMatrix2 = new float[16];
                    Matrix.multiplyMM(resultMatrix2, 0, secondMatrix, 0, temp, 0);
                    textView.setText("State: Confirm TM _ " + aaa);
                }
                break;
                //**Matrix**//

                case 5: {
                    calibrateTF = true;
                    textView.setText("State: Calibration Done");
                }
                break;
            }
            CALI_STATE = (CALI_STATE + 1) % 6;
        }
    }

    protected void writeParaFile() {
        if (calibrateTF) {
            try {
                int times = 0;
                File file = new File("/sdcard/HECmatrix.txt");
                boolean aa = file.createNewFile();
                while (!aa) {
                    times++;
                    file = new File("/sdcard/HECmatrix_" + times + ".txt");
                    aa = file.createNewFile();
                }
                Toast.makeText(caliButton.getContext(), "File Created." + times, Toast.LENGTH_SHORT).show();

                String str = "";

                str = str + viewAngle + "\n";
                for (int i = 0; i < 16; i++) {
                    str += (firstMatrix[i] + "\t");
                }
                str += "\n";
                for (int i = 0; i < 16; i++) {
                    str += (secondMatrix[i] + "\t");
                }
                str += "\n";
                for (int i = 0; i < 16; i++) {
                    str += (thirdMatrix[i] + "\t");
                }
                str += "\n";
                for (int i = 0; i < 16; i++) {
                    str += (resultMatrix1[i] + "\t");
                }
                str += "\n";
                for (int i = 0; i < 16; i++) {
                    str += (resultMatrix2[i] + "\t");
                }

                FileOutputStream fout = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fout);
                outputStreamWriter.write(str);
                outputStreamWriter.close();
                fout.close();

                Toast.makeText(caliButton.getContext(), "Saved.", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.e("Writing files", e + "");
                Toast.makeText(saveParaButton.getContext(), "Saving Wrong.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(saveParaButton.getContext(), "Not Yet.", Toast.LENGTH_SHORT).show();
        }
    }
}
