package capsis.lib.cstability.function;

import java.util.StringTokenizer;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.function.util.OneVariable;
import capsis.lib.cstability.function.util.Variables;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.state.State;

/**
 * A linear enzymatic transfer
 * 
 * @author J. Sainte-Marie, F. de Coligny - May 2021
 */
@SuppressWarnings("serial")
public class EnzymaticLinearTransfer extends Function {

	// Note: Function implements Decodable

	private String enzymeName;
	private double transferRate;

	/**
	 * Default constructor
	 */
	public EnzymaticLinearTransfer() {
	}

	/**
	 * Constructor
	 */
	public EnzymaticLinearTransfer(String enzymeName, double transferRate) {
		this.enzymeName = enzymeName;
		this.transferRate = transferRate;
	}

	/**
	 * decode(): decoding method from an encoded string
	 */
	@Override
	public EnzymaticLinearTransfer decode(String encodedString, Parameters p, Context c) throws Exception {

		// e.g. enzymaticLinearTransfer(cellulase;1)

		try {
			String s = encodedString.trim();

			if (!s.startsWith("enzymaticLinearTransfer("))
				throw new Exception("Not an EnzymaticLinearTransfer");

			s = encodedString.replace("enzymaticLinearTransfer(", "");
			s = s.replace(")", "");
			StringTokenizer st = new StringTokenizer(s, ";");

			enzymeName = st.nextToken().trim();
			if (!p.getEnzymeTraitsMap().keySet().contains(enzymeName))
				throw new Exception("EnzymaticLinearTransfer.decode (), unknown enzymeName " + enzymeName);

			transferRate = Double.parseDouble(st.nextToken().trim());

			return new EnzymaticLinearTransfer(enzymeName, transferRate);

		} catch (Exception e) {
			throw new Exception(
					"EnzymaticLinearTransfer.decode (), could not parse this encodedString: " + encodedString, e);
		}
	}

	/**
	 * execute()
	 */
	@Override
	public double execute(Parameters p, Context c, State s, Variables v) throws Exception {
		v.checkIf(OneVariable.class);
		// x is a value of the transfered distribution
		double x = ((OneVariable) v).x1;
		
		return transferRate * s.getEnzyme(enzymeName).getActivity() * x;
	}

	/**
	 * expectedVariables()
	 */
	@Override
	public Class expectedVariables() {
		return OneVariable.class;
	}

}
