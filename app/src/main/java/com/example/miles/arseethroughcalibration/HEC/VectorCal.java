package com.example.miles.arseethroughcalibration.HEC;

/**
 * Created by miles on 2015/8/11.
 */
public class VectorCal {

    public static float[] getMapPosition(double latitude, double longitude) {
        float[] gps_zero = new float[]{22.996597f, 120.219615f};
        float gps_scale = 153000f;

        float x = (float) (longitude - gps_zero[1]) * gps_scale;
        float y = (float) (latitude - gps_zero[0]) * gps_scale;

        return new float[]{x, y};
    }

    public static float getAngleDeg(float[] p1, float[] p2) {
        float ans = (float) (Math.acos(dot(p1, p2) / (magnitude(p1) * magnitude(p2))) / 3.1415926 * 180.0);
        if (Float.isNaN(ans)) {
            return Float.NaN;
        } else {
            return ans;
        }
    }

    public static float[] getVecByPoint(float[] p1, float[] p2) {
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = p2[i] - p1[i];
        }
        return result;
    }

    public static float[] getNormByPtArray(float[] ptArray) {
        float[] result = new float[ptArray.length];
        for (int i = 0; i < ptArray.length / 9; i++) {
            float[] temp;
            float[] v1;
            float[] v2;

            int k = 9 * i;

            float[] p1 = new float[]{ptArray[k], ptArray[k + 1], ptArray[k + 2]};
            float[] p2 = new float[]{ptArray[k + 3], ptArray[k + 4], ptArray[k + 5]};
            float[] p3 = new float[]{ptArray[k + 6], ptArray[k + 7], ptArray[k + 8]};

            v1 = getVecByPoint(p1, p2);
            normalize(v1);
            v2 = getVecByPoint(p2, p3);
            normalize(v2);

            temp = cross(v1, v2);
            normalize(temp);

            result[k] = temp[0];
            result[k + 1] = temp[1];
            result[k + 2] = temp[2];

            result[k + 3] = temp[0];
            result[k + 4] = temp[1];
            result[k + 5] = temp[2];

            result[k + 6] = temp[0];
            result[k + 7] = temp[1];
            result[k + 8] = temp[2];
        }
        return result;
    }

    public static float dot(float[] v1, float[] v2) {
        float res = 0;
        for (int i = 0; i < v1.length; i++)
            res += v1[i] * v2[i];
        return res;
    }

    public static float[] cross(float[] p1, float[] p2) {
        float[] result = new float[3];
        result[0] = p1[1] * p2[2] - p2[1] * p1[2];
        result[1] = p1[2] * p2[0] - p2[2] * p1[0];
        result[2] = p1[0] * p2[1] - p2[0] * p1[1];
        normalize(result);
        return result;
    }

    public static void normalize(float[] vector) {
        scalarMultiply(vector, 1 / magnitude(vector));
    }

    public static void scalarMultiply(float[] vector, float scalar) {
        for (int i = 0; i < vector.length; i++)
            vector[i] *= scalar;
    }

    public static float magnitude(float[] vector) {
        return (float) Math.sqrt(vector[0] * vector[0] +
                vector[1] * vector[1] +
                vector[2] * vector[2]);
    }

    public static float[] RodriguesMatrix(float angle, float[] axis){
        normalize(axis);
        return new float[]{

        };
    }
}
