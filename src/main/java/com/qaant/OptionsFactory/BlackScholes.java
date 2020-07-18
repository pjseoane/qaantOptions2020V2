package com.qaant.OptionsFactory;


import org.apache.commons.math3.distribution.NormalDistribution;

public class BlackScholes extends AbstractModel {

    public BlackScholes() {}

    public BlackScholes(UnderlyingAsset underlying, OptionElements option) {
        super(underlying, option);
        modelName="Black-Scholes";
        option.setExerciseType('E');
    }

    public Imodelable run(){
        startTime = System.currentTimeMillis();
        double q=0;

        switch(tipoContrato) {
            case UnderlyingAsset.STOCK:
                q= divYield;
                break;
            case UnderlyingAsset.FUTURES:
                q= rate;
                break;

        }
        double underlyingNPV=underlyingValue*Math.exp(-q*dayYear);
        double z= Math.exp(-rate*dayYear);
        double ww=Math.exp(-q*dayYear); //drift

        double d1= (Math.log(underlyingNPV *Math.exp(q*dayYear)/ strike) + dayYear * (rate - q + modelVlt * modelVlt / 2)) / (modelVlt * Math.sqrt(dayYear));
        double d2 = d1 - modelVlt * Math.sqrt(dayYear);
        double cndfd1 = new NormalDistribution().cumulativeProbability(d1);
        double cndfd2 = new NormalDistribution().cumulativeProbability(d2);
        double pdfd1 = new NormalDistribution().density(d1);

        gamma = pdfd1 * ww / (underlyingNPV * modelVlt * Math.sqrt(dayYear));
        vega = underlyingNPV * Math.sqrt(dayYear) * pdfd1 / 100;

     //   switch(optionType) {

            if( optionType==OptionElements.CALL) {
                prima = underlyingNPV * cndfd1 - z * strike * cndfd2;
                delta = ww * cndfd1;
                theta = (-(underlyingNPV * modelVlt * pdfd1 / (2 * Math.sqrt(dayYear))) - strike * rate * z * cndfd2 + q * underlyingNPV * cndfd1) / 365;
                rho = strike * dayYear * Math.exp(-(rate - q) * dayYear) * cndfd2 / 1000;
            }else {
                //case OptionElements.PUT:
                double cndf_d1 = new NormalDistribution().cumulativeProbability(-d1);
                double cndf_d2 = new NormalDistribution().cumulativeProbability(-d2);

                prima = -underlyingNPV * cndf_d1 + z * strike * cndf_d2;
                delta = ww * (cndfd1 - 1);
                theta = (-(underlyingNPV * modelVlt * pdfd1 / (2 * Math.sqrt(dayYear))) + strike * rate * z * cndf_d2 - q * underlyingNPV * cndf_d1) / 365;
                rho = -strike * dayYear * Math.exp(-(rate - q) * dayYear) * cndf_d2 / 1000;
            }//  break;
     //   }
        elapsedTime= System.currentTimeMillis()-startTime;
        return this ;
    }

    @Override
    public double getPrimaWithThisVlt(double vlt){
        return (new BlackScholes(underlying,option.setImpliedVlt(vlt)).run().getPrima());

    }
}
