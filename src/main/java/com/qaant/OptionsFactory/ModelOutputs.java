package com.qaant.OptionsFactory;

public class ModelOutputs {
    private String modelName;

    private double prima,delta,gamma,vega,theta,rho;

    public ModelOutputs(String modelName, double prima, double delta, double gamma, double vega, double theta, double rho) {
        this.modelName = modelName;
        this.prima = prima;
        this.delta = delta;
        this.gamma = gamma;
        this.vega = vega;
        this.theta = theta;
        this.rho = rho;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public double getPrima() {
        return prima;
    }

    public void setPrima(double prima) {
        this.prima = prima;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getVega() {
        return vega;
    }

    public void setVega(double vega) {
        this.vega = vega;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getRho() {
        return rho;
    }

    public void setRho(double rho) {
        this.rho = rho;
    }
}
