package com.qaantOptions;

import com.qaant.OptionsFactory.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello Options Factory!" );

        UnderlyingAsset underlying= new UnderlyingAsset("name","ticker","ISIN", 66350.0, 0.40, 0.00, 'F'); //Stock or Future
        OptionElements option = new OptionElements('A', 'P', 64000.0,100.0, 0, .40,.35);

        ModelFactory model= new ModelFactory();

        Modelable bsOpt=model.getModel("BlackScholes",underlying,option);
        Modelable wOpt=model.getModel("Whaley",underlying,option);
        Modelable jrOpt=model.getModel("JRudd",underlying,option,500);//por default hace 500 steps
        System.out.println(bsOpt.toString());
        System.out.println(wOpt.toString());
        System.out.println(jrOpt.toString());
        System.out.println("Theta :"+jrOpt.getTheta());
        System.out.println("Vega :"+jrOpt.getVega());
        System.out.println("Rho :"+jrOpt.getRho());

        double ivBS=bsOpt.getImpliedVlt(4500);
        double ivW=wOpt.getImpliedVlt(4200);
        double ivJR=jrOpt.getImpliedVlt(4700);
        //option.setOptionMktPrice(4500);
     //   double ivJR = model.getModel("JRudd",underlying,option,600).getImpliedVlt();

        System.out.println("BS Implied Vlt :"+ivBS);
        System.out.println("W  Implied Vlt :"+ivW);
        System.out.println("JR Implied Vlt :"+ivJR);

        System.out.println(("-------------------Calculo con nuevas IVs--------------------:"));
        bsOpt=model.getModel("BlackScholes",underlying,option.setImpliedVlt(ivBS));
        wOpt=model.getModel("Whaley",underlying,option.setImpliedVlt(ivW));
        jrOpt=model.getModel("JRudd",underlying,option.setImpliedVlt(ivJR), jrOpt.getSteps());
        System.out.println(bsOpt.toString());
        System.out.println(wOpt.toString());
        System.out.println(jrOpt.toString());



        System.out.println("Option Ex Type -------------->:"+option.getExerciseType());


       // System.out.println("Theta :"+((JRudd)opt3).getTheta2());

        /*
        double ivTurbo=opt1.getImpliedVlt();
        System.out.println("Implied Vlt "+ivTurbo);
        System.out.println("Option with turbo vlt:"+model.getModel("BlackScholes",underlying,option.setImpliedVlt(ivTurbo)).toString());

        option.setDaysToExpiration(100);
        option.setOptionType('P');
        System.out.println("Option with daysToExpiration :"+model.getModel("BlackScholes",underlying,option).toString());

        Imodelable opt2=model.getModel("Whaley",underlying,option);
        System.out.println(opt2.toString());

        ivTurbo=opt2.getImpliedVlt();
        System.out.println("Implied Vlt "+ivTurbo);
        System.out.println("Option with turbo vlt:"+model.getModel("Whaley",underlying,option.setImpliedVlt(ivTurbo)).toString());

        Imodelable opt3=model.getModel("JRudd",underlying,option);//por default hace 500 steps
        System.out.println(opt3.toString());
        /*


        Imodelable opt3=model.getModel("JRudd",underlying,option);
       //System.out.println(opt3.getModelName());
        System.out.println(opt3.toString());

        System.out.println("####################");
        System.out.println("Another way");

        Imodelable BSopt=new BlackScholes(underlying,option).run();
        System.out.println(BSopt.toString());

        option.setOptionType('C');
        BSopt=new BlackScholes(underlying,option).run();
        // System.out.println(BSopt.getModelName());
        // System.out.println("Prima :"+BSopt.getDerivatives()[0]);
        System.out.println(BSopt.toString());
        double iv=BSopt.getImpliedVlt();
        System.out.println("Implied Vol:"+iv);
        option.setImpliedVlt(iv);
        BSopt = new BlackScholes(underlying,option).run();
        System.out.println("Nueva prima con Ivlt:"+BSopt.getPrima());

        //option.setOptionType('C');
        Imodelable wOption =new Whaley(underlying,option).run();
        System.out.println(wOption.toString());

        option.setOptionMktPrice(5100.0);
        iv=wOption.getImpliedVlt();
        System.out.println("Implied Vol:"+iv);
        option.setImpliedVlt(iv);
        wOption = new Whaley(underlying,option).run();
        System.out.println("Nueva prima con Ivlt:"+wOption.getPrima());



        Imodelable JRopt=new JRudd(underlying,option).run();
      //  System.out.println(JRopt.getModelName());
       // System.out.println("Prima :"+JRopt.getDerivatives()[0]);
        System.out.println(JRopt.toString());
*/
    }
}
