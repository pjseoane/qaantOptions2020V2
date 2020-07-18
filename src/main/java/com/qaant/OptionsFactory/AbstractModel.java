package com.qaant.OptionsFactory;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;

public abstract class AbstractModel implements Imodelable {
    protected UnderlyingAsset underlying;
    protected OptionElements option;
    protected double prima,delta,gamma,vega,theta,rho;
    protected double[] derivatives=new double[10];
    protected String modelName;
    protected double startTime;
    protected double underlyingValue;
    protected double divYield;
    protected double strike;
    protected double rate;
    protected double modelVlt;
    protected double dayYear;
    protected char tipoContrato;
    protected char tipoEjercicio;
    protected char optionType;
    protected double elapsedTime;
    protected double callPutFlag;
    protected int steps;

    public AbstractModel() {}

    public AbstractModel(UnderlyingAsset underlying, OptionElements option) {
        this.underlying     = underlying;
        this.option         = option;
        underlyingValue     = underlying.getUnderlyingValue();
        tipoContrato        = underlying.getTipoContrato();
        divYield            = underlying.getUnderlyingDividendYield();
        tipoEjercicio       = option.getExerciseType();
        strike              = option.getStrike();
        rate                = option.getInterestRate();
        modelVlt            = option.getImpliedVlt();
        dayYear             = option.getDaysToExpiration()/365;
        optionType          = option.getOptionType();
        callPutFlag         = (optionType == OptionElements.CALL) ? 1 : -1;

    }

    @Override
    public double[] getDerivatives() {
        derivatives[0] = prima;
        derivatives[1] = delta;
        derivatives[2] = gamma;
        derivatives[3] = vega;
        derivatives[4] = theta;
        derivatives[5] = rho;
        derivatives[9]= elapsedTime;

        return derivatives;
    }

    @Override
    public double getPrima() {
        return prima;
    }

    @Override
    public double getDelta() {
        return delta;
        }

    @Override
    public double getGamma() {
        return gamma;
    }
    @Override
    public double getVega() {
        return vega;
    }
    @Override
    public double getTheta() {
        return theta;
    }
    @Override
    public double getRho() {
        return rho;
    }
    public UnderlyingAsset getUnderlying() {
        return underlying;
    }

    public OptionElements getOption() {
        return option;
    }

    @Override
    public double getImpliedVltVega(){
       return modelVlt+ (option.getOptionMktPrice()-prima)/vega/100;
    }
    @Override
    public double getImpliedVlt(){
        double optMktPrice;
        optMktPrice=option.getOptionMktPrice();
        if (optMktPrice!=0) {
            double ivVega = getImpliedVltVega();

            double ivMin = ivVega * 0.95;
            double ivMax = ivVega * 1.05;
            //System.out.println("ivVega :"+ivVega+", min :"+ivMin+", max :"+ivMax);
            UnivariateFunction f = xVlt -> option.getOptionMktPrice() - getPrimaWithThisVlt(xVlt);
            BisectionSolver solver = new BisectionSolver(0.000001);

            return (solver.solve(100, f, ivMin, ivMax));
        }else{
            return modelVlt;
        }
    }

    @Override
    public double getImpliedVlt(double mktPrice) {
        option.setOptionMktPrice(mktPrice);
        return getImpliedVlt();
    }

/*
    public double getImpliedVlt2(){
        UnivariateFunction f = xVlt -> option.getOptionMktPrice()- getPrimaWithThisVlt(xVlt);

        double ivMax;
        double ivMin;

        if (option.getOptionMktPrice()>=prima) { //hay que subir la vlt
            ivMin = modelVlt;
            ivMax = modelVlt * 3.0;
        }else{
            ivMin=0.0;
            ivMax=modelVlt;
        }

        BisectionSolver solver= new BisectionSolver(0.00001); //precision()
        double impliedVol=solver.solve(100, f, ivMin,ivMax); //max iteration, funcion,min, max
        return impliedVol;
    }
*/
    abstract double getPrimaWithThisVlt(double vlt);
    //Esta func la reseulve cada modelo

    @Override
    public String toString() {
        return "AbstractModel{" +
                "modelName='" + modelName + '\'' +
                ", prima=" + prima +
                ", delta=" + delta +
                ", gamma=" + gamma +
                ", vega=" + vega +
                ", theta=" + theta +
                ", rho=" + rho +
                ", underlyingValue=" + underlyingValue +
                ", divYield=" + divYield +
                ", strike=" + strike +
                ", rate=" + rate +
                ", modelVlt=" + modelVlt +
                ", tipoContrato=" + tipoContrato +
                ", optionType=" + optionType +
                ", optionMktPrice=" + option.getOptionMktPrice() +
                ", steps="+steps+
                '}';
    }

    public String getModelName(){
        return modelName;
    }

}
