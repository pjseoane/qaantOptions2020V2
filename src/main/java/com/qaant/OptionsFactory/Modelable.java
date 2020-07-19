package com.qaant.OptionsFactory;

public interface Modelable {
    double [] getDerivatives();
    String getModelName();
    double getPrima();
    double getDelta();
    double getGamma();
    double getVega();
    double getTheta();
    double getRho();
    double getImpliedVlt();
    double getImpliedVlt(double mkt);
    double getImpliedVltVega();
    int getSteps();
    String toString();

}
