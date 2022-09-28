package capsis.lib.cstability.filereader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capsis.lib.cstability.app.Simulator;
import capsis.lib.cstability.context.Context;
import capsis.lib.cstability.context.SubstrateInputManager;
import capsis.lib.cstability.function.Function;
import capsis.lib.cstability.observer.EnzymeObserver;
import capsis.lib.cstability.observer.MicrobeObserver;
import capsis.lib.cstability.observer.Observer;
import capsis.lib.cstability.observer.ObserverList;
import capsis.lib.cstability.observer.PoolObserver;
import capsis.lib.cstability.observer.PoolTransferObserver;
import capsis.lib.cstability.observer.StateObserver;
import capsis.lib.cstability.parameter.BiochemicalClass;
import capsis.lib.cstability.parameter.EnzymeTraits;
import capsis.lib.cstability.parameter.MicrobeSpecies;
import capsis.lib.cstability.parameter.Parameters;
import capsis.lib.cstability.parameter.PoolTransferTraits;
import capsis.lib.cstability.state.Enzyme;
import capsis.lib.cstability.state.PoolTransfer;
import capsis.lib.cstability.state.State;
import capsis.lib.cstability.state.Substrate;

/**
 * A loader for the C-Stability setup file.
 * 
 * @author J. Sainte-Marie, F. de Coligny - March 2021
 */
@SuppressWarnings("serial")
public class SetupFileLoader implements Serializable {

	private String fileName;
	private Parameters parameters;
	private Context context;
	private ObserverList observerList;

	// Setup file format
	private List<Decodable> decodables;
	private MicrobeDecoder microbeSpeciesDecoder;
	private PoolDecoder poolDecoder;
	private SubstrateInputManager substrateInputManager;

	/**
	 * Constructor
	 */
	public SetupFileLoader(String fileName) {
		this.fileName = fileName;
		initFormat();
	}

	/**
	 * initFormat(): declares the expected line formats in the setup file.
	 */
	private void initFormat() {
		decodables = new ArrayList<>();
		decodables.add(new LabeledNumber()); // Before LabeledString
		decodables.add(new LabeledString());
		decodables.add(new BiochemicalClass());
		decodables.add(new EnzymeTraits());
		decodables.add(new PoolTransferTraits());

		microbeSpeciesDecoder = new MicrobeDecoder();
		decodables.add(microbeSpeciesDecoder);
		poolDecoder = new PoolDecoder();
		decodables.add(poolDecoder);

		decodables.add(new StateObserver());
		decodables.add(new PoolObserver());
		decodables.add(new MicrobeObserver());
		decodables.add(new EnzymeObserver());
		decodables.add(new PoolTransferObserver());

		substrateInputManager = new SubstrateInputManager();
		decodables.add(substrateInputManager);
	}

