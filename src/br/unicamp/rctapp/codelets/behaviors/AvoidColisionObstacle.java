/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.rctapp.codelets.behaviors;

import br.unicamp.cst.behavior.subsumption.SubsumptionAction;
import br.unicamp.cst.behavior.subsumption.SubsumptionArchitecture;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.unicamp.rctapp.memory.CreatureInnerSense;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;

/**
 * @author Du
 */
public class AvoidColisionObstacle extends SubsumptionAction {

    private MemoryObject closestObstacleMO;
    private MemoryObject innerSenseMO;
    private MemoryObject handsMO;
    private MemoryObject knownJewelsMO;
    private int reachDistance;
    private MemoryObject legsMO;
    private Creature creature;
    Thing closestObstacle;
    CreatureInnerSense cis;

    public AvoidColisionObstacle(SubsumptionArchitecture subsumptionArchitecture, int reachDistance) {
        super(subsumptionArchitecture);
        this.reachDistance = reachDistance;
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
    public void accessMemoryObjects() {
        if (closestObstacleMO == null) {
            closestObstacleMO = (MemoryObject) this.getInput("CLOSEST_OBSTACLE");
        }

        if (innerSenseMO == null) {
            innerSenseMO = (MemoryObject) this.getInput("INNER");
        }

        if (legsMO == null) {
            legsMO = (MemoryObject) this.getOutput("LEGS");
        }

        if (handsMO == null) {
            handsMO = (MemoryObject) this.getOutput("HANDS");
        }

        if (knownJewelsMO == null)
            knownJewelsMO = (MemoryObject) this.getInput("KNOWN_JEWELS");

    }

    @Override
    public void calculateActivation() {
        try {
            Thing brick = (Thing) closestObstacleMO.getI();

            if (brick != null) {
                setActivation(1);
            } else {

                setActivation(0);

            }

        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(AvoidColisionObstacle.class.getName()).log(Level.SEVERE, null, ex);
        }

    }




    @Override
    public void act() {
        String obstacleName = "";
        closestObstacle = (Thing) closestObstacleMO.getI();
        cis = (CreatureInnerSense) innerSenseMO.getI();

        //Find distance between closest apple and self
        //If closer than reachDistance, eat the apple
        if (closestObstacle != null) {
            JSONObject message = new JSONObject();
            if (closestObstacle.getName().contains("Brick")) {
                try {

                    message.put("OBJECT", obstacleName);
                    message.put("ACTION", "AVOID");
                    legsMO.setEvaluation(getActivation());
                    legsMO.setI(message.toString());
                    handsMO.setEvaluation(getActivation());
                    handsMO.setI("");

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            } else {

                if (closestObstacle.getName().contains("Jewel")) {
                    List<Thing> jewels = (List<Thing>) knownJewelsMO.getI();
                    if (!jewels.contains(closestObstacle)) {
                        try {
                            message.put("OBJECT", closestObstacle.getName());
                            message.put("ACTION", "BURY");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handsMO.setEvaluation(getActivation());
                        handsMO.setI(message.toString());
                        legsMO.setEvaluation(getActivation());
                        legsMO.setI("");
                    } else {
                        try {
                            message.put("OBJECT", closestObstacle.getName());
                            message.put("ACTION", "PICKUP");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handsMO.setEvaluation(getActivation());
                        handsMO.setI(message.toString());
                        legsMO.setEvaluation(getActivation());
                        legsMO.setI("");
                    }
                } else {


                    try {
                        message.put("OBJECT", closestObstacle.getName());
                        message.put("ACTION", "BURY");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    handsMO.setEvaluation(getActivation());
                    handsMO.setI(message.toString());

                    legsMO.setEvaluation(getActivation());
                    legsMO.setI("");

                }
            }
        } else {
            legsMO.setEvaluation(getActivation());
            legsMO.setI("");
            handsMO.setEvaluation(getActivation());
            handsMO.setI("");
        }
    }
}
