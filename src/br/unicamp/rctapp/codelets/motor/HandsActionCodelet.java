package br.unicamp.rctapp.codelets.motor;

import br.unicamp.cst.core.entities.MemoryObject;
import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;

/**
 * @author Du
 */

public class HandsActionCodelet extends Codelet {

	private MemoryObject handsMO;
	private MemoryObject hiddenApplesMO;
	private MemoryObject visionMO;

	private String previousHandsAction = "";
	private Creature c;
	private Random r = new Random();
	private static Logger log = Logger.getLogger(HandsActionCodelet.class.getCanonicalName());

	public HandsActionCodelet(Creature nc) {
		setC(nc);
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		HandsActionCodelet.log = log;
	}

	@Override
	public void accessMemoryObjects() {
		if (getHandsMO() == null)
			setHandsMO((MemoryObject) this.getInput("HANDS"));

		if (getHiddenApplesMO() == null)
			setHiddenApplesMO((MemoryObject) this.getInput("HIDDEN_THINGS"));

		if(getVisionMO() == null)
			setVisionMO((MemoryObject) this.getInput("VISION"));


	}

	public void proc() {
		synchronized (getHandsMO()) {

			if (getHandsMO().getI() != null) {
				String command = (String) getHandsMO().getI();

				if (!command.equals("") && (!command.equals(getPreviousHandsAction()))) {
					JSONObject jsonAction;
					try {
						jsonAction = new JSONObject(command);
						if (jsonAction.has("ACTION") && jsonAction.has("OBJECT")) {
							String action = jsonAction.getString("ACTION");
							String objectName = jsonAction.getString("OBJECT");
							if (action.equals("PICKUP")) {
								if (!getPreviousHandsAction().contains(action)) {
									try {
										getC().putInSack(objectName);
										getC().rotate(3);
									} catch (CommandExecException e) {
										e.printStackTrace();
									}
									getLog().info("Sending PUT IN SACK command to agent:****** " + objectName + "**********");
								}
							}
							if (action.equals("EATIT")) {
								if (!getPreviousHandsAction().contains(action)) {
									try {
										getC().eatIt(objectName);
										getC().rotate(3);
									} catch (Exception e) {

									}
									getLog().info("Sending EAT command to agent:****** " + objectName + "**********");
								}
							}
							if (action.equals("BURY")) {
								if (!getPreviousHandsAction().contains(action)) {
									try {
										getC().hideIt(objectName);

										List<Thing> vision = Collections.synchronizedList((List<Thing>) getVisionMO().getI());

										List<Thing> thingsA = vision.stream().filter(v -> v.getName().equals(objectName)).collect(Collectors.toList());
										//Thing closestObstacle = vision.stream().filter(v -> v.getName().equals(objectName)).collect(Collectors.toList()).get(0);

										if(thingsA.size() > 0) {
											Thing closestObstacle = thingsA.get(0);
											if (closestObstacle.getName().contains("Food")) {
												closestObstacle.hidden = true;

												List<Thing> things = (List<Thing>) getHiddenApplesMO().getI();

												if (things.size() == 0) {
													things.add(closestObstacle);
												} else {
													for (int i = 0; i < things.size(); i++) {
														if (!things.get(i).getName().equals(closestObstacle.getName())) {
															things.add(closestObstacle);
														}
													}
												}
											}
										}

									} catch (Exception e) {
										e.printStackTrace();
									}

									getLog().info("Sending BURY command to agent:****** " + objectName + "**********");
								}
							}
							if (action.equals("UNEARTH")) {
								if (!getPreviousHandsAction().contains(action)) {
									try {

										getC().unhideIt(objectName);
										getC().eatIt(objectName);
										getC().rotate(3);

									} catch (CommandExecException e) {
										e.printStackTrace();
									}

									List<Thing> things = (List<Thing>) getHiddenApplesMO().getI();
									for (int i = 0; i < things.size(); i++) {
										if (things.get(i).getName().equals(objectName)) {
											things.remove(i);
											break;
										}
									}
									getLog().info("Sending UNEARTH command to agent:****** " + objectName + "**********");
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				setPreviousHandsAction(command);
			}
		}
	}//end proc

	@Override
	public void calculateActivation() {

	}


	public MemoryObject getHandsMO() {
		return handsMO;
	}

	public void setHandsMO(MemoryObject handsMO) {
		this.handsMO = handsMO;
	}

	public MemoryObject getHiddenApplesMO() {
		return hiddenApplesMO;
	}

	public void setHiddenApplesMO(MemoryObject hiddenApplesMO) {
		this.hiddenApplesMO = hiddenApplesMO;
	}

	public MemoryObject getVisionMO() {
		return visionMO;
	}

	public void setVisionMO(MemoryObject visionMO) {
		this.visionMO = visionMO;
	}

	public String getPreviousHandsAction() {
		return previousHandsAction;
	}

	public void setPreviousHandsAction(String previousHandsAction) {
		this.previousHandsAction = previousHandsAction;
	}

	public Creature getC() {
		return c;
	}

	public void setC(Creature c) {
		this.c = c;
	}

	public Random getR() {
		return r;
	}

	public void setR(Random r) {
		this.r = r;
	}
}
