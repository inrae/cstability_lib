package capsis.lib.cstability.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Context of the model C-STABILITY
 *
 * @author J. Sainte-Marie, F. de Coligny - February 2021
 */
@SuppressWarnings("serial")
public class Context implements Serializable {

	private String timeUnit;

	private int initialDate = -1;
	private int finalDate = -1;
	private double userTimeStep = -1;
	private Timeline timeline;

	private SubstrateInputManager substrateInputManager;
	private List<EnvironmentContext> environmentContextList;

	/**
	 * Constructor
	 */
	public Context() {
	}

	/**
	 * buildTimeline()
	 */
	public void buildTimeline() throws Exception {
		this.timeline = new Timeline(timeUnit, initialDate, finalDate, userTimeStep);
	}

	/**
	 * buildEnvironmentList():
	 * 
	 * TODO Réflexions sur la structuration des inputs de type environnement. Ces
	 * données doivent avoir un format standardisé.
	 * 
	 * créer un objet Environment dans lequel on gèrera ces variables
	 * 
	 * private soilPh, private soilTemperature, private soilHumidity
	 * 
	 * Il faut voir que toutes des données pourraient être lues à partir de fichiers
	 * - éventuellement à interpoler sur le maillage - ou alors être programmées de
	 * manière fonctionelle
	 */
	public void buildEnvironmentList(/* data */) {
		environmentContextList = new ArrayList<>();
		for (int i = 0; i < timeline.getDiscretization().length; i++)
			environmentContextList.add(new EnvironmentContext(timeline.getDiscretization()[i]));
	}

	/**
	 * setTimeUnit()
	 */
	public void setTimeUnit(String timeUnit) throws Exception {
		this.timeUnit = timeUnit;
	}

	/**
	 * setInitialDate()
	 */
	public void setInitialDate(int initialDate) throws Exception {
		if (initialDate < 0)
			throw new Exception("Context.setInitialDate(): initialDate " + initialDate + " must be non negative");
		this.initialDate = initialDate;
	}

	/**
	 * setFinalDate()
	 */
	public void setFinalDate(int finalDate) throws Exception {
		if (finalDate < initialDate)
			throw new Exception("Context.setFinalDate(): finalDate " + finalDate
					+ " must be strictly superior to initialDate" + initialDate);
		this.finalDate = finalDate;
	}

	/**
	 * setUserTimeStep()
	 */
	public void setUserTimeStep(double userTimeStep) throws Exception {
		if (userTimeStep <= 0)
			throw new Exception("Context.setUserTimeStep(): userTimeStep " + userTimeStep + " must be positve");
		this.userTimeStep = userTimeStep;
	}

	/**
	 * setSubstrateInputManager()
	 */
	public void setSubstrateInputManager(SubstrateInputManager substrateInputManager) {
		this.substrateInputManager = substrateInputManager;
	}

	/**
	 * getTimeUnit()
	 */
	public String getTimeUnit() {
		return timeUnit;
	}

	/**
	 * getInitialDate()
	 */
	public int getInitialDate() {
		return initialDate;
	}

	/**
	 * getFinalDate()
	 */
	public int getFinalDate() {
		return finalDate;
	}

	/**
	 * getUserTimeStepv
	 */
	public double getUserTimeStep() {
		return userTimeStep;
	}

	/**
	 * getTimeline()
	 */
	public Timeline getTimeline() {
		return timeline;
	}

	/**
	 * getSubstrateInputManager()
	 */
	public SubstrateInputManager getSubstrateInputManager() {
		return substrateInputManager;
	}

	@Override
	/**
	 * toString()
	 */
	public String toString() {
		String CR = "\n";
		StringBuffer b = new StringBuffer("--- Context");

		b.append(CR);
		b.append("timeUnit: " + timeUnit);

		b.append(CR);
		b.append("initialDate: " + initialDate);

		b.append(CR);
		b.append("finalDate: " + finalDate);

		b.append(CR);
		b.append("userTimeStep: " + userTimeStep);

		b.append(CR);
		b.append("timeline: " + timeline);

		b.append(CR);
		b.append("--- end-of-Context");

		return "" + b;
	}

}
