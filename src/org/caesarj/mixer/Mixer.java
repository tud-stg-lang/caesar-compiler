package org.caesarj.mixer;

public class Mixer {
    
    private static Mixer singleton = new Mixer();
   
    public static Mixer instance() {
        return singleton;
    }

    private Mixer() {
    }
    
    String generateClass(MixinList mixinList) throws MixerException {
        
        /*
         * 
         * TODO KARL        
         * generate .class file here
         * 
         */
        
        return mixinList.generateClassName();
    }
}
