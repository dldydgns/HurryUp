package com.example.hurryup.ui.home;

public class Bias {
    public double x;
    public double y;

    public Bias(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Bias(int state){
        state -= 1;

        //  0 1 2
        //  3 4 5
        //  6 7 8
        double[] stateToBias = { 0.20, 0.5, 0.8 };

        this.x = stateToBias[state % 3];
        this.y = stateToBias[state / 3];
    }
}
