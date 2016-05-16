package com.example.miles.arseethroughcalibration;

import android.graphics.PixelFormat;
import android.graphics.Point;
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

import com.example.miles.arseethroughcalibration.HEC.HE_Calibrator;

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
    public static boolean CALI_DONE;
    public static int CALI_STATE = 0;                  //0, Angle Adjustment; 1, 1st Matrix; 2 2nd Matrix; 3, cali result;

    public static float[][] Base = new float[][]{
            {1, 0, 0, 25, 0, 1, 0, 10, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, -25, 0, 1, 0, 10, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1, 0, 10, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, 25, 0, 1, 0, 0, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, -25, 0, 1, 0, 0, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, 25, 0, 1, 0, -10, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, -25, 0, 1, 0, -10, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1, 0, -10, 0, 0, 1, -900, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, -900, 0, 0, 0, 1},
    };

    private HE_Calibrator he_calibrator = new HE_Calibrator(Base);

    public float[][] cameraMatrix = new float[10][];

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

                    for (int i = 0; i < 16; i++) {
                        cameraMatrix[0] = new float[0];
                    }
                    calibrateTF = false;
                    angleSeekBar.setVisibility(View.VISIBLE);
                    textView.setText("State: Adjust Angle.");
                }
                break;

                case 1: {
                    CALI_DONE = false;
                    angleSeekBar.setVisibility(View.GONE);
                    textView.setText("State: Adjust FM.");
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
                    cameraMatrix[CALI_STATE - 2] = new float[16];
                    cameraMatrix[CALI_STATE - 2] = ARToolKit.getInstance().queryMarkerTransformation(SimpleRenderer.markerID);
                    Log.e("get " + (CALI_STATE - 2), " Matrix " + (-10 + (MainActivity.CALI_STATE % 3) * 10));
                    textView.setText("State: Confirm " + (CALI_STATE - 2));
                }
                break;
                //**Matrix**//

                case 12: {
                    he_calibrator.Add_HEC(cameraMatrix);
                    String str = he_calibrator.Calibration();
                    calibrateTF = true;
                    SimpleRenderer.HECMatrix = he_calibrator.getResultMatrixf();
                    String str2 = SimpleRenderer.HECMatrix[0] + " " + SimpleRenderer.HECMatrix[4] + " " + SimpleRenderer.HECMatrix[8] + " " + SimpleRenderer.HECMatrix[12] + "\n"
                            + SimpleRenderer.HECMatrix[1] + " " + SimpleRenderer.HECMatrix[5] + " " + SimpleRenderer.HECMatrix[9] + " " + SimpleRenderer.HECMatrix[13] + "\n"
                            + SimpleRenderer.HECMatrix[2] + " " + SimpleRenderer.HECMatrix[6] + " " + SimpleRenderer.HECMatrix[10] + " " + SimpleRenderer.HECMatrix[14] + "\n"
                            + SimpleRenderer.HECMatrix[3] + " " + SimpleRenderer.HECMatrix[7] + " " + SimpleRenderer.HECMatrix[11] + " " + SimpleRenderer.HECMatrix[15] + "\n";
                    textView.setText("State: Calibration Done\n" + str + "\n" + str2);
                    CALI_DONE = true;
                }
                break;
            }
            CALI_STATE = (CALI_STATE + 1) % 13;
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
                    str += (SimpleRenderer.HECMatrix[i] + "\t");
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
