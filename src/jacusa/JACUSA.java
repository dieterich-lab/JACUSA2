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

import jacusa.cli.parameters.PileupParameter;
import jacusa.cli.parameters.RTArrestParameter;
import jacusa.method.call.OneConditionCallFactory;
import jacusa.method.call.TwoConditionCallFactory;
import jacusa.method.pileup.PileupFactory;
import jacusa.method.rtarrest.RTArrestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lib.data.BaseCallReadInfoData;
import lib.data.PileupData;
import lib.data.generator.DataGenerator;
import lib.data.generator.PileupDataGenerator;
import lib.data.generator.BaseCallReadInfoDataGenerator;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

/**
 * @author Michael Piechotta
 */
public class JACUSA extends AbstractTool {

	private int comparisons;
	
	public JACUSA(final String args[]) {
		super("JACUSA", "2.0.0-BETA7", args);
		comparisons = 0;
	}

	@Override
	protected Map<String, AbstractMethodFactory<?, ?>> getMethodFactories() {
		// container for available methods (e.g.: call, pileup)
		final Map<String, AbstractMethodFactory<?, ?>> methodFactories = 
				new TreeMap<String, AbstractMethodFactory<?, ?>>();

		final List<AbstractMethodFactory<?, ?>> factories = new ArrayList<AbstractMethodFactory<?, ?>>(10);
		// calling variants
		DataGenerator<PileupData> pileupDataGenerator = new PileupDataGenerator();
		factories.add(new OneConditionCallFactory<PileupData>(pileupDataGenerator));
		factories.add(new TwoConditionCallFactory<PileupData>(pileupDataGenerator));
		// factories.add(new CallFactory<PileupData>(new CallParameter<T>(3), dataGenerator)); // TODO make conditions general
		// pileup information

		factories.add(new PileupFactory<PileupData>(new PileupParameter<PileupData>(1), pileupDataGenerator));

		// Read info
		DataGenerator<BaseCallReadInfoData> baseCallReadInfoGenerator = new BaseCallReadInfoDataGenerator();
		factories.add(new RTArrestFactory<BaseCallReadInfoData>(new RTArrestParameter<BaseCallReadInfoData>(2), 
				baseCallReadInfoGenerator));

		for (final AbstractMethodFactory<?, ?> factory : factories) {
			methodFactories.put(factory.getName(), factory);
		}
		return methodFactories;
	}

	@Override
	protected String getEpilog() {
		final StringBuilder sb = new StringBuilder();

		// print statistics to STDERR
		sb.append("Screening done using " + getCLI().getMethodFactory().getParameter().getMaxThreads() + " thread(s)");
		sb.append('\n');
		
		sb.append("Results can be found in: " + getCLI().getMethodFactory().getParameter().getResultWriter());
		sb.append('\n');
		
		final String lineSep = "--------------------------------------------------------------------------------";

		sb.append(lineSep);
		sb.append('\n');
		sb.append("Analyzed Parallel Pileups:\t" + comparisons);
		sb.append('\n');
		sb.append("Elapsed time:\t\t\t" + getLogger().getTimer().getTotalTimestring());

		return sb.toString();
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		JACUSA jacusa = new JACUSA(args);
		try {
			jacusa.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
