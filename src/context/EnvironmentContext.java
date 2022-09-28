package capsis.lib.cstability.context;

import java.io.Serializable;

/**
 * An environment of the model C-STABILITY
 *
 * @author J. Sainte-Marie, F. de Coligny - May 2021
 */
@SuppressWarnings("serial")
public class EnvironmentContext implements Serializable {

	private double date;

	/**
	 * Constructor
	 */
	public EnvironmentContext(double date) {
		this.date = date;
	}

	/**
	 * getDate()
	 */
	public double getDate() {
		return date;
	}
}
