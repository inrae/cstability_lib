package capsis.lib.cstability.observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.context.Timeline;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.parameter.PoolTransferTraits;
import capsis.lib.cstability.state.Enzyme;
import capsis.lib.cstability.state.Microbe;
import capsis.lib.cstability.state.Pool;
import capsis.lib.cstability.state.PoolTransfer;
import capsis.lib.cstability.state.State;
import capsis.lib.cstability.state.Substrate;

/**
 * The observerList which stores observers.
 *
 * @author J. Sainte-Marie, F. de Coligny - April 2021
 */
@SuppressWarnings("serial")
public class ObserverList implements Serializable {

	private List<Observer> observers;

	/**
	 * Constructor
	 */
	public ObserverList() {
		observers = new ArrayList<>();
	}

	/**
	 * observe()
	 */
	public void observe(State s) throws Exception {

		double date = s.getDate();

		if ((int) date != date)
			return;

		int d = (int) date;

		for (Observer o : observers) {
			o.observe(s, d);
		}
	}

	/**
	 * setDefaultObserverList()
	 */
	public void setDefaultOberverList(State s, Context c, Parameters p) throws Exception {

		Timeline tl = c.getTimeline();
		List<Integer> datesToObserve = new ArrayList<>();
		for (int i = tl.getMin(); i <= tl.getMax(); ++i) {
			datesToObserve.add(i);
		}

		Map<String, ObservableVariable> availableVariables = ObservableVariable.getAvailableVariables();

		for (String key : availableVariables.keySet()) {
			ObservableVariable av = availableVariables.get(key);
			if (av.getType().equals(ObservableVariable.Type.STATE)) {
				String variableName = av.getName();
				observers.add(new StateObserver(variableName, datesToObserve));
			}
		}

		for (Microbe m : s.getMicrobes()) {
			for (String key : availableVariables.keySet()) {
				ObservableVariable av = availableVariables.get(key);
				if (av.getType().equals(ObservableVariable.Type.MICROBE)) {
					String variableName = av.getName();
					observers.add(new MicrobeObserver(m.getSpecies().getName(), variableName, datesToObserve));
				}
			}
		}

		for (Enzyme e : s.getEnzymes()) {
			for (String key : availableVariables.keySet()) {
				ObservableVariable av = availableVariables.get(key);
				if (av.getType().equals(ObservableVariable.Type.ENZYME)) {
					String variableName = av.getName();
					observers.add(new EnzymeObserver(e.getTraits().getName(), variableName, datesToObserve));
				}
			}
		}

		for (PoolTransfer pt : s.getPoolTransfers()) {
			for (String key : availableVariables.keySet()) {
				ObservableVariable av = availableVariables.get(key);
				if (av.getType().equals(ObservableVariable.Type.POOL_TRANSFER)) {
					String variableName = av.getName();
					PoolTransferTraits ptt = pt.getTraits();
					observers.add(new PoolTransferObserver(ptt.getBiochemicalClass().getName(), ptt.getOriginName(),
							ptt.getArrivalName(), variableName, datesToObserve, p));
				}
			}
		}

		Substrate sub = s.getSubstrate();
		Set<String> bcNames = sub.getAccessibleBCNames();
		for (String bcName : bcNames) {
			Pool asp = sub.getAccessiblePool(bcName);
			String accessibilityName = asp.getAccessibility().getKey();
			for (String key : availableVariables.keySet()) {
				ObservableVariable av = availableVariables.get(key);
				if (av.getType().equals(ObservableVariable.Type.POOL)) {
					String variableName = av.getName();
					observers.add(new PoolObserver(bcName, accessibilityName, variableName, datesToObserve));
				}
			}
			List<Pool> ispl = sub.getInaccessiblePools(bcName);
			for (Pool isp : ispl) {
				accessibilityName = isp.getAccessibility().getKey();
				for (String key : availableVariables.keySet()) {
					ObservableVariable av = availableVariables.get(key);
					if (av.getType().equals(ObservableVariable.Type.POOL)) {
						String variableName = av.getName();
						observers.add(new PoolObserver(bcName, accessibilityName, variableName, datesToObserve));
					}
				}
			}
		}
	}

	/**
	 * isEmpty()
	 */
	public boolean isEmpty() {
		if (observers.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * addObserver()
	 */
	public void addObserver(Observer o) {
		observers.add(o);
	}

	/**
	 * write()
	 */
	public void write(String outputDir, boolean appendMode) throws Exception {
		for (Observer o : observers) {
			o.write(outputDir, appendMode);
		}
	}
}
