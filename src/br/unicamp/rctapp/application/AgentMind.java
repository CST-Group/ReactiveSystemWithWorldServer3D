package br.unicamp.rctapp.application;

import br.unicamp.cst.behavior.subsumption.SubsumptionAction;
import br.unicamp.cst.behavior.subsumption.SubsumptionArchitecture;
import br.unicamp.cst.behavior.subsumption.SubsumptionBehaviourLayer;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.rctapp.codelets.behaviors.*;
import br.unicamp.rctapp.codelets.motor.HandsActionCodelet;
import br.unicamp.rctapp.codelets.motor.LegsActionCodelet;
import br.unicamp.rctapp.codelets.perception.AppleDetector;
import br.unicamp.rctapp.codelets.perception.ClosestAppleDetector;
import br.unicamp.rctapp.codelets.perception.ClosestJewelDetector;
import br.unicamp.rctapp.codelets.perception.ClosestObstacleDetector;
import br.unicamp.rctapp.codelets.perception.JewelDetector;
import br.unicamp.rctapp.codelets.sensors.InnerSense;
import br.unicamp.rctapp.codelets.sensors.Vision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.unicamp.rctapp.memory.CreatureInnerSense;
import ws3dproxy.model.Thing;

/**
 * @author Du
 */
public class AgentMind extends Mind {

    private static int creatureBasicSpeed = 3;


