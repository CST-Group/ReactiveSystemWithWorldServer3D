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

import java.awt.Point;
import java.awt.geom.Point2D;
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
public class GetClosestJewel extends SubsumptionAction {

    private MemoryObject closestJewelMO;
    private MemoryObject innerSenseMO;
    private int reachDistance;
    private MemoryObject handsMO;
    private Creature creature;
    Thing closestJewel;
    CreatureInnerSense cis;
    List<Thing> known;

    public GetClosestJewel(int reachDistance, Creature creature, SubsumptionArchitecture subsumptionBehaviourLayer) {
        super(subsumptionBehaviourLayer);
        this.reachDistance = reachDistance;
        this.creature = creature;
    }

    @Override
    public void accessMemoryObjects() {
        if (closestJewelMO == null) {
            closestJewelMO = (MemoryObject) this.getInput("CLOSEST_JEWEL");
        }

        if (innerSenseMO == null) {
            innerSenseMO = (MemoryObject) this.getInput("INNER");
        }

        if (handsMO == null) {
            handsMO = (MemoryObject) this.getOutput("HANDS");
        }


    }


    @Override
    public void calculateActivation() {
        try {
            if (closestJewelMO.getI() != null && (creature.getAttributes().getFuel() / 1000) >= 0.4) {

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
        String jewelName = "";
        closestJewel = (Thing) closestJewelMO.getI();
        cis = (CreatureInnerSense) innerSenseMO.getI();
        //Find distance between closest apple and self
        //If closer than reachDistance, eat the apple

        if (closestJewel != null) {
            double jewelX = 0;
            double jewelY = 0;
            try {
                jewelX = closestJewel.getX1();
                jewelY = closestJewel.getY1();
                jewelName = closestJewel.getName();

            } catch (Exception e) {
                e.printStackTrace();
            }

            double selfX = cis.getPosition().getX();
            double selfY = cis.getPosition().getY();

            Point2D pApple = new Point();
            pApple.setLocation(jewelX, jewelY);

            Point2D pSelf = new Point();
            pSelf.setLocation(selfX, selfY);

            double distance = pSelf.distance(pApple);
            JSONObject message = new JSONObject();
            try {
                if (distance < reachDistance) { //eat it	
                    message.put("OBJECT", jewelName);
                    message.put("ACTION", "PICKUP");
                    handsMO.updateI(message.toString());

                } else {
                    handsMO.updateI("");    //nothing
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            handsMO.updateI("");    //nothing
        }
    }
}
