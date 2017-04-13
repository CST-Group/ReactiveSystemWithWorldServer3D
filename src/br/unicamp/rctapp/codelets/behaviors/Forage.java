package br.unicamp.rctapp.codelets.behaviors;

import br.unicamp.cst.behavior.subsumption.SubsumptionAction;
import br.unicamp.cst.behavior.subsumption.SubsumptionArchitecture;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Thing;

/**
 * @author Du
 */
public class Forage extends SubsumptionAction {

    private MemoryObject knownMO;
    private MemoryObject knownJewelMO;
    private List<Thing> known;
    private MemoryObject legsMO;

    /**
     * Default constructor
     */
    public Forage(SubsumptionArchitecture subsumptionBehaviourLayer) {
        super(subsumptionBehaviourLayer);

    }

    @Override
    public void accessMemoryObjects() {
        if (knownMO == null) {
            knownMO = (MemoryObject) this.getInput("KNOWN_APPLES");
        }

        if (legsMO == null) {
            legsMO = (MemoryObject) this.getOutput("LEGS");
        }

        if (knownJewelMO == null) {
            knownJewelMO = (MemoryObject) this.getInput("KNOWN_JEWELS");
        }
    }

    @Override
    public void calculateActivation() {
        try {
            if (((List<Thing>) knownJewelMO.getI()).size() == 0) {
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
        known = (List<Thing>) knownMO.getI();
        if (known.size() == 0) {
            JSONObject message = new JSONObject();
            try {
                message.put("ACTION", "FORAGE");
                legsMO.updateI(message.toString());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
