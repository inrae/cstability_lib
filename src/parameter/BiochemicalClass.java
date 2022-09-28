package capsis.lib.cstability.parameter;

import java.io.Serializable;
import java.util.StringTokenizer;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.filereader.Decodable;
import capsis.lib.cstability.util.Interval;

/**
 * A biochemical class of C-Stability.
 * 
 * @author J. Sainte-Marie, F. de Coligny - February 2021
 */
@SuppressWarnings("serial")
public class BiochemicalClass implements Comparable<BiochemicalClass>, Decodable, Serializable {

	private String name; // e.g. Lipid
	private Polymerization polymerization;

	/**
	 * Default constructor
	 */
	public BiochemicalClass() {
	}

	/**
	 * Constructor
	 */
	public BiochemicalClass(String name, Polymerization polymerization) {
		this.name = name;
		this.polymerization = polymerization;
	}

	/**
	 * compareTo()
	 */
	@Override
	public int compareTo(BiochemicalClass bc) {
		return this.getName().compareTo(bc.getName());
	}

	/**
	 * decode(): decoding method from an encoded string
	 */
	@Override
	public BiochemicalClass decode(String encodedString, Parameters p, Context c) throws Exception {

		// e.g. BIOCHEMICAL_CLASS lipid [0,2]

		try {
			String s = encodedString.trim();
			// String s = encodedString.replace ("pow2p(", "");
			// s = s.replace (")", "");
			StringTokenizer st = new StringTokenizer(s, "\t");

			String flag = st.nextToken().trim();
			if (flag.equals("BIOCHEMICAL_CLASS")) {
				String name = st.nextToken().trim();

				Interval<Double> minMax = (Interval<Double>) Decodable.pleaseDecode(Interval.class,
						st.nextToken().trim(), p, c);
				double userStep = p.getUserPolymerizationStep();
				Polymerization polymerization = new Polymerization(minMax.getMin(), minMax.getMax(), userStep);

				return new BiochemicalClass(name, polymerization);
			} else {
				throw new Exception("wrong flag, except BIOCHEMICAL_CLASS");
			}

		} catch (Exception e) {
			throw new Exception("BiochemicalClass.decode (), could not parse this encodedString: " + encodedString, e);
		}

	}

	/**
	 * getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * getPolymerization()
	 */
	public Polymerization getPolymerization() {
		return polymerization;
	}

	/**
	 * toString()
	 */
	@Override
	public String toString() {
		return "BiochemicalClass, name: " + name + ", polymerization: " + polymerization;
	}
}
