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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.unicamp.rctapp.memory.CreatureInnerSense;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;
import ws3dproxy.util.Constants;

/**
 *
 * @author Du
 */
public class GoToJewel extends SubsumptionAction {

    private MemoryObject knownJewels;
    private MemoryObject selfInfoMO;
    private MemoryObject legsMO;

    private int creatureBasicSpeed;
    private double reachDistance;
    private Creature creature;
    private double agentScore = 0;

    public GoToJewel(int creatureBasicSpeed, int reachDistance, Creature creature, SubsumptionArchitecture subsumptionArchitecture) {
        super(subsumptionArchitecture);
        this.creatureBasicSpeed = creatureBasicSpeed;
        this.reachDistance = 58;
        this.creature = creature;
    }

    @Override
    public void accessMemoryObjects() {
        if(knownJewels == null)
            knownJewels = (MemoryObject) this.getInput("KNOWN_JEWELS");

        if(selfInfoMO == null)
            selfInfoMO = (MemoryObject) this.getInput("INNER");

        if(legsMO == null)
            legsMO = (MemoryObject) this.getOutput("LEGS");

    }

    @Override
    public void calculateActivation() {
        try {

            CreatureInnerSense cis = (CreatureInnerSense) selfInfoMO.getI();
            if(cis.getLeafletList() != null)
                cis.setLeafletCompleteRate(getCollectedNumberLeaflet(cis.getLeafletList())/getFullNumberLeaflet(cis.getLeafletList()));

            if ((creature.getAttributes().getFuel() / 1000) >= 0.4) {
                List<Thing> jewels = (List<Thing>) knownJewels.getI();
                if (!jewels.isEmpty()) {
                    setActivation(1);

                } else {
                    setActivation(0);
                }
            } else {
                setActivation(0);
            }

        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(GoToApple.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public boolean suppressCondition() {
        return false;
    }

    @Override
    public boolean inhibitCondition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void act() {

        List<Thing> jewels = (List<Thing>) knownJewels.getI();
        CreatureInnerSense cis = (CreatureInnerSense) selfInfoMO.getI();
        synchronized (legsMO) {
            synchronized (jewels) {
                if (!jewels.isEmpty()) {
                    double jewelX = 0;
                    double jewelY = 0;

                    Thing jewel = jewels.get(0);

                    try {
                        jewelX = jewel.getX1();
                        jewelY = jewel.getY1();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONObject message = new JSONObject();
                    try {

                        message.put("ACTION", "GOTO");
                        message.put("X", (int) jewelX);
                        message.put("Y", (int) jewelY);
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


    public double getFullNumberLeaflet(List<Leaflet> leaflets) {

        ArrayList<String> colors = new ArrayList<String>();
        colors.add(Constants.colorRED);
        colors.add(Constants.colorYELLOW);
        colors.add(Constants.colorGREEN);
        colors.add(Constants.colorWHITE);
        colors.add(Constants.colorORANGE);
        colors.add(Constants.colorMAGENTA);
        colors.add(Constants.colorBLUE);

        double totalJewels = 0;

        double totalScore = 0;

        for (Leaflet leaflet : leaflets) {

            int countLeaflet = 0;

            for (String color : colors) {
                if (leaflet.getTotalNumberOfType(color) != -1) {
                    totalJewels += leaflet.getTotalNumberOfType(color);

                    if(leaflet.getMissingNumberOfType(color) > 0)
                        countLeaflet++;
                }
            }

            if(countLeaflet == 0)
                totalScore += leaflet.getSituation();

        }

        this.agentScore = totalScore;

        return totalJewels;
    }

    public double getCollectedNumberLeaflet(List<Leaflet> leaflets) {

        ArrayList<String> colors = new ArrayList<String>();
        colors.add(Constants.colorRED);
        colors.add(Constants.colorYELLOW);
        colors.add(Constants.colorGREEN);
        colors.add(Constants.colorWHITE);
        colors.add(Constants.colorORANGE);
        colors.add(Constants.colorMAGENTA);
        colors.add(Constants.colorBLUE);

        double totalCollectedJewels = 0;

        for (Leaflet leaflet : leaflets) {

            for (String color : colors) {
                if (leaflet.getTotalNumberOfType(color) != -1)
                    totalCollectedJewels += leaflet.getCollectedNumberOfType(color);
            }

        }

        return totalCollectedJewels;
    }
}
