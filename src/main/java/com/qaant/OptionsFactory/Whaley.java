package com.qaant.OptionsFactory;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Whaley extends BlackScholes {
   // private final static String modelName=;
    public Whaley() {}

    public Whaley(UnderlyingAsset und, OptionElements opt) {
        super(und, opt);
        modelName="Whaley-Barone-Adesi";

    }

    public Imodelable run(){
        startTime = System.currentTimeMillis();
        //genera una opcion black scholes para las greeks
        super.run();

        if (underlying.getTipoContrato() == UnderlyingAsset.FUTURES
                || option.getOptionType() == OptionElements.PUT) {
            reallyWhaleyModel();
        }
        return this;
    }
    public Imodelable reallyWhaleyModel(){
        option.setExerciseType('A');
        double q=0;

        switch (underlying.getTipoContrato()){
            case UnderlyingAsset.STOCK:
                q= underlying.getUnderlyingDividendYield();
                break;
            case UnderlyingAsset.FUTURES:
                q= rate;
                break;
        }

        double underlyingNPV=underlyingValue*Math.exp(-q*dayYear);
        double h = 1 - Math.exp(-rate * dayYear);
        double alfa = 2 * rate / (modelVlt * modelVlt);
        double beta = 2 * (rate - q) * modelVlt;
        double lambda = (-(beta - 1) + callPutFlag * Math.sqrt((beta - 1) * (beta - 1) + 4 * alfa / h)) / 2;
        double eex = Math.exp(-q * dayYear);

        double s1 = strike;
        double zz = 1 / Math.sqrt(2 * Math.PI);
        double zerror = 1;
        double d1;
        double xx;
        double corr;

        double mBSprima;
        double rhs, lhs,nd1,slope;

        OptionElements opt= new OptionElements('A', optionType, strike, option.getDaysToExpiration(), 0, modelVlt,rate);
        do {
            d1 = (Math.log(s1 / strike) + ((rate - q) + modelVlt / 2) * dayYear) / (modelVlt * Math.sqrt(dayYear));
            xx = (1 - eex * new NormalDistribution().cumulativeProbability(callPutFlag * d1));
            corr = s1 / lambda * xx;

            UnderlyingAsset und = new UnderlyingAsset(s1, modelVlt, divYield, tipoContrato);
            Imodelable bsOpt = new BlackScholes(und, opt).run();

            mBSprima = bsOpt.getPrima();

            rhs = mBSprima + callPutFlag * corr;
            lhs = callPutFlag * (s1 - strike);
            zerror= lhs-rhs;
            nd1= new NormalDistribution().density(d1);
            //nd1=zz*Math.exp(-0.5*d1*d1); //standard Normal prob
            slope=callPutFlag*(1-1/lambda)*xx+1/lambda*(eex*nd1)*1/(modelVlt * Math.sqrt(dayYear));
            s1=s1-zerror/slope;

        } while (Math.abs(zerror) > 0.000001);

        double a= callPutFlag*s1/lambda*xx;

        final double prima = a * Math.pow((underlyingNPV / s1), lambda);
        switch(optionType){

            case OptionElements.CALL:

                if (underlyingNPV >= s1) {
                    this.prima =underlyingNPV-strike;
                }else {
                    this.prima += prima;
                }
                break;

            case OptionElements.PUT:
                if (underlyingNPV < s1) {
                    this.prima =strike-underlyingNPV;
                }else{
                    if (s1==0) {
                        this.prima =-2;

                    }else {
                        this.prima += prima;
                    }
                }
                break;


            default:
                this.prima =-1;
        }

        return this;
    }


    @Override
    public double getPrimaWithThisVlt(double vlt){
        return (new Whaley(underlying,option.setImpliedVlt(vlt)).run().getPrima());
    }
}
