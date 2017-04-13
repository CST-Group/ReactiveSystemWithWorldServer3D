package br.unicamp.rctapp.application;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Du
 */
public class ExperimentMain {


    public Logger logger = Logger.getLogger(ExperimentMain.class.getName());


    public ExperimentMain() {
        //WS3DProxy.logger.setLevel(Level.SEVERE);
        Logger.getLogger("br/unicamp/rctapp/codelets").setLevel(Level.SEVERE);
        // Create Environment
        Environment env = new Environment(); //Creates only a creature and some apples
        AgentMind a = new AgentMind(env);  // Creates the Agent Mind and start it

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        ExperimentMain em = new ExperimentMain();
    }

}
