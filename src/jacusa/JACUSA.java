/*
    JAVA framework for accurate SNV assessment (JACUSA) is a one-stop solution to detect single
nucleotide variants (SNVs) from comparing matched sequencing samples.
    Copyright (C) 2015  Michael Piechotta

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jacusa;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.CLI;
import jacusa.method.AbstractMethodFactory;
import jacusa.method.call.OneConditionCallFactory;
import jacusa.method.call.TwoConditionCallFactory;
import jacusa.method.call.CallFactory;
import jacusa.method.pileup.nConditionPileupFactory;
import jacusa.method.rtarrest.RTArrestFactory;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.util.Logger;
import jacusa.util.SimpleTimer;
import jacusa.util.coordinateprovider.BedCoordinateProvider;
import jacusa.util.coordinateprovider.CoordinateProvider;
import jacusa.util.coordinateprovider.ThreadedCoordinateProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Michael Piechotta
 */
public class JACUSA {

	// timer used for all time measurements
	private static SimpleTimer timer;
	public static final String NAME = "jacusa";	
	public static final String JAR = NAME + ".jar";
	public static final String VERSION = "2.0.0-BETA6";

	// command line interface
	private CLI cli;
	private Logger logger;
	
	/**
	 * 
	 */
	public JACUSA() {
		cli = CLI.getSingleton();

		// container for available methods (e.g.: call, pileup)
		Map<String, AbstractMethodFactory<?>> methodFactories = 
				new TreeMap<String, AbstractMethodFactory<?>>();

		List<AbstractMethodFactory<?>> factories = new ArrayList<AbstractMethodFactory<?>>(10);
	
		// calling variants
		factories.add(new OneConditionCallFactory());
		factories.add(new TwoConditionCallFactory());
		factories.add(new CallFactory(2)); // TODO make it general
		// pileup information
		factories.add(new nConditionPileupFactory(0));
		// Read info
		factories.add(new RTArrestFactory());

		for (AbstractMethodFactory<?> factory : factories) {
			methodFactories.put(factory.getName(), factory);
		}

		// add to cli 
		cli.setMethodFactories(methodFactories);
	}

	/**
	 * Singleton Pattern
	 * @return a SimpleTimer instance
	 */
	public static SimpleTimer getSimpleTimer() {
		if (timer == null) {
			timer = new SimpleTimer();
		}

		return timer;
	}

	/**
	 * 
	 * @return
	 */
	public CLI getCLI() {
		return cli;
	}
	
	/**
	 * 
	 * @param comparisons
	 */
	/*
	private void printEpilog(int comparisons) {
		// print statistics to STDERR
		printLog("Screening done using " + cli.getMethodFactory().getParameters().getMaxThreads() + " thread(s)");

		System.err.println("Results can be found in: " + cli.getMethodFactory().getParameters().getOutput().getInfo());

		String lineSep = "--------------------------------------------------------------------------------";

		System.err.println(lineSep);
		System.err.println("Analyzed Parallel Pileups:\t" + comparisons);
		System.err.println("Elapsed time:\t\t\t" + getSimpleTimer().getTotalTimestring());
	}
	*/

	/**
	 * Application logic.
	 * 
	 * @param args
	 * @throws Exception 
	 */
	private void run(final String[] args) throws Exception {
		// prolog
		printProlog(args);
		
		CLI cmd = getCLI();

		// parse CLI
		if (! cmd.processArgs(args)) {
			System.exit(1);
		}
		
		// instantiate chosen method
		AbstractMethodFactory<?> methodFactory = cmd.getMethodFactory();
		AbstractParameters<?> parameters = methodFactory.getParameters();
	
		// process coordinate provider
		CoordinateProvider coordinateProvider = null;
		if (parameters.getBedPathname().isEmpty()) {
			methodFactory.initCoordinateProvider();
			coordinateProvider = methodFactory.getCoordinateProvider();
		} else {
			coordinateProvider = new BedCoordinateProvider(parameters.getBedPathname());
		}
	
		String[][] pathnames = new String[parameters.getConditionParameters().size()][]; 
		for (int conditionIndex = 0; conditionIndex < parameters.getConditions(); conditionIndex++) {
			pathnames[conditionIndex] = parameters.getConditionParameters(conditionIndex).getPathnames();
		}
	
		// wrap chosen coordinate provider 
		if (parameters.getMaxThreads() > 1) {
			coordinateProvider = new ThreadedCoordinateProvider(coordinateProvider, 
					pathnames, parameters.getThreadReservedWindowSize());
		}
	
		// main
		AbstractWorkerDispatcher<?> workerDispatcher = methodFactory.getInstance(coordinateProvider);
		int comparisons = workerDispatcher.run();
		
		// epilog
		printEpilog(comparisons);
	
		// cleaup
		parameters.getOutput().close();
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		JACUSA jacusa = new JACUSA();
		jacusa.run(args);
	}
		
	
}
