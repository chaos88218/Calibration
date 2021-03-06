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
    public int CALI_STATE = 0;                  //0, Angle Adjustment; 1, 1st Matrix; 2 2nd Matrix; 3, cali result;

    public static float[] firstMatrix;
    public static float[] secondMatrix;
    public static float[] resultMatrix;

    public static float viewAngle = 9.2f;
    public static boolean calibrateTF = false;
    public static boolean gotFM = false;


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
        Log.d("Para", " " + (int) -((height - size.y) / 2.0f) + " " +(int) -((height - size.y) / 2.0f));
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
                case 0: {
                    gotFM = false;
                    calibrateTF = false;
                    angleSeekBar.setVisibility(View.VISIBLE);
                    textView.setText("步驟一:\n拉動調整角度\n使兩黃線與兩黑線\n兩兩直線距離一致\n完成按下calibration按鈕");
                }
                break;

                case 1: {
                    angleSeekBar.setVisibility(View.GONE);
                    textView.setText("步驟二:\n調整畫面中紅方框位置大小\n使紅方框範圍充滿整個螢幕\n按下calibration按鈕紀錄位置");
                }
                break;

                case 2: {
                    firstMatrix = new float[16];
                    firstMatrix = ARToolKit.getInstance().queryMarkerTransformation(SimpleRenderer.markerID);
                    Log.e("get firstMatrix", "get: " + firstMatrix[0]);
                    textView.setText("步驟三:\n身體相對Marker略往右移\n將上一步驟紀錄的紅框位置\n和實體Marker對齊\n按下calibration按鈕");
                    gotFM = true;

                }
                break;

                case 3: {
                    float[] temp = new float[16];
                    secondMatrix = ARToolKit.getInstance().queryMarkerTransformation(SimpleRenderer.markerID);
                    Log.e("get secondMatrix", "get: " + secondMatrix[0]);
                    boolean aaa = Matrix.invertM(temp, 0, secondMatrix, 0);
                    resultMatrix = new float[16];
                    Matrix.multiplyMM(resultMatrix, 0, firstMatrix, 0, temp, 0);
                    firstMatrix = new float[0];
                    textView.setText("步驟四：\n校正成功與否 :" + aaa + "\n按下calibration按鈕繼續");
                }
                break;

                case 4: {
                    calibrateTF = true;
                    textView.setText("步驟五：可以存檔\n或按下calibration按鈕重新校正" + "\n");
                }
                break;
            }
            CALI_STATE = (CALI_STATE + 1) % 5;
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
                    str += (resultMatrix[i] + "\t");
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