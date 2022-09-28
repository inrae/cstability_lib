package capsis.lib.cstability.parameter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import capsis.lib.cstability.util.Format;

/**
 * Accessibility of the substrate.
 * 
 * @author J. Sainte-Marie, F. de Coligny - February 2021
 */
@SuppressWarnings("serial")
public class SubstrateAccessibility implements Serializable {

	public static final SubstrateAccessibility ACCESSIBLE = new SubstrateAccessibility("ACCESSIBLE", "accessible",
			true);
	public static final SubstrateAccessibility INACCESSIBLE_AGGREGATION = new SubstrateAccessibility(
			"INACCESSIBLE_AGGREGATION", "inaccessible_aggregation", false);
	public static final SubstrateAccessibility INACCESSIBLE_MINERAL_ASSOCIATION = new SubstrateAccessibility(
			"INACCESSIBLE_MINERAL_ASSOCIATION", "inaccessible_mineral_association", false);
	public static final SubstrateAccessibility INACCESSIBLE_EMBEDMENT = new SubstrateAccessibility(
			"INACCESSIBLE_EMBEDMENT", "Inaccessible_embedment", false);

	private static Map<String, SubstrateAccessibility> availableSubstrateAccessibilities;

	static {
		availableSubstrateAccessibilities = new HashMap<>();
		availableSubstrateAccessibilities.put("ACCESSIBLE", ACCESSIBLE);
		availableSubstrateAccessibilities.put("INACCESSIBLE_AGGREGATION", INACCESSIBLE_AGGREGATION);
		availableSubstrateAccessibilities.put("INACCESSIBLE_MINERAL_ASSOCIATION", INACCESSIBLE_MINERAL_ASSOCIATION);
		availableSubstrateAccessibilities.put("INACCESSIBLE_EMBEDMENT", INACCESSIBLE_EMBEDMENT);
	}

	private String key; // "ACCESSIBLE", "INACCESSIBLE_AGGREGATION"....
	private String status; // "accessible", "inaccessible_aggregation"....
	private boolean accessible;

	/**
	 * Constructor
	 */
	private SubstrateAccessibility(String key, String status, boolean accessible) {
		this.key = key;
		this.status = status;
		this.accessible = accessible;
	}

	/**
	 * isAccessible()
	 */
	public boolean isAccessible() {
		return accessible;
	}

	/**
	 * getKey()
	 */
	public String getKey() {
		return key;
	}

	/**
	 * getStatus()
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * getSubstrateAccessibility()
	 */
	public static SubstrateAccessibility getSubstrateAccessibility(String key) throws Exception {
		if (availableSubstrateAccessibilities.containsKey(key)) {
			return availableSubstrateAccessibilities.get(key);
		} else {
			throw new Exception("SubstrateAccessibility.getSubstrateAccessibility(): unknown key " + key
					+ ", should be " + Format.toString(availableSubstrateAccessibilities.keySet(), ", "));
		}
	}

	/**
	 * toString()
	 */
	@Override
	public String toString() {
		return "SubstrateAccessibility, key: " + key + ", status: " + status + ", accessible: " + accessible;
	}

}
