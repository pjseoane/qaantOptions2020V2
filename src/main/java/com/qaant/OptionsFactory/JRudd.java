package com.qaant.OptionsFactory;

public class JRudd extends AbstractModel {

    private boolean noLoop=false;
    private Nodo[][] tablaNodos;

    public JRudd(UnderlyingAsset underlying, OptionElements option) {
       this(underlying, option,500);
    }

    public JRudd(UnderlyingAsset underlying, OptionElements option, boolean noLoop){
        this(underlying, option);
        this.noLoop=noLoop;
    }

    public JRudd(UnderlyingAsset underlying, OptionElements option, int steps) {
        super(underlying, option);
        this.steps = steps;
        modelName="Jarrow-Rudd";
    }


    public Imodelable run(){
        double h = dayYear / steps;
        double drift = (tipoContrato == 'F') ? 1 : Math.exp(rate * h);

        double firstTerm = (rate - 0.5 * Math.pow(modelVlt, 2)) * h;
        double secondTerm = modelVlt * Math.sqrt(h);

        double u = Math.exp(firstTerm + secondTerm);
        double d = Math.exp(firstTerm - secondTerm);

        double p = (drift - d) / (u - d);
        double z = Math.exp(-rate * h);
        tablaNodos = new Nodo[steps + 1][steps + 1];

        for (int i = 0; i < steps + 1; i++) {
            for (int j = 0; j <= i; j++) {
                Nodo nodo = new Nodo(underlyingValue * Math.pow(u, i - j) * Math.pow(d, j));
                nodo.setNodeLevel(i);
                nodo.setNodeNumber(j);

                tablaNodos[i][j] = nodo;
            }
        }

        // BoundaryConditions, work on last level steps
        //double strike = opt.getStrike();
        for (int j = 0; j < steps + 1; j++) {
            tablaNodos[steps][j].setOptionPremiumAtThisNode(
                    payoff(tablaNodos[steps][j].getUnderlyingValueAtThisNode(), strike, callPutFlag));
        }
// Resolving tree Backward
        double oneMinusP = 1 - p;
        double aux;
        for (int i = steps - 1; i >= 0; i--) {

            for (int j = 0; j <= i; j++) {

                aux = (tablaNodos[i + 1][j].getOptionPremiumAtThisNode() * p
                        + tablaNodos[i + 1][j + 1].getOptionPremiumAtThisNode() * (oneMinusP)) * z;

                tablaNodos[i][j].setOptionPremiumAtThisNode(aux);

                if (tipoEjercicio == 'A') {

                    tablaNodos[i][j].setOptionPremiumAtThisNode(Math.max(aux,
                            payoff(tablaNodos[i][j].getUnderlyingValueAtThisNode(), strike, callPutFlag)));
                }
            }
        }
        prima = tablaNodos[0][0].getOptionPremiumAtThisNode();
        delta = (tablaNodos[1][1].getOptionPremiumAtThisNode()
                - tablaNodos[1][0].getOptionPremiumAtThisNode()) / (tablaNodos[1][1].getUnderlyingValueAtThisNode()
                - tablaNodos[1][0].getUnderlyingValueAtThisNode());


        gamma = ((tablaNodos[2][0].getOptionPremiumAtThisNode() - tablaNodos[2][1].getOptionPremiumAtThisNode())
                /(tablaNodos[2][0].getUnderlyingValueAtThisNode()-tablaNodos[2][1].getUnderlyingValueAtThisNode())
                -(tablaNodos[2][1].getOptionPremiumAtThisNode()- tablaNodos[2][2].getOptionPremiumAtThisNode())
                /(tablaNodos[2][1].getUnderlyingValueAtThisNode()-tablaNodos[2][2].getUnderlyingValueAtThisNode()))
                /((tablaNodos[2][0].getUnderlyingValueAtThisNode()-tablaNodos[2][2].getUnderlyingValueAtThisNode())/2);

        /* esta parte anda pero hace todo mas lento
        if (noLoop){
           ;
        }else {
            theta = getTheta();
            vega =  getVega();
            rho =   getRho();
        }
       */

        return this ;

    }
    public double getTheta() {

        double days=option.getDaysToExpiration();
        option.setDaysToExpiration(days-1);
        JRudd op= new JRudd(underlying, option, true);
        op.run();
        option.setDaysToExpiration(days);
        return op.getPrima()-prima;
    }

    public double getVega() {

        double vlt=option.getImpliedVlt();
        option.setImpliedVlt(vlt+0.01);
        JRudd op= new JRudd(underlying, option, true);
        op.run();
        option.setImpliedVlt(vlt);
        return op.getPrima()-prima;

    }
    public double getRho() {

        double rate=option.getInterestRate();
        option.setInterestRate(rate+0.01);
        JRudd op= new JRudd(underlying, option, true);
        op.run();
        option.setInterestRate(rate);
        return op.getPrima()-prima;

    }



    public double payoff(double und, double strike, double callFlag) {
        return Math.max((und - strike) * callFlag, 0);
    }
    @Override
    public double getPrimaWithThisVlt(double vlt){

        Imodelable jrOption = new JRudd(underlying,option.setImpliedVlt(vlt),steps).run();
        return jrOption.getPrima();
    }
    private class Nodo {
        private double underlyingValueAtThisNode;
        private double optionPremiumAtThisNode;
        private int nodeLevel;
        private int nodeNumber;

        public Nodo() {}

        public Nodo(double underlyingValueAtThisNode) {
            //super();
            this.underlyingValueAtThisNode = underlyingValueAtThisNode;
        }
        public double getUnderlyingValueAtThisNode() {
            return underlyingValueAtThisNode;
        }
        public void setUnderlyingValueAtThisNode(double underlyingValueAtThisNode) {
            this.underlyingValueAtThisNode = underlyingValueAtThisNode;
        }
        public double getOptionPremiumAtThisNode() {
            return optionPremiumAtThisNode;
        }
        public void setOptionPremiumAtThisNode(double optionPremiumAtThisNode) {
            this.optionPremiumAtThisNode = optionPremiumAtThisNode;
        }
        public int getNodeLevel() {
            return nodeLevel;
        }
        public void setNodeLevel(int nodeLevel) {
            this.nodeLevel = nodeLevel;
        }
        public int getNodeNumber() {
            return nodeNumber;
        }
        public void setNodeNumber(int nodeNumber) {
            this.nodeNumber = nodeNumber;
        }
    }

}
