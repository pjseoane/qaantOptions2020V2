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
        OptionElements option = new OptionElements('A', 'P', 64000.0,76.0, 0, .40,.35);

        ModelFactory model= new ModelFactory();

        Imodelable opt1=model.getModel("BlackScholes",underlying,option);
        Imodelable opt2=model.getModel("Whaley",underlying,option);
        Imodelable opt3=model.getModel("JRudd",underlying,option,500);//por default hace 500 steps
        System.out.println(opt1.toString());
        System.out.println(opt2.toString());
        System.out.println(opt3.toString());

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
