package capsis.lib.cstability.function;

import java.util.StringTokenizer;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.function.util.BasicFunctions;
import capsis.lib.cstability.function.util.OneVariable;
import capsis.lib.cstability.function.util.Variables;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.state.State;

/**
 * A Gaussian
 * 
 * @author J. Sainte-Marie, F. de Coligny - March 2021
 */
@SuppressWarnings("serial")
public class Gaussian extends Function {

	// Note: Function implements Decodable

	private double mean;
	private double sd;

	/**
	 * Default constructor
	 */
	public Gaussian() {
	}

	/**
	 * Constructor
	 */
	public Gaussian(double mean, double sd) {
		this.mean = mean;
		this.sd = sd;
	}

	/**
	 * decode(): decoding method from an encoded string
	 */
	@Override
	public Gaussian decode(String encodedString, Parameters p, Context c) throws Exception {

		// e.g. gaussian(1;0.4)

		try {
			String s = encodedString.trim();

			if (!s.startsWith("gaussian("))
				throw new Exception("Not a Gaussian");

			s = encodedString.replace("gaussian(", "");
			s = s.replace(")", "");
			StringTokenizer st = new StringTokenizer(s, ";");

			mean = Double.parseDouble(st.nextToken().trim());

			sd = Double.parseDouble(st.nextToken().trim());

			return new Gaussian(mean, sd);

		} catch (Exception e) {
			throw new Exception("Gaussian.decode (), could not parse this encodedString: " + encodedString, e);
		}

	}

	/**
	 * execute()
	 */
	@Override
	public double execute(Parameters p, Context c, State s, Variables v) throws Exception {
		v.checkIf(OneVariable.class);
		double x = ((OneVariable) v).x1;

		return BasicFunctions.gaussian(mean, sd, x);
	}

	/**
	 * expectedVariables()
	 */
	@Override
	public Class expectedVariables() {
		return OneVariable.class;
	}

}
