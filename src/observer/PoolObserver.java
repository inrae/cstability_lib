package capsis.lib.cstability.observer;

import java.util.List;
import java.util.StringTokenizer;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.parameter.SubstrateAccessibility;
import capsis.lib.cstability.state.Pool;
import capsis.lib.cstability.state.State;
import capsis.lib.cstability.util.Date;

/**
 * The observer which extract data from current state of a substrate pool.
 *
 * @author J. Sainte-Marie, F. de Coligny - April 2021
 */
@SuppressWarnings("serial")
public class PoolObserver extends Observer {

	private String poolName;
	private SubstrateAccessibility accessibility;

	/**
	 * Constructor: default for Decodable
	 */
	public PoolObserver() {
		super();
	}

	/**
	 * Constructor
	 */
	public PoolObserver(String poolName, String accessibilityName, String variableName, List<Integer> datesToObserve)
			throws Exception {
		super(ObservableVariable.Type.POOL, variableName, datesToObserve);
		this.poolName = poolName;
		this.accessibility = SubstrateAccessibility.getSubstrateAccessibility(accessibilityName);
	}

	/**
	 * getPoolName()
	 */
	public String getPoolName() {
		return poolName;
	}

	/**
	 * getAccessibility()
	 */
	public SubstrateAccessibility getAccessibility() {
		return accessibility;
	}

	/**
	 * getObservedItemName()
	 */
	public String getObservedItemName() {
		return poolName + "-" + accessibility.getStatus();
	}

	/**
	 * observe()
	 */
	public void observe(State s, int date) throws Exception {
		if (datesToObserve.contains(date)) {
			Pool sp = s.getSubstrate().getPool(poolName, accessibility.getKey());
			observations.add(observableVariable.getValue(date, sp));
		}
	}

	/**
	 * decode()
	 */
	@Override
	public PoolObserver decode(String encodedString, Parameters p, Context c) throws Exception {

		try {
			String s = encodedString.trim();
			StringTokenizer st = new StringTokenizer(s, "\t");

			String flag = st.nextToken().trim();
			// POOL_OBSERVER [lipid, ACCESSIBLE] polymerization [365]
			if (flag.equals("POOL_OBSERVER")) {

				String temp = st.nextToken().trim();
				temp = temp.replace("[", "");
				temp = temp.replace("]", "");
				StringTokenizer st2 = new StringTokenizer(temp, ",");

				String poolName = st2.nextToken().trim();
				String accessibilityName = st2.nextToken().trim();

				String variableName = st.nextToken().trim();
				String datesString = st.nextToken().trim();

				return new PoolObserver(poolName, accessibilityName, variableName, Date.read(datesString));
			} else {
				throw new Exception("Wrong flag, expect POOL_OBSERVER");
			}

		} catch (Exception e) {
			throw new Exception("PoolObserver.decode (), could not parse this encodedString: " + encodedString, e);
		}
	}
}
