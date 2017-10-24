package br.unicamp.rctapp.codelets.behaviors;

import br.unicamp.cst.behavior.subsumption.SubsumptionAction;
import br.unicamp.cst.behavior.subsumption.SubsumptionArchitecture;
import java.awt.Point;
import java.awt.geom.Point2D;

import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.rctapp.memory.CreatureInnerSense;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;

/**
 *
 * @author Du
 */
public class EatClosestApple extends SubsumptionAction {

    private MemoryObject closestAppleMO;
    private MemoryObject innerSenseMO;
    private MemoryObject hiddenApplesMO;
    private int reachDistance;
    private MemoryObject handsMO;
    Thing closestApple;
    CreatureInnerSense cis;
    private Creature creature;

    public EatClosestApple(int reachDistance, Creature creature, SubsumptionArchitecture subsumptionBehaviourLayer) {
        super(subsumptionBehaviourLayer);
        this.reachDistance = reachDistance;
        this.creature = creature;
    }

    @Override
    public void accessMemoryObjects() {
        if (closestAppleMO == null) {
            closestAppleMO = (MemoryObject) this.getInput("CLOSEST_APPLE");
        }

        if (innerSenseMO == null) {
            innerSenseMO = (MemoryObject) this.getInput("INNER");
        }

        if (handsMO == null) {
            handsMO = (MemoryObject) this.getOutput("HANDS");
        }

        if (hiddenApplesMO == null)
            hiddenApplesMO = (MemoryObject) this.getInput("HIDDEN_THINGS");
    }

    @Override
    public void calculateActivation() {

        try {
            if (closestAppleMO.getI() != null) {
                setActivation(1);
            } else {

                setActivation(0);

            }

        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(GoToApple.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
    @Override
    public boolean suppressCondition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean inhibitCondition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void act() {
        String appleName = "";
        closestApple = (Thing) closestAppleMO.getI();
        cis = (CreatureInnerSense) innerSenseMO.getI();

        //Find distance between closest apple and self
        //If closer than reachDistance, eat the apple
        if (closestApple != null) {
            double appleX = 0;
            double appleY = 0;
            try {
                appleX = closestApple.getX1();
                appleY = closestApple.getY1();
                appleName = closestApple.getName();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            double selfX = cis.getPosition().getX();
            double selfY = cis.getPosition().getY();

            Point2D pApple = new Point();
            pApple.setLocation(appleX, appleY);

            Point2D pSelf = new Point();
            pSelf.setLocation(selfX, selfY);

            double distance = pSelf.distance(pApple);
            JSONObject message = new JSONObject();
            try {
                if (distance < reachDistance) { //eat it
                    if(closestApple.hidden){
                        message.put("OBJECT", appleName);
                        message.put("ACTION", "UNEARTH");
                        handsMO.setEvaluation(getActivation());
                        handsMO.setI(message.toString());
                    }
                    else {
                        message.put("OBJECT", appleName);
                        message.put("ACTION", "EATIT");
                        handsMO.setEvaluation(getActivation());
                        handsMO.setI(message.toString());
                    }

                } else {
                    handsMO.setEvaluation(getActivation());
                    handsMO.setI("");
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }
        } else {
            handsMO.setEvaluation(getActivation());
            handsMO.setI("");
        }

    }


}
