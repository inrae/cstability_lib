package capsis.lib.cstability.observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capsis.lib.cstability.observer.observation.DistributionMapObservation;
import capsis.lib.cstability.observer.observation.DistributionObservation;
import capsis.lib.cstability.observer.observation.DoubleObservation;
import capsis.lib.cstability.observer.observation.Observation;
import capsis.lib.cstability.state.Enzyme;
import capsis.lib.cstability.state.Microbe;
import capsis.lib.cstability.state.Pool;
import capsis.lib.cstability.state.PoolTransfer;
import capsis.lib.cstability.state.State;
import capsis.lib.cstability.util.Format;

/**
 * A variable of C-STABILITY
 *
 * @author J. Sainte-Marie, F. de Coligny - April 2021
 */
@SuppressWarnings("serial")
public class ObservableVariable implements Serializable {

	private static Map<String, ObservableVariable> availableVariables;

	static {

		List<ObservableVariable> ovs = new ArrayList<>();

		ovs.add(new ObservableVariable("respiration", Type.STATE) {
			public Observation getValue(int date, Object state) {
				checkType(this, state, State.class);
				return new DoubleObservation(date, this, ((State) state).getRespiration());
			}
		});

		ovs.add(new ObservableVariable("mass", Type.POOL) {
			public Observation getValue(int date, Object pool) {
				checkType(this, pool, Pool.class);
				return new DoubleObservation(date, this, ((Pool) pool).getCarbonMass());
			}
		});

		ovs.add(new ObservableVariable("mass_distribution", Type.POOL) {
			public Observation getValue(int date, Object sp) {
				checkType(this, sp, Pool.class);
				return new DistributionObservation(date, this, (Pool) sp, "polymerization", "mass");
			}
		});

		ovs.add(new ObservableVariable("flux_distribution", Type.POOL_TRANSFER) {
			public Observation getValue(int date, Object pt) {
				checkType(this, pt, PoolTransfer.class);
				return new DistributionObservation(date, this, (PoolTransfer) pt, "polymerization", "flux");
			}
		});

		ovs.add(new ObservableVariable("mass", Type.MICROBE) {
			public Observation getValue(int date, Object m) {
				checkType(this, m, Microbe.class);
				return new DoubleObservation(date, this, ((Microbe) m).getMass());
			}
		});

		ovs.add(new ObservableVariable("uptake_flux_distribution_map", Type.MICROBE) {
			public Observation getValue(int date, Object m) {
				checkType(this, m, Microbe.class);
				return new DistributionMapObservation(date, this, ((Microbe) m).getUptakeFluxes(), "biochemical_class",
						"polymerization", "uptake_flux");
			}
		});

		ovs.add(new ObservableVariable("carbon_use_efficiency_distribution_map", Type.MICROBE) {
			public Observation getValue(int date, Object m) {
				checkType(this, m, Microbe.class);
				return new DistributionMapObservation(date, this, ((Microbe) m).getCUseEfficiencies(),
						"biochemical_class", "polymerization", "carbon_use_efficiency");
			}
		});

		ovs.add(new ObservableVariable("respiration", Type.MICROBE) {
			public Observation getValue(int date, Object m) {
				checkType(this, m, Microbe.class);
				return new DoubleObservation(date, this, ((Microbe) m).getRespiration());
			}
		});

		ovs.add(new ObservableVariable("mortality_flux", Type.MICROBE) {
			public Observation getValue(int date, Object m) {
				checkType(this, m, Microbe.class);
				return new DoubleObservation(date, this, ((Microbe) m).getMortalityFlux());
			}
		});

		ovs.add(new ObservableVariable("depolymerization_rate_distribution", Type.ENZYME) {
			public Observation getValue(int date, Object e) {
				checkType(this, e, Enzyme.class);
				return new DistributionObservation(date, this, ((Enzyme) e).getDepolymerizationRate(), "polymerization",
						"depolymerization_rate");
			}
		});

		ovs.add(new ObservableVariable("activity_distribution", Type.ENZYME) {
			public Observation getValue(int date, Object e) {
				checkType(this, e, Enzyme.class);
				return new DistributionObservation(date, this, ((Enzyme) e).getActivityDistribution(), "polymerization",
						"activity");
			}
		});

		availableVariables = new HashMap<>();
		for (ObservableVariable ov : ovs) {
			availableVariables.put(ov.getType() + "_" + ov.getName(), ov);
		}
	}

	private String name;
	private String type;

	/**
	 * Constructor
	 */
	private ObservableVariable(String name, String type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Class Type
	 */
	protected class Type {
		public static final String STATE = "state";
		public static final String MICROBE = "microbe";
		public static final String ENZYME = "enzyme";
		public static final String POOL = "pool";
		public static final String POOL_TRANSFER = "pool_transfer";
	}

	/**
	 * checkType()
	 */
	private static void checkType(ObservableVariable ov, Object o, Class klass) throws RuntimeException {
		if (!klass.isAssignableFrom(o.getClass()))
			throw new RuntimeException("ObservableVariable.checkType(), in variable name " + ov.name + " and type "
					+ ov.type + " wrong class: " + o.getClass() + " expected: " + klass);
	}

	/**
	 * getValue()
	 */
	public Observation getValue(int date, Object obj) {
		return null;
	}

	/**
	 * getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * getType()
	 */
	public String getType() {
		return type;
	}

	/**
	 * getObservableVariable()
	 */
	public static ObservableVariable getObservableVariable(String type, String name) throws Exception {
		String key = type + "_" + name;
		if (availableVariables.containsKey(key)) {
			return availableVariables.get(key);
		} else {
			throw new Exception("VariableType.getVariable(): unknown key " + key + ", should be "
					+ Format.toString(availableVariables.keySet(), ", "));
		}
	}

	/**
	 * getAvailableVariables()
	 */
	public static Map<String, ObservableVariable> getAvailableVariables() {
		return availableVariables;
	}

}
