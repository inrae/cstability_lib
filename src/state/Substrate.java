package capsis.lib.cstability.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.context.poolinput.PoolInput;
import capsis.lib.cstability.distribution.DiscreteDistribution;
import capsis.lib.cstability.parameter.BiochemicalClass;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.parameter.SubstrateAccessibility;
import capsis.lib.cstability.util.Format;

/**
 * Substrate contains pools.
 * 
 * @author J. Sainte-Marie, F. de Coligny - February 2021
 */
@SuppressWarnings("serial")
public class Substrate implements Serializable {

	// key: bcName, value: pool
	private Map<String, Pool> accessiblePoolMap;
	private Map<String, List<Pool>> inaccessiblePoolMap;

	/**
	 * Constructor
	 */
	public Substrate() {
		this.accessiblePoolMap = new HashMap<>(); // contains only one accessible pool
		this.inaccessiblePoolMap = new HashMap<>(); // can contain zero pool
	}

	/**
	 * Constructor for duplication
	 */
	public Substrate(Substrate substrate) throws Exception {
		this();
		for (String bcName : substrate.accessiblePoolMap.keySet()) {
			Pool accessiblePool = substrate.accessiblePoolMap.get(bcName);
			addAccessiblePool(bcName, accessiblePool.copy());
		}
		for (String bcName : substrate.inaccessiblePoolMap.keySet()) {
			List<Pool> inaccessiblePools = substrate.inaccessiblePoolMap.get(bcName);
			for (Pool isp : inaccessiblePools) {
				addInaccessiblePool(bcName, isp.copy());
			}
		}
	}

	/**
	 * manageInputs() : Add substrate inputs
	 */
	public void manageInputs(Parameters p, Context c, State s, double date) throws Exception {
		List<Pool> poolList = new ArrayList<>(accessiblePoolMap.values());
		for (String key : inaccessiblePoolMap.keySet()) {
			poolList.addAll(inaccessiblePoolMap.get(key));
		}

		for (Pool pool : poolList) {
			String poolKey = pool.getKey();
			PoolInput pi = c.getSubstrateInputManager().getPoolInput(p, c, s, poolKey, date);
			if (pi != null) {
				// pool = pool + dt*pi
				DiscreteDistribution temp = DiscreteDistribution.mult(c.getUserTimeStep(), pi);
				pool.add(temp);
			}
		}
	}

	/**
	 * init(): initialize empty pools of substrate according to parameters
	 */
	public void init(Parameters p) throws Exception {
		for (String bcName : p.getSubstrateAccessibilityMap().keySet()) {
			List<SubstrateAccessibility> list = p.getSubstrateAccessibilityMap().get(bcName);
			BiochemicalClass bc = p.getBiochemicalClassMap().get(bcName);
			for (SubstrateAccessibility sa : list) {
				Pool sp = Pool.getEmptySubstratePool(bc, sa, p.getIntegrationMethod());
				this.addPool(bcName, sp);
			}
		}
	}

	/**
	 * addPool()
	 */
	public void addPool(String bcName, Pool pool) throws Exception {
		if (pool.isAccessible())
			addAccessiblePool(bcName, pool);
		else
			addInaccessiblePool(bcName, pool);
	}

	/**
	 * addAccessiblePool()
	 */
	public void addAccessiblePool(String bcName, Pool pool) throws Exception {
		if (!pool.isAccessible())
			throw new Exception("Substrate.addAccessiblePool, pool is not accessible:" + pool);
		if (accessiblePoolMap.containsKey(bcName))
			throw new Exception("Substrate.addAccessiblePool, pool key already exists");
		this.accessiblePoolMap.put(bcName, pool);
	}

	/**
	 * addInaccessiblePool()
	 */
	public void addInaccessiblePool(String bcName, Pool pool) throws Exception {
		if (pool.isAccessible())
			throw new Exception("Substrate.addInaccessiblePool, pool is accessible:" + pool);
		List<Pool> pools = inaccessiblePoolMap.get(bcName);
		if (pools == null) {
			pools = new ArrayList<>();
			inaccessiblePoolMap.put(bcName, pools);
		}
		for (Pool p : pools) {
			if (p.getAccessibility().getStatus().equals(pool.getAccessibility().getStatus()))
				throw new Exception("Substrate.addInaccessiblePool, pool accessibility cannot be added twice: "
						+ p.getAccessibility().getStatus());
		}
		pools.add(pool);
	}

	/**
	 * getPool()
	 */
	public Pool getPool(String bcName, String accessKey) {
		if (accessKey.equals(SubstrateAccessibility.ACCESSIBLE.getKey()))
			return accessiblePoolMap.get(bcName);
		else {
			Pool res = null;
			for (Pool sp : inaccessiblePoolMap.get(bcName)) {
				if (sp.getAccessibility().getKey().equals(accessKey)) {
					res = sp;
					break;
				}
			}
			return res;
		}
	}

	/**
	 * getAccessiblePool()
	 */
	public Pool getAccessiblePool(String bcName) throws Exception {
		Pool p = accessiblePoolMap.get(bcName);
		if (p == null)
			throw new Exception("Substrate.getAccessiblePool, accessible pool does not exist for: " + bcName);
		return p;
	}

	/**
	 * getAccessibleBCNames()
	 */
	public Set<String> getAccessibleBCNames() {
		return accessiblePoolMap.keySet();
	}

	/**
	 * getInaccessiblePools()
	 */
	public List<Pool> getInaccessiblePools(String bcName) {
		List<Pool> pools = inaccessiblePoolMap.get(bcName);
		if (pools == null)
			pools = new ArrayList<>();
		return pools;
	}

	/**
	 * toString()
	 */
	@Override
	public String toString() {

		String CR = "\n";
		StringBuffer b = new StringBuffer("--- Substrate");

		b.append(CR);
		b.append("accessiblePoolMap: " + Format.toString(accessiblePoolMap));

		b.append(CR);
		b.append("inaccessiblePoolMap: " + Format.toString(inaccessiblePoolMap));

		b.append(CR);
		b.append("--- end-of-Substrate");

		return b.toString();
	}

}
