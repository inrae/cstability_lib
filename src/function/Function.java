package capsis.lib.cstability.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.context.poolinput.ConstantInputFunction;
import capsis.lib.cstability.filereader.Decodable;
import capsis.lib.cstability.function.util.Variables;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.state.State;

/**
 * A Function of the model C-STABILITY.
 * 
 * @author J. Sainte-Marie, F. de Coligny - March 2021
 */
@SuppressWarnings("serial")
public abstract class Function implements Decodable, Serializable {

	static private List<Function> availableFunctions;

	static {
		availableFunctions = new ArrayList<>();
		// Add prototypes in the list (a prototype is a non functional instance, useful
		// for decoding an encodedString)
		availableFunctions.add(new Constant());
		availableFunctions.add(new Gaussian());
		availableFunctions.add(new GaussianTruncatedNormalized());
		availableFunctions.add(new GaussianTruncatedProportionalized());
		availableFunctions.add(new KernelAlpha());
		availableFunctions.add(new Linear());
		availableFunctions.add(new UniformLinear());
		availableFunctions.add(new EnzymaticLinearTransfer());
		availableFunctions.add(new ConstantInputFunction());
		// Add other functions here...

	}

	/**
	 * getFunction(): decodes and returns the Function matching the given
	 * encodedFuncton String.
	 */
	public static Function getFunction(String encodedFunction, Parameters p, Context c) throws Exception {

		for (Function functionPrototype : availableFunctions) {
			try {
				return (Function) functionPrototype.decode(encodedFunction, p, c);
			} catch (Exception e) {
				// try with next available function
			}
		}

		throw new Exception("Function.getFunction (), could not decode this function: " + encodedFunction);
	}

	/**
	 * Constructor
	 */
	public Function() {

	}

	/**
	 * execute()
	 */
	public abstract double execute(Parameters p, Context c, State s, Variables v) throws Exception;

	/**
	 * expectedVariables(): return OneVariable or TwoVariables depending on what is
	 * expected in execute
	 */
	public abstract Class expectedVariables();

}
