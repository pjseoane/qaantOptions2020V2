package com.qaant.OptionsFactory;

public class JRudd extends AbstractModel {

    private boolean noLoop=false;
    private Nodo[][] tablaNodos;
    //private double theta2;

    public JRudd(UnderlyingAsset underlying, OptionElements option, boolean noLoop){
        this(underlying, option);
        this.noLoop=noLoop;
    }

    public JRudd(UnderlyingAsset underlying, OptionElements option) {
       this(underlying, option,500);
       //si no se pasan steps hace 500 por default
    }



    public JRudd(UnderlyingAsset underlying, OptionElements option, int steps) {
        super(underlying, option);
        this.steps = steps;
        modelName="Jarrow-Rudd";

    }


    public Modelable run(){
        startTime = System.currentTimeMillis();
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

                //System.out.println(("Tipo ejercicio :"+tipoEjercicio));
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


       // theta2 = (tablaNodos[2][1].getOptionPremiumAtThisNode()-tablaNodos[0][0].getOptionPremiumAtThisNode())/(2*dayYear*365/steps);
        //esta parte anda pero lo hace mas lento
//        if (noLoop){
//           ;
//        }else {
//            theta = getTheta();
//            vega =  getVega();
//            rho =   getRho();
//        }

        elapsedTime= System.currentTimeMillis()-startTime;
        return this ;

    }


    private static JRudd getNewOption(UnderlyingAsset underlying, OptionElements option, int steps){
        JRudd op= new JRudd(underlying, option, steps);
        op.run();
        return op;


    }
    @Override
    public double getTheta() {

        double days=option.getDaysToExpiration();
        option.setDaysToExpiration(days-1);
        JRudd op=getNewOption(underlying, option,steps);

        option.setDaysToExpiration(days);
        return op.getPrima()-prima;
    }

    @Override
    public double getVega() {
        // se podria pasar un vega por BS mucha mas rapido
        BlackScholes opt = new BlackScholes(underlying, option);
                opt.run();
                return opt.getVega();
/*
        //aca el modelo para pasar el vega correcto
        double vlt=option.getImpliedVlt();
       // System.out.println("---Vlt-JR-"+vlt);
        option.setImpliedVlt(vlt+0.01);
        JRudd op= getNewOption(underlying, option,steps);

        option.setImpliedVlt(vlt);
        return op.getPrima()-prima;
*/
    }
    @Override
    public double getRho() {

        double rate=option.getInterestRate();
        option.setInterestRate(rate+0.01);
        JRudd op= getNewOption(underlying, option,steps);

        option.setInterestRate(rate);
        return op.getPrima()-prima;

    }



    public static double payoff(double und, double strike, double callFlag) {
        return Math.max((und - strike) * callFlag, 0);
    }

    @Override
    public double getPrimaWithThisVlt(double vlt){

        Modelable jrOption = new JRudd(underlying,option.setImpliedVlt(vlt),steps).run();
        return jrOption.getPrima();
    }
    private static class Nodo {
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
