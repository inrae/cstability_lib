package capsis.lib.cstability.parameter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import capsis.lib.cstability.distribution.DiscretePositiveDistribution;
import capsis.lib.cstability.function.Function;
import capsis.lib.cstability.util.Format;

/**
 * MicrobeSpecies of the model C-STABILITY.
 * 
 * @author J. Sainte-Marie, F. de Coligny - March 2021
 */
@SuppressWarnings("serial")
public class MicrobeSpecies implements Serializable {

	private String name;
	private Map<String, DiscretePositiveDistribution> signatureMap; // Signature: key: biochemicalClassName
	private Map<String, Function> enzymeProductionMap; // Enzyme production: key: biochemicalClassName
	private Map<String, AssimilationElement> assimilationMap; // Assimilation: key: biochemicalClassName
	private Function mortalityFunction; // Mortality

	/**
	 * Inner Class
	 */
	private class AssimilationElement implements Serializable {
		public Function uptakeFluxFunction;
		public Function carbonUseEfficiencyFunction;

		/**
		 * Constructor
		 */
		public AssimilationElement(Function uptakeFluxFunction, Function carbonUseEfficiencyFunction) {
			this.uptakeFluxFunction = uptakeFluxFunction;
			this.carbonUseEfficiencyFunction = carbonUseEfficiencyFunction;
		}
	}

	/**
	 * Constructor
	 */
	public MicrobeSpecies(String name) {
		this.name = name;
		signatureMap = new HashMap<>();
		enzymeProductionMap = new HashMap<String, Function>();
		assimilationMap = new HashMap<>();
	}

	/**
	 * checkSignature(): check microbe species signature (sum = 1)
	 */
	public void checkSignature() throws Exception {
		double test = 0;
		for (String signatureName : signatureMap.keySet()) {
			DiscretePositiveDistribution signature = signatureMap.get(signatureName);
			test += signature.getIntegral();
		}
		if (Math.abs(test - 1.) > 10.e-9)
			throw new Exception("MicrobeSpecies, " + name + " Signature integral equals " + test + " instead of 1.");
	}

	/**
	 * addSignatureElement()
	 */
	public void addSignatureElement(String biochemicalClassName,
			DiscretePositiveDistribution polymerizationDistribution) {
		signatureMap.put(biochemicalClassName, polymerizationDistribution);
	}

	/**
	 * addEnzyme()
	 */
	public void addEnzyme(String enzymeName, Function productionFunction) throws Exception {
		if (enzymeProductionMap.containsKey(enzymeName))
			throw new Exception("MicrobeSpecies, " + name + " Enzyme, " + enzymeName + " is defined twice.");
		enzymeProductionMap.put(enzymeName, productionFunction);
	}

	/**
	 * addAssimilationElement()
	 */
	public void addAssimilationElement(String biochemicalClassName, Function uptakeFluxFunction,
			Function carbonUseEfficiencyFunction) throws Exception {
		if (assimilationMap.containsKey(biochemicalClassName))
			throw new Exception(
					"MicrobeSpecies, " + name + " Assimilation for " + biochemicalClassName + " is defined twice.");
		AssimilationElement ae = new AssimilationElement(uptakeFluxFunction, carbonUseEfficiencyFunction);
		assimilationMap.put(biochemicalClassName, ae);
	}

	/**
	 * setMortalityFunction()
	 */
	public void setMortalityFunction(Function mortalityFunction) {
		this.mortalityFunction = mortalityFunction;
	}

	/**
	 * getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * getEnzymeNames()
	 */
	public Set<String> getEnzymeNames() {
		return enzymeProductionMap.keySet();
	}

	/**
	 * getEnzymeProductionMap()
	 */
	public Map<String, Function> getEnzymeProductionMap() {
		return enzymeProductionMap;
	}

	/**
	 * getMortalityFunction()
	 */
	public Function getMortalityFunction() {
		return mortalityFunction;
	}

	/**
	 * getSignature()
	 */
	public DiscretePositiveDistribution getSignature(String biochemicalClassName) {
		return signatureMap.get(biochemicalClassName);
	}

	/**
	 * getSignatureBCNames()
	 */
	public Set<String> getSignatureBCNames() {
		return signatureMap.keySet();
	}

	/**
	 * getUptakeFluxFunction()
	 */
	public Function getUptakeFluxFunction(String biochemicalClassName) {
		return assimilationMap.get(biochemicalClassName).uptakeFluxFunction;
	}

	/**
	 * getCarbonUseEfficiencyFunction()
	 */
	public Function getCarbonUseEfficiencyFunction(String biochemicalClassName) {
		return assimilationMap.get(biochemicalClassName).carbonUseEfficiencyFunction;
	}

	/**
	 * getAssimilationBCNames()
	 */
	public Set<String> getAssimilationBCNames() {
		return assimilationMap.keySet();
	}

	/**
	 * toString()
	 */
	@Override
	public String toString() {
		String CR = "\n";
		String CR2 = "\n  ";

		return "MicrobeSpecies, " + name + CR2 + "signatureMap: " + Format.printKeys(signatureMap) + CR2
				+ "enzymeProductionMap: " + Format.printKeys(enzymeProductionMap) + CR2 + "assimilationMap: "
				+ Format.printKeys(assimilationMap) + CR;
	}

}
