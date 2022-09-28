package capsis.lib.cstability.function;

import java.util.StringTokenizer;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.function.util.OneVariable;
import capsis.lib.cstability.function.util.Variables;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.state.State;

/**
 * A uniform linear function.
 * 
 * @author J. Sainte-Marie, F. de Coligny - March 2021
 */
@SuppressWarnings("serial")
public class Linear extends Function {

	// Note: Function implements Decodable

	private double slope;

	/**
	 * Default constructor
	 */
	public Linear() {
	}

	/**
	 * Constructor
	 */
	public Linear(double slope) {
		this.slope = slope;
	}

	/**
	 * decode(): decoding method from an encoded string
	 */
	@Override
	public Linear decode(String encodedString, Parameters p, Context c) throws Exception {

		// e.g. Linear(0.4)

		try {
			String s = encodedString.trim();

			if (!s.startsWith("linear("))
				throw new Exception("Not a linear");

			s = encodedString.replace("linear(", "");
			s = s.replace(")", "");
			StringTokenizer st = new StringTokenizer(s, ";");

			slope = Double.parseDouble(st.nextToken().trim());

			return new Linear(slope);

		} catch (Exception e) {
			throw new Exception("Linear.decode (), could not parse this encodedString: " + encodedString, e);
		}

	}

	/**
	 * execute(): the function is uniform on the domain (x1) and then linear (x2)
	 */
	@Override
	public double execute(Parameters p, Context c, State s, Variables v) throws Exception {
		v.checkIf(OneVariable.class);

		double x1 = ((OneVariable) v).x1;

		return slope * x1;

	}

	/**
	 * expectedVariables()
	 */
	@Override
	public Class expectedVariables() {
		return OneVariable.class;
	}

}