	/**
	 * load(): load the data in the given simulator
	 */
	public void load(Simulator sim) throws Exception {

		this.parameters = sim.getParameters();
		this.context = new Context();
		this.observerList = new ObserverList();

		State s0 = new State();

		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = in.readLine()) != null) {
				line.trim();
				if (line.startsWith("#") || line.length() == 0)
					continue;
				processLine(s0, line);
			}
			in.close();

		} catch (Exception e) {
			throw new Exception("Could not read setupFile: " + fileName, e);
		}

		/**
		 * Store the microbeSpecies in Parameters
		 */
		for (String microbeName : microbeSpeciesDecoder.getMicrobeSpeciesMap().keySet()) {
			MicrobeSpecies ms = microbeSpeciesDecoder.getMicrobeSpeciesMap().get(microbeName);
			ms.checkSignature();
			parameters.addMicrobeSpecies(ms);
		}

		context.buildTimeline();

		/**
		 * Creation of initial state
		 */

		/**
		 * Creation of substrate pools
		 */
		Substrate substrate = new Substrate();
		substrate.init(parameters);
		Map<String, Function> poolFunctionMap = poolDecoder.getPoolFunctionMap();
		for (String key : poolFunctionMap.keySet()) {
			// initially, substratePool is a ZeroDiscretePositiveDistribution
			// we set here Y values of substratePool from X values according to f
			String bcName = poolDecoder.getBiochemicalClassName(key);
			String accessKey = poolDecoder.getAccessibilityKey(key);
			substrate.getPool(bcName, accessKey).setValuesY(parameters, context, s0, poolFunctionMap.get(key));
		}

		/**
		 * Creation of substrate inputs substrateInputManager requires substrate to be
		 * defined and we add it to context
		 */
		context.setSubstrateInputManager(substrateInputManager);

		/**
		 * Creation of microbes state
		 */
		for (String microbeName : microbeSpeciesDecoder.getMicrobeMap().keySet()) {
			s0.addMicrobe(microbeSpeciesDecoder.getMicrobeMap().get(microbeName));
		}

		/**
		 * Creation poolTransfer state
		 */
		for (String pttName : parameters.getPoolTransferTraitsMap().keySet()) {
			PoolTransfer pt = new PoolTransfer(pttName, parameters);
			s0.addPoolTransfer(pt);
		}

		/**
		 * Creation enzyme state
		 */
		for (String enzymeName : parameters.getEnzymeTraitsMap().keySet()) {
			Enzyme e = new Enzyme(parameters.getEnzymeTraitsMap().get(enzymeName));
			s0.addEnzyme(e);
		}

		/**
		 * Loading environment
		 */
		context.buildEnvironmentList();

		/**
		 * Evaluation of initial state and storage in simulation
		 */
		s0.evaluate(substrate, parameters, context);
		sim.setState0(s0);
	}

	/**
	 * processLine()
	 */
	private void processLine(State s0, String line) throws Exception {

		StringBuffer decoderExceptions = new StringBuffer();

		Decodable decoded = null;
		for (Decodable prototype : decodables) {
			try {
				decoded = prototype.decode(line, parameters, context);
				break;
			} catch (Exception e) {
				// try with next decodable prototype
				decoderExceptions.append("\n" + e.toString());
				if (e.getCause() != null)
					decoderExceptions.append(", caused by: " + e.getCause());
			}
		}

		if (decoded == null)
			throw new Exception("Unexpected line in: " + fileName + ": " + line
					+ "\nExceptions returned by the decoders: " + decoderExceptions);

		if (decoded instanceof LabeledNumber) {
			LabeledNumber ln = (LabeledNumber) decoded;
			if (ln.getLabel().equals("initialDate")) {
				context.setInitialDate(ln.getInt());
			} else if (ln.getLabel().equals("finalDate")) {
				context.setFinalDate(ln.getInt());
			} else if (ln.getLabel().equals("userTimeStep")) {
				context.setUserTimeStep(ln.getDouble());
			} else if (ln.getLabel().equals("userPolymerizationStep")) {
				parameters.setUserPolymerizationStep(ln.getDouble());
			} else {
				throw new Exception("Unknown labeledNumber: " + ln.getLabel());
			}

		} else if (decoded instanceof LabeledString) {
			LabeledString ls = (LabeledString) decoded;
			if (ls.getLabel().equals("timeUnit")) {
				context.setTimeUnit(ls.getString());
			} else if (ls.getLabel().equals("integrationMethod")) {
				parameters.setIntegrationMethod(ls.getString());
			} else {
				throw new Exception("Unknown labeledString " + ls.getLabel());
			}

		} else if (decoded instanceof BiochemicalClass) {
			parameters.addBiochemicalClass((BiochemicalClass) decoded);

		} else if (decoded instanceof EnzymeTraits) {
			parameters.addEnzymeTraits((EnzymeTraits) decoded);

		} else if (decoded instanceof PoolTransferTraits) {
			parameters.addPoolTransferTraits((PoolTransferTraits) decoded);

		} else if (decoded instanceof Observer) {
			observerList.addObserver((Observer) decoded);

		}
	}

	/**
	 * getContext()
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * getObserverList()
	 */
	public ObserverList getObserverList() {
		return observerList;
	}
}
