package br.unicamp.rctapp.codelets.behaviors;

import br.unicamp.cst.behavior.subsumption.SubsumptionAction;
import br.unicamp.cst.behavior.subsumption.SubsumptionArchitecture;

import org.json.JSONException;
import org.json.JSONObject;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.unicamp.rctapp.memory.CreatureInnerSense;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;

/**
 * @author Du
 */
public class GoToApple extends SubsumptionAction {

    private MemoryObject knownApplesMO;

    private MemoryObject selfInfoMO;
    private MemoryObject legsMO;
    private int creatureBasicSpeed;
    private double reachDistance;
    private Creature creature;

    public GoToApple(int creatureBasicSpeed, int reachDistance, Creature creature, SubsumptionArchitecture subsumptionBehaviourLayer) {
        super(subsumptionBehaviourLayer);
        this.creatureBasicSpeed = creatureBasicSpeed;
        this.reachDistance = 58;
        this.creature = creature;
    }

    @Override
    public void accessMemoryObjects() {

        if (knownApplesMO == null)
            knownApplesMO = (MemoryObject) this.getInput("KNOWN_APPLES");


        if (selfInfoMO == null) {
            selfInfoMO = (MemoryObject) this.getInput("INNER");
        }

        if (legsMO == null) {
            legsMO = (MemoryObject) this.getOutput("LEGS");
        }

    }

    @Override
    public void calculateActivation() {
        List<Thing> apples = (List<Thing>) knownApplesMO.getI();
        try {

            if ((creature.getAttributes().getFuel() / 1000) >= 0.4) {
                setActivation(0);
            } else {
                if (!apples.isEmpty()) {
                    setActivation(1);
                } else {
                    setActivation(0);
                }
            }

        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(GoToApple.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public boolean suppressCondition() {
        if (getActivation() == 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean inhibitCondition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void act() {
        // Find distance between creature and closest apple
        //If far, go towards it
        //If close, stops

        List<Thing> apples = (List<Thing>) knownApplesMO.getI();

        CreatureInnerSense cis = (CreatureInnerSense) selfInfoMO.getI();

        synchronized (legsMO) {
            synchronized (apples) {
                if (!apples.isEmpty()) {
                    double appleX = 0;
                    double appleY = 0;
                    try {
                        appleX = apples.get(0).getX1();
                        appleY = apples.get(0).getY1();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    JSONObject message = new JSONObject();
                    try {

                        message.put("ACTION", "GOTO");
                        message.put("X", (int) appleX);
                        message.put("Y", (int) appleY);
                        message.put("SPEED", creatureBasicSpeed);

                        legsMO.updateI(message.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    JSONObject message = new JSONObject();
                    try {
                        message.put("ACTION", "FORAGE");
                        legsMO.setI(message.toString());
                        legsMO.setEvaluation(getActivation());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}
