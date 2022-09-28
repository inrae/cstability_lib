package capsis.lib.cstability.function;

import java.util.StringTokenizer;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.filereader.Decodable;
import capsis.lib.cstability.function.util.BasicFunctions;
import capsis.lib.cstability.function.util.TwoVariables;
import capsis.lib.cstability.function.util.Variables;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.state.State;
import capsis.lib.cstability.util.Interval;

/**
 * A uniform linear function.
 * 
 * @author J. Sainte-Marie, F. de Coligny - March 2021
 */
@SuppressWarnings("serial")
public class UniformLinear extends Function {

	// Note: Function implements Decodable

	private Interval<Double> domain;
	private double slope;

	/**
	 * Default constructor
	 */
	public UniformLinear() {
	}

	/**
	 * Constructor
	 */
	public UniformLinear(Interval<Double> domain, double slope) {
		this.domain = domain;
		this.slope = slope;
	}

	/**
	 * decode(): decoding method from an encoded string
	 */
	@Override
	public UniformLinear decode(String encodedString, Parameters p, Context c) throws Exception {

		// e.g. uniformLinear(1;[0,0.4])

		try {
			String s = encodedString.trim();

			if (!s.startsWith("uniformLinear("))
				throw new Exception("Not a uniformLinear");

			s = encodedString.replace("uniformLinear(", "");
			s = s.replace(")", "");
			StringTokenizer st = new StringTokenizer(s, ";");

			domain = (Interval<Double>) Decodable.pleaseDecode(Interval.class, st.nextToken().trim(), p, c);

			slope = Double.parseDouble(st.nextToken().trim());

			return new UniformLinear(domain, slope);

		} catch (Exception e) {
			throw new Exception("UniformLinear.decode (), could not parse this encodedString: " + encodedString, e);
		}

	}

	/**
	 * execute(): the function is uniform on the domain (x1) and then linear (x2)
	 */
	@Override
	public double execute(Parameters p, Context c, State s, Variables v) throws Exception {
		v.checkIf(TwoVariables.class);

		double x1 = ((TwoVariables) v).x1;
		double x2 = ((TwoVariables) v).x2;

		return BasicFunctions.indicator(domain, x1) * slope * x2;

	}

	/**
	 * expectedVariables()
	 */
	@Override
	public Class expectedVariables() {
		return TwoVariables.class;
	}

}
