package com.example.cargoshipapp;

import com.github.mikephil.charting.utils.Utils;

public class StandardDeviation {
    double[] PitchCurrent = new double[60];
    double[] RollCurrent = new double[60];
    int avgCounter = 0;
    int avgStdDevPitch = 0;
    int avgStdDevRoll = 0;
    int counter = 0;
    double[] currentStandardDeviation = new double[2];
    double[] stdDeviationAvg = new double[10];

    public void AccelData(double d, double d2) {
        if (this.counter < 60) {
            this.PitchCurrent[this.counter] = d;
            this.RollCurrent[this.counter] = d2;
            this.counter++;
            return;
        }
        stdDeviation(this.PitchCurrent, this.RollCurrent);
        this.counter = 0;
    }

    public void stdDeviation(double[] dArr, double[] dArr2) {
        double[] dArr3 = dArr;
        double[] dArr4 = dArr2;
        int length = dArr3.length;
        double d = Utils.DOUBLE_EPSILON;
        for (double d2 : dArr3) {
            d += d2;
        }
        double d3 = (double) length;
        double d4 = d / d3;
        double d5 = Utils.DOUBLE_EPSILON;
        for (double d6 : dArr3) {
            d5 += Math.pow(d6 - d4, 2.0d);
        }
        double sqrt = Math.sqrt(d5 / d3);
        int length2 = dArr4.length;
        double d7 = Utils.DOUBLE_EPSILON;
        for (double d8 : dArr4) {
            d7 += d8;
        }
        double d9 = (double) length2;
        double d10 = d7 / d9;
        double d11 = Utils.DOUBLE_EPSILON;
        for (double d12 : dArr4) {
            d11 += Math.pow(d12 - d10, 2.0d);
        }
        double sqrt2 = Math.sqrt(d11 / d9);
        this.currentStandardDeviation[0] = sqrt;
        this.currentStandardDeviation[1] = sqrt2;
        if (this.avgCounter < 10) {
            this.avgStdDevPitch = (int) (((double) this.avgStdDevPitch) + this.currentStandardDeviation[0]);
            this.avgStdDevRoll = (int) (((double) this.avgStdDevRoll) + this.currentStandardDeviation[1]);
            this.avgCounter++;
            return;
        }
        this.stdDeviationAvg[0] = (double) (this.avgStdDevPitch / 10);
        this.stdDeviationAvg[1] = (double) (this.avgStdDevRoll / 10);
        this.avgStdDevPitch = 0;
        this.avgStdDevRoll = 0;
        this.avgCounter = 0;
    }
}