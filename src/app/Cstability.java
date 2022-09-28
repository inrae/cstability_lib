package capsis.lib.cstability.app;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

import capsis.lib.cstability.filereader.SetupFileLoader;
import capsis.lib.cstability.util.Log;

/**
 * Starter of C-STABILITY library.
 *
 * to launch a simulation:
 * 
 * java -cp ./class capsis.lib.cstability.app.Cstability setupFileName
 *
 * @author J. Sainte-Marie, F. de Coligny - February 2021
 * 
 *         TODO :
 *         * add a mass conservation check
 *         * add a display option to the main method to log display (-D option)
 *         * add a debug option for Log
 */
@SuppressWarnings("serial")
public class Cstability implements Serializable {

	private String outputDir;
	private String simulationName;
	private boolean appendMode;
	private Simulator simulator;
	private SetupFileLoader setupFileLoader;

	/**
	 * main()
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 1) {
			String setupFilePath = args[0];
			Cstability starter = new Cstability(setupFilePath);
			starter.run();
		} else {
			usage();
		}
	}

	/**
	 * usage()
	 */
	private static void usage() {
		System.out.println("C-STABILITY");
		System.out.println("  Expects a setupFileName parameter:");
		System.out.println("  java -cp ./class capsis.lib.cstability.app.Cstability setupFileName");
	}

	/**
	 * Constructor
	 */
	public Cstability(String setupFilePath) throws Exception {

		appendMode = false; // set to false for the moment but may be true in the future

		manageOutputDirectory(setupFilePath);

		Log.init(outputDir, simulationName + ".log");

		simulator = new Simulator(outputDir);

		Log.trace("Loading file " + setupFilePath + "...");
		setupFileLoader = simulator.load(setupFilePath);
	}

	/**
	 * manageOutputDirectory()
	 */
	private void manageOutputDirectory(String filePath) throws Exception {

		File f = new File(filePath);
		String workingDirectory = f.getParent();
		String fileName = f.getName();

		simulationName = fileName;
		if (fileName.contains("."))
			simulationName = fileName.substring(0, fileName.lastIndexOf("."));

		outputDir = workingDirectory + "/output_" + simulationName;

		if (!Files.exists(Paths.get(outputDir))) {
			appendMode = false;
			File od = new File(outputDir);
			od.mkdirs();
		}

		if (!appendMode) {
			for (File file : new File(outputDir).listFiles()) {
				if (file.isFile())
					Files.delete(Paths.get(file.getPath()));
			}
		}
	}

	/**
	 * run()
	 */
	public void run() throws Exception {

		Log.trace("Starting simulation...");
		simulator.execute(setupFileLoader.getContext(), setupFileLoader.getObserverList());
		Log.trace("Simulation completed");

		Log.trace("Writing observations...");
		simulator.writeObservations(outputDir, appendMode);

		Log.close();
	}

	/**
	 * getSimulator()
	 */
	public Simulator getSimulator() {
		return simulator;
	}
}
