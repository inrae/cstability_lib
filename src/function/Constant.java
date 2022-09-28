package capsis.lib.cstability.function;

import java.util.StringTokenizer;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.function.util.Variables;
import capsis.lib.cstability.function.util.ZeroVariable;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.state.State;

/**
 * A Constant
 * 
 * @author J. Sainte-Marie, F. de Coligny - March 2021
 */
@SuppressWarnings("serial")
public class Constant extends Function {

	// Note: Function implements Decodable

	private double constant;

	/**
	 * Default constructor
	 */
	public Constant() {
	}

	/**
	 * Constructor
	 */
	public Constant(double constant) {
		this.constant = constant;

	}

	/**
	 * decode(): decoding method from an encoded string
	 */
	@Override
	public Constant decode(String encodedString, Parameters p, Context c) throws Exception {

		// e.g. constant(0.3)

		try {
			String s = encodedString.trim();

			if (!s.startsWith("constant("))
				throw new Exception("Not a constant");

			s = encodedString.replace("constant(", "");
			s = s.replace(")", "");
			StringTokenizer st = new StringTokenizer(s, ";");

			constant = Double.parseDouble(st.nextToken().trim());

			return new Constant(constant);

		} catch (Exception e) {
			throw new Exception("Constant.decode (), could not parse this encodedString: " + encodedString, e);
		}
	}

	/**
	 * execute()
	 */
	@Override
	public double execute(Parameters p, Context c, State s, Variables v) throws Exception {
		v.checkIf(ZeroVariable.class);

		return constant;
	}

	/**
	 * expectedVariables()
	 */
	@Override
	public Class expectedVariables() {
		return ZeroVariable.class;
	}

}
