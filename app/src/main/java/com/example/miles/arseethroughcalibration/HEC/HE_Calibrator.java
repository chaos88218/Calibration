package com.example.miles.arseethroughcalibration.HEC;

import org.ejml.simple.SimpleMatrix;

/**
 * Created by miles on 2016/5/3.
 */
public class HE_Calibrator {
    private float[][] HEC_Matrix;
    private float[][] Base_Matrix;
    private SimpleMatrix CaliMatrix;
    private int HEC_Num = 0;

    //**********Constructor

    public HE_Calibrator(float[][] in_Base) {
        this.HEC_Matrix = new float[10][16];
        this.Base_Matrix = new float[10][16];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 16; j++) {
                this.Base_Matrix[i][j] = in_Base[i][j];
            }
        }
    }

    public HE_Calibrator(float[][] in_Hec, float[][] in_Base) {
        this.HEC_Matrix = new float[10][16];
        this.Base_Matrix = new float[10][16];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 16; j++) {
                this.HEC_Matrix[i][j] = in_Hec[i][j];
                this.Base_Matrix[i][j] = in_Base[i][j];
            }
        }
    }

    //**********Settings

    public boolean Add_HEC(float[] in_Hec){
        if(HEC_Num >= 10){
            return false;
        }
        HEC_Num++;

        for (int j = 0; j < 16; j++) {
            this.HEC_Matrix[HEC_Num][j] = in_Hec[j];
        }

        return true;
    }

    public boolean Add_HEC(float[][] in_Hec){
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 16; j++) {
                this.HEC_Matrix[i][j] = in_Hec[i][j];
            }
        }
        return true;
    }

    public SimpleMatrix getResultMatrix(){
        return CaliMatrix;
    }

    public float[] getResultMatrixf(){
        return new float[]{ (float)CaliMatrix.get(0, 0), (float)CaliMatrix.get(1, 0), (float)CaliMatrix.get(2, 0), (float)CaliMatrix.get(3, 0),
                (float)CaliMatrix.get(0, 1), (float)CaliMatrix.get(1, 1), (float)CaliMatrix.get(2, 1), (float)CaliMatrix.get(3, 1),
                (float)CaliMatrix.get(0, 2), (float)CaliMatrix.get(1, 2), (float)CaliMatrix.get(2, 2), (float)CaliMatrix.get(3, 2),
                (float)CaliMatrix.get(0, 3), (float)CaliMatrix.get(1, 3), (float)CaliMatrix.get(2, 3), (float)CaliMatrix.get(3, 3)};
    }

    //**********Caculation
    public String Calibration() {
        int size = 10;
        int allPose = (size * size - size) / 2;
        SimpleMatrix A = new SimpleMatrix(3 * allPose, 3);
        SimpleMatrix B = new SimpleMatrix(3 * allPose, 1);
        SimpleMatrix A_t = new SimpleMatrix(3 * allPose, 3);
        SimpleMatrix B_t = new SimpleMatrix(3 * allPose, 1);
        int count = 0;

        SimpleMatrix[] bHg = new SimpleMatrix[size];
        SimpleMatrix[] wHc = new SimpleMatrix[size];

        for (int i = 0; i < size; i++) {
            bHg[i] = new SimpleMatrix(fA2dM_4(HEC_Matrix[i]));
            wHc[i] = new SimpleMatrix(fA2dM_4(Base_Matrix[i]));
            wHc[i] = wHc[i].transpose();
            wHc[i] = wHc[i].invert();
        }


        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                SimpleMatrix Hg = new SimpleMatrix((bHg[j].invert()).mult(bHg[i]));
                SimpleMatrix Hc = new SimpleMatrix((wHc[j]).mult(wHc[i].invert()));

                SimpleMatrix Pg = new SimpleMatrix(3, 1, true, rot2quat(Hg));
                Pg = Pg.scale(2);
                SimpleMatrix Pc = new SimpleMatrix(3, 1, true, rot2quat(Hc));
                Pc = Pc.scale(2);

                count++;
                double[][] aTemp = skew(Pg.plus(Pc));
                SimpleMatrix bTemp = Pc.minus(Pg);

                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        A.set(3 * count - 3 + k, l, aTemp[k][l]);
                    }
                    B.set(3 * count - 3 + k, 0, bTemp.get(k));
                }
                //end
            }
        }

        SimpleMatrix Pcg = A.solve(B);
        Pcg = Pcg.scale(2).divide(Math.sqrt(1 + (Pcg.transpose()).mult(Pcg).get(0)));
        Pcg = quat2rot(Pcg.divide(2));

        count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                SimpleMatrix Hg = new SimpleMatrix((bHg[j].invert()).mult(bHg[i]));
                SimpleMatrix Hc = new SimpleMatrix((wHc[j]).mult(wHc[i].invert()));

                count++;
                SimpleMatrix a_t_temp = (Hg.extractMatrix(0, 3, 0, 3)).minus(SimpleMatrix.identity(3));
                SimpleMatrix bTemp = ((Pcg.extractMatrix(0, 3, 0, 3)).mult(Hc.extractMatrix(0, 3, 3, 4))).minus(Hg.extractMatrix(0, 3, 3, 4));

                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        A_t.set(3 * count - 3 + k, l, a_t_temp.get(k, l));
                    }
                    B_t.set(3 * count - 3 + k, 0, bTemp.get(k));
                }
                //end
            }
        }

        SimpleMatrix Tcg = A_t.solve(B_t);
        Pcg.set(0, 3, Tcg.get(0));
        Pcg.set(1, 3, Tcg.get(1));
        Pcg.set(2, 3, Tcg.get(2));

        CaliMatrix = new SimpleMatrix(Pcg).invert();

        return CaliMatrix.toString();
    }

    private double[][] fA2dM_4(float[] inMatrix) {
        return new double[][]{
                {inMatrix[0], inMatrix[4], inMatrix[8], inMatrix[12]},
                {inMatrix[1], inMatrix[5], inMatrix[9], inMatrix[13]},
                {inMatrix[2], inMatrix[6], inMatrix[10], inMatrix[14]},
                {inMatrix[3], inMatrix[7], inMatrix[11], inMatrix[15]},
        };
    }

    private double[] rot2quat(SimpleMatrix inMatrix) {
        SimpleMatrix Rm = inMatrix.extractMatrix(0, 3, 0, 3);
        double w4 = 2 * Math.sqrt(1 + Rm.trace());

        return new double[]{
                (Rm.get(2, 1) - Rm.get(1, 2)) / w4,
                (Rm.get(0, 2) - Rm.get(2, 0)) / w4,
                (Rm.get(1, 0) - Rm.get(0, 1)) / w4
        };
    }

    private SimpleMatrix quat2rot(SimpleMatrix inPcg) {
        SimpleMatrix tempP = (inPcg.transpose()).mult(inPcg);
        double tempP_d = tempP.get(0);

        double tempW = Math.sqrt(1 - tempP_d);

        SimpleMatrix returnMatrix = SimpleMatrix.identity(4);

        SimpleMatrix temprM = (inPcg.mult(inPcg.transpose())).scale(2);
        temprM = (temprM.plus((new SimpleMatrix(skew(inPcg)))).scale(2 * tempW));
        temprM = temprM.plus(SimpleMatrix.identity(3));
        temprM = (temprM.minus(SimpleMatrix.diag(tempP_d, tempP_d, tempP_d).scale(2)));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                returnMatrix.set(i, j, temprM.get(i, j));
            }
        }

        return returnMatrix;
    }

    private double[][] skew(SimpleMatrix inVector) {
        return new double[][]{
                {0, -inVector.get(2), inVector.get(1)},
                {inVector.get(2), 0, -inVector.get(0)},
                {-inVector.get(1), inVector.get(0), 0}
        };
    }
}
