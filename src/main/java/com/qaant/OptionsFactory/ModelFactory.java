package com.qaant.OptionsFactory;



public class ModelFactory {
    public Modelable getModel(String modelName, UnderlyingAsset und, OptionElements opt){
        if(modelName==null){
            return null;
        }
        if (modelName.equalsIgnoreCase("BlackScholes")){

            return new BlackScholes (und,opt).run();

        }else if(modelName.equalsIgnoreCase("Whaley")){

            return new Whaley (und,opt).run();

        }else if(modelName.equalsIgnoreCase("JRudd")){

            return new JRudd  (und,opt).run();
        }
        return null;
    }

    public Modelable getModel(String modelName, UnderlyingAsset und, OptionElements opt, int steps){
        if(modelName==null){
            return null;
        }
        if(modelName.equalsIgnoreCase("JRudd")){

            return new JRudd  (und,opt,steps).run();
        }
        return null;
    }

}
