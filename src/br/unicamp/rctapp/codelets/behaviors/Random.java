package br.unicamp.rctapp.codelets.behaviors;

import br.unicamp.cst.behavior.subsumption.SubsumptionAction;
import br.unicamp.cst.behavior.subsumption.SubsumptionArchitecture;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.rctapp.memory.CreatureInnerSense;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.WorldPoint;

import java.util.Date;

/**
 * Created by du on 12/04/17.
 */
public class Random extends SubsumptionAction {

    private MemoryObject innerSenseMO;
    private MemoryObject legsMO;
    private CreatureInnerSense cis;
    private WorldPoint creaturePositionSaved;
    private Date timeCheckPoint;

    public Random(SubsumptionArchitecture subsumptionArchitecture) {
        super(subsumptionArchitecture);
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
        return false;
    }

    @Override
    public void act() {
        JSONObject message = new JSONObject();

        try {

            java.util.Random random = new java.util.Random();

            message.put("ACTION", "RANDOM");
            message.put("SPEED", 3);
            message.put("X", random.nextInt(800));
            message.put("Y", random.nextInt(600));
            legsMO.setEvaluation(getActivation());
            legsMO.setI(message.toString());

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void accessMemoryObjects() {
        if (innerSenseMO == null) {
            innerSenseMO = (MemoryObject) this.getInput("INNER");
        }

        if (legsMO == null) {
            legsMO = (MemoryObject) this.getOutput("LEGS");
        }
    }

    @Override
    public void calculateActivation() {
        double activation = 0;

        cis = (CreatureInnerSense) innerSenseMO.getI();

        if (creaturePositionSaved == null)
            creaturePositionSaved = cis.getPosition();
        else {
            WorldPoint actuallyPosition = cis.getPosition();

            if (creaturePositionSaved.getX() == actuallyPosition.getX() && creaturePositionSaved.getY() == actuallyPosition.getY()) {
                if (timeCheckPoint == null)
                    timeCheckPoint = new Date();
                else {
                    double diff = (new Date()).getTime() - timeCheckPoint.getTime();
                    activation = diff / 20000 > 1 ? 1 : diff / 20000;
                }
            } else {
                timeCheckPoint = new Date();
                creaturePositionSaved = cis.getPosition();
            }
        }

        try {
            setActivation(activation);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }
}
