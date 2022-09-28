package capsis.lib.cstability.context.poolinput;

import java.util.StringTokenizer;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.function.Function;
import capsis.lib.cstability.function.util.OneVariable;
import capsis.lib.cstability.function.util.TwoVariables;
import capsis.lib.cstability.function.util.Variables;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.state.State;

/**
 * A Constant input function of C-STABILITY library
 * 
 * @author J. Sainte-Marie, F. de Coligny - May 2021
 */
@SuppressWarnings("serial")
public class ConstantInputFunction extends Function {

	private double constant;
	private Function signature;

	/**
	 * Default constructor required to use Decodable, Function implements Decodable
	 */
	public ConstantInputFunction() {
	}

	/**
	 * Constructor
	 */
	public ConstantInputFunction(double constant, Function signature) {
		this.constant = constant;
		this.signature = signature;
	}

	/**
	 * decode(): decoding method from an encoded string
	 */
	@Override
	public ConstantInputFunction decode(String encodedString, Parameters p, Context c) throws Exception {

		// e.g. constantInput(0.3:signatureFunction)

		try {
			String s = encodedString.trim();

			if (!s.startsWith("constantInput("))
				throw new Exception("Not a constantInput");

			s = encodedString.replace("constantInput(", "");
			s = s.replace(")", "");
			StringTokenizer st = new StringTokenizer(s, ":");

			constant = Double.parseDouble(st.nextToken().trim());

			signature = Function.getFunction(st.nextToken().trim(), p, c);

			return new ConstantInputFunction(constant, signature);

		} catch (Exception e) {
			throw new Exception("ConstantInput.decode (), could not parse this encodedString: " + encodedString, e);
		}
	}

	/**
	 * execute()
	 */
	@Override
	public double execute(Parameters p, Context c, State s, Variables v) throws Exception {
		v.checkIf(TwoVariables.class);

		double date = ((TwoVariables) v).x1;
		double x = ((TwoVariables) v).x2;

		return constant * signature.execute(p, c, s, new OneVariable(x));
	}

	@Override
	public Class expectedVariables() {
		return TwoVariables.class;
	}

}
