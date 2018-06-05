package jacusa;

import jacusa.cli.parameters.LRTarrestParameter;
import jacusa.cli.parameters.PileupParameter;
import jacusa.cli.parameters.RTarrestParameter;
import jacusa.method.call.OneConditionCallFactory;
import jacusa.method.call.TwoConditionCallFactory;
import jacusa.method.pileup.PileupFactory;
import jacusa.method.rtarrest.LRTarrestFactory;
import jacusa.method.rtarrest.RTArrestFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

/*
    JAVA framework for accurate SNV assessment (JACUSA) is a one-stop solution 
    to detect single nucleotide variants (SNVs) from comparing matched sequencing samples.
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


/**
 * JAVA framework for accurate SNV assessment (JACUSA) is a one-stop solution 
 * to detect single nucleotide variants (SNVs) from comparing matched sequencing samples.
 * Copyright (C) 2015  Michael Piechotta
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Michael Piechotta
 * @version 2.0.0 
 */
public class JACUSA extends AbstractTool {
	
	public JACUSA(final String args[]) {
		super("JACUSA", "2.0.0-BETA-17", args);
	}

	@Override
	protected Map<String, AbstractMethodFactory<?, ?>> getMethodFactories() {
		// container for available methods (e.g.: call, pileup)
		final Map<String, AbstractMethodFactory<?, ?>> methodFactories = 
				new LinkedHashMap<String, AbstractMethodFactory<?, ?>>();

		// populate container
		for (final AbstractMethodFactory<?, ?> factory : getMethodFactoriesList()) {
			methodFactories.put(factory.getName(), factory);
		}

		return methodFactories;
	}

	/**
	 * Helper - gives the list of available methods
	 * @return list of available methods
	 */
	private List<AbstractMethodFactory<?, ?>> getMethodFactoriesList() {
		// list container for available methods
		final List<AbstractMethodFactory<?, ?>> factories = 
				new ArrayList<AbstractMethodFactory<?, ?>>(10);

		// calling variants
		factories.add(new OneConditionCallFactory());
		factories.add(new TwoConditionCallFactory());

		// pileup
		factories.add(new PileupFactory(new PileupParameter(1)));

		// reverse transcription read arrest
		factories.add(new RTArrestFactory(new RTarrestParameter(2)));

		// linked reverse transcription read arrest
		factories.add(new LRTarrestFactory(new LRTarrestParameter(2)));

		return factories; 
	}

	@Override
	protected String getEpilog() {
		final StringBuilder sb = new StringBuilder();
		
		// number of threads
		final int maxThreads = getCLI().getMethodFactory().getParameter().getMaxThreads();
		sb.append("Screening done using ");
		sb.append(maxThreads);
		sb.append(" thread(s)");
		sb.append('\n');
		
		// location of result
		sb.append("Results can be found in: ");
		sb.append(getCLI().getMethodFactory().getParameter().getResultWriter().getInfo());
		sb.append('\n');
		
		final String lineSep = "--------------------------------------------------------------------------------";

		// # of result and total elapsd time
		sb.append(lineSep);
		sb.append('\n');
		sb.append("Analyzed sites:\t");
		sb.append(getComparisons());
		sb.append('\n');
		sb.append("Elapsed time:\t");
		sb.append(getLogger().getTimer().getTotalTimestring());

		return sb.toString();
	}

	@Override
	protected Class<?> getMainClass() {
		return this.getClass();
	}
	
	/**
	 * Main method for JACUSA2.
	 * 
	 * @param args command line arguments
	 */
	public static void main(final String[] args) {
		final JACUSA jacusa = new JACUSA(args);
		try {
			jacusa.run();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
