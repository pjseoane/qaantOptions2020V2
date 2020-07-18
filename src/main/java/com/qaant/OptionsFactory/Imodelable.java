package com.qaant.OptionsFactory;

public interface Imodelable {
    public double [] getDerivatives();
    public String getModelName();
    public double getPrima();
    public double getDelta();
    public double getGamma();
    public double getVega();
    public double getTheta();
    public double getRho();
    public double getImpliedVlt();
    public double getImpliedVlt(double mkt);
    public double getImpliedVltVega();
    //public double getIVTurbo();

    public String toString();

}
