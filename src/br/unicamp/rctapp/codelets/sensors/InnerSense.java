package br.unicamp.rctapp.codelets.sensors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.rctapp.memory.CreatureInnerSense;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;

/**
 * @author Du
 */

public class InnerSense extends Codelet {

    private MemoryObject innerSenseMO;
    private Creature c;
    private CreatureInnerSense cis;

    public InnerSense(Creature nc) {
        setC(nc);
        setCis(new CreatureInnerSense());
    }

    @Override
    public void accessMemoryObjects() {
        if (getInnerSenseMO() == null) {
            setInnerSenseMO((MemoryObject) this.getOutput("INNER"));
        }
    }

    public void proc() {
        getCis().setPosition(getC().getPosition());
        getCis().setPitch(getC().getPitch());
        getCis().setFov(getC().getFOV());
        getCis().setFuel(getC().getFuel());
        getCis().setLeafletList(getC().getLeaflets());
        getCis().setScore(getC().s.score);
        getCis().setDeliverySpotPosition(World.getDeliverySpot());

        try {
            getCis().setThingsInWorld(World.getWorldEntities());
        } catch (CommandExecException e) {
            e.printStackTrace();
        }

        getInnerSenseMO().setI(getCis());

    }

    @Override
    public void calculateActivation() {
        try {
            setActivation(0);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }


    public MemoryObject getInnerSenseMO() {
        return innerSenseMO;
    }

    public void setInnerSenseMO(MemoryObject innerSenseMO) {
        this.innerSenseMO = innerSenseMO;
    }

    public Creature getC() {
        return c;
    }

    public void setC(Creature c) {
        this.c = c;
    }

    public CreatureInnerSense getCis() {
        return cis;
    }

    public void setCis(CreatureInnerSense cis) {
        this.cis = cis;
    }
}