    public AgentMind(Environment env) {
        super();
        // Create RawMemory and Coderack
        //Mind m = new Mind();
        //RawMemory rawMemory=RawMemory.getInstance();
        //CodeRack codeRack=CodeRack.getInstance();

        // Declare Memory Objects
        MemoryObject legsMO;
        MemoryObject handsMO;
        MemoryObject visionMO;
        MemoryObject innerSenseMO;
        MemoryObject closestAppleMO;
        MemoryObject knownApplesMO;
        MemoryObject closestJewelMO;
        MemoryObject knownJewelsMO;
        MemoryObject closestObstacleMO;
        MemoryObject hiddenObjetecsMO;

        int reachDistance = 60;
        int brickDistance = 48;

        SubsumptionArchitecture subsumptionArchitecture = new SubsumptionArchitecture(this);

        //Initialize Memory Objects
        legsMO = createMemoryObject("LEGS", "");
        handsMO = createMemoryObject("HANDS", "");
        List<Thing> vision_list = Collections.synchronizedList(new ArrayList<Thing>());
        visionMO = createMemoryObject("VISION", vision_list);
        CreatureInnerSense cis = new CreatureInnerSense();
        innerSenseMO = createMemoryObject("INNER", cis);

        Thing closestApple = null;
        closestAppleMO = createMemoryObject("CLOSEST_APPLE", closestApple);
        List<Thing> knownApples = Collections.synchronizedList(new ArrayList<Thing>());
        knownApplesMO = createMemoryObject("KNOWN_APPLES", knownApples);

        Thing closestJewel = null;
        closestJewelMO = createMemoryObject("CLOSEST_JEWEL", closestJewel);

        Thing closestObstacle = null;
        closestObstacleMO = createMemoryObject("CLOSEST_OBSTACLE", closestObstacle);

        List<Thing> knownJewels = Collections.synchronizedList(new ArrayList<Thing>());
        knownJewelsMO = createMemoryObject("KNOWN_JEWELS", knownJewels);

        hiddenObjetecsMO = createMemoryObject("HIDDEN_THINGS");
        hiddenObjetecsMO.setI(Collections.synchronizedList(new ArrayList<Thing>()));


        // Create Sensor Codelets
        Codelet vision = new Vision("VisionCodelet", env.c);
        vision.addOutput(visionMO);
        insertCodelet(vision); //Creates a vision sensor

        Codelet innerSense = new InnerSense(env.c);
        innerSense.addOutput(innerSenseMO);
        insertCodelet(innerSense); //A sensor for the inner state of the creature

        // Create Actuator Codelets
        Codelet legs = new LegsActionCodelet(env.c);
        legs.addInput(legsMO);
        insertCodelet(legs);

        Codelet hands = new HandsActionCodelet(env.c);
        hands.addInput(handsMO);
        hands.addInput(hiddenObjetecsMO);
        hands.addInput(visionMO);
        insertCodelet(hands);

        // Create Perception Codelets
        Codelet ad = new AppleDetector("AppleDetectorCodelet", env.c);
        ad.addInput(visionMO);
        ad.addInput(hiddenObjetecsMO);
        ad.addOutput(knownApplesMO);
        insertCodelet(ad);

        Codelet closestAppleDetector = new ClosestAppleDetector("ClosestAppleDetectorCodelet", env.c, reachDistance);
        closestAppleDetector.addInput(knownApplesMO);
        closestAppleDetector.addInput(innerSenseMO);
        closestAppleDetector.addOutput(closestAppleMO);
        insertCodelet(closestAppleDetector);

        Codelet aj = new JewelDetector("JewelDetectorCodelet", env.c);
        aj.addInput(visionMO);
        aj.addOutput(knownJewelsMO);
        insertCodelet(aj);

        Codelet closestJewelDetector = new ClosestJewelDetector("ClosestJewelDetectorCodelet", env.c, reachDistance);
        closestJewelDetector.addInput(knownJewelsMO);
        closestJewelDetector.addInput(innerSenseMO);
        closestJewelDetector.addOutput(closestJewelMO);
        insertCodelet(closestJewelDetector);


        Codelet closestObstacleDetector = new ClosestObstacleDetector("ClosestObstacleDetectorCodelet", env.c, brickDistance);
        closestObstacleDetector.addInput(visionMO);
        closestObstacleDetector.addInput(innerSenseMO);
        closestObstacleDetector.addOutput(closestObstacleMO);
        insertCodelet(closestObstacleDetector);


        SubsumptionBehaviourLayer layer = new SubsumptionBehaviourLayer();


        // Create Behavior Codelets
        SubsumptionAction goToClosestApple = new GoToApple(creatureBasicSpeed, reachDistance, env.c, subsumptionArchitecture);
        goToClosestApple.addInput(knownApplesMO);
        goToClosestApple.addInput(innerSenseMO);
        goToClosestApple.addOutput(legsMO);


        SubsumptionAction eatApple = new EatClosestApple(reachDistance, env.c, subsumptionArchitecture);
        eatApple.addInput(closestAppleMO);
        eatApple.addInput(innerSenseMO);
        eatApple.addInput(hiddenObjetecsMO);
        eatApple.addOutput(handsMO);

        SubsumptionAction goToClosestJewel = new GoToJewel(creatureBasicSpeed, reachDistance, env.c, subsumptionArchitecture);
        goToClosestJewel.addInput(knownJewelsMO);
        goToClosestJewel.addInput(innerSenseMO);

        goToClosestJewel.addOutput(legsMO);

        SubsumptionAction getJewel = new GetClosestJewel(reachDistance, env.c, subsumptionArchitecture);
        getJewel.addInput(closestJewelMO);
        getJewel.addInput(innerSenseMO);
        getJewel.addOutput(handsMO);

        SubsumptionAction random = new Random(subsumptionArchitecture);
        random.addInput(innerSenseMO);
        random.addOutput(handsMO);

        SubsumptionAction avoidColisionObstacle = new AvoidColisionObstacle(env.c, subsumptionArchitecture, brickDistance);
        avoidColisionObstacle.addInput(closestObstacleMO);
        avoidColisionObstacle.addInput(knownJewelsMO);
        avoidColisionObstacle.addInput(innerSenseMO);
        avoidColisionObstacle.addOutput(legsMO);
        avoidColisionObstacle.addOutput(handsMO);


        SubsumptionAction goToDeliverySpot = new GoToDeliverySpot(creatureBasicSpeed, env.c, subsumptionArchitecture);
        goToDeliverySpot.addInput(innerSenseMO);
        goToDeliverySpot.addOutput(legsMO);


        /*SubsumptionAction forage = new Forage(subsumptionArchitecture);
        forage.addInput(knownApplesMO);
        forage.addInput(knownJewelsMO);
        forage.addOutput(legsMO);*/

        //layer.addAction(forage);
        layer.addAction(random);
        layer.addAction(goToClosestJewel);
        layer.addAction(getJewel);
        layer.addAction(eatApple);
        layer.addAction(avoidColisionObstacle);
        layer.addAction(goToDeliverySpot);

        subsumptionArchitecture.addSuppressedAction(random, goToClosestJewel);
        subsumptionArchitecture.addSuppressedAction(random, goToClosestApple);

        subsumptionArchitecture.addSuppressedAction(avoidColisionObstacle, goToClosestJewel);
        subsumptionArchitecture.addSuppressedAction(avoidColisionObstacle, goToClosestApple);
        subsumptionArchitecture.addSuppressedAction(avoidColisionObstacle, goToDeliverySpot);
        subsumptionArchitecture.addInhibitedAction(avoidColisionObstacle, random);

        subsumptionArchitecture.addSuppressedAction(goToClosestApple, goToClosestJewel);
        subsumptionArchitecture.addSuppressedAction(goToClosestApple, goToDeliverySpot);

        subsumptionArchitecture.addSuppressedAction(goToDeliverySpot, goToClosestJewel);
        subsumptionArchitecture.addSuppressedAction(goToDeliverySpot, goToClosestApple);

        subsumptionArchitecture.addLayer(layer);


        // Create and Populate MindViewer
        /*MindView mv = new MindView("MindView");

        mv.setCreatureInnerSense(cis);
        mv.setCreature(env.c);
        mv.setMind(this);

        mv.addMO(knownApplesMO);
        mv.addMO(visionMO);
        mv.addMO(closestAppleMO);
        mv.addMO(innerSenseMO);
        mv.addMO(handsMO);
        mv.addMO(legsMO);
        mv.addMO(closestJewelMO);
        mv.addMO(knownJewelsMO);
        mv.addMO(closestObstacleMO);

        mv.StartTimer();
        mv.setVisible(true);*/


        // Start Cognitive Cycle
        start();
    }

}
