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

import jacusa.cli.parameters.LinkageRTArrestParameter;
import jacusa.cli.parameters.PileupParameter;
import jacusa.cli.parameters.RTArrestParameter;
import jacusa.method.call.OneConditionCallFactory;
import jacusa.method.call.TwoConditionCallFactory;
import jacusa.method.pileup.PileupFactory;
import jacusa.method.rtarrest.LinkageRTArrestFactory;
import jacusa.method.rtarrest.RTArrestFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lib.data.RTarrestData;
import lib.data.LRTarrestData;
import lib.data.generator.LRTarrestDataGenerator;
import lib.data.generator.DataGenerator;
import lib.data.generator.RTarrestDataGenerator;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

/**
 * @author Michael Piechotta
 */
public class JACUSA extends AbstractTool {
	
	public JACUSA(final String args[]) {
		super("JACUSA", "2.0.0-DEVEL", args);
	}

	@Override
	protected Map<String, AbstractMethodFactory<?, ?>> getMethodFactories() {
		// container for available methods (e.g.: call, pileup)
		final Map<String, AbstractMethodFactory<?, ?>> methodFactories = 
				new LinkedHashMap<String, AbstractMethodFactory<?, ?>>();

		final List<AbstractMethodFactory<?, ?>> factories = new ArrayList<AbstractMethodFactory<?, ?>>(10);
		// calling variants
		factories.add(new OneConditionCallFactory());
		factories.add(new TwoConditionCallFactory());

		factories.add(new PileupFactory(new PileupParameter(1)));

		// Read info
		DataGenerator<RTarrestData> baseCallReadInfoGenerator = new RTarrestDataGenerator();
		factories.add(new RTArrestFactory<RTarrestData>(new RTArrestParameter<RTarrestData>(2), 
				baseCallReadInfoGenerator));

		DataGenerator<LRTarrestData> baseCallReadInfoExtendedGenerator = new LRTarrestDataGenerator();
		factories.add(new LinkageRTArrestFactory<LRTarrestData>(new LinkageRTArrestParameter<LRTarrestData>(2), 
				baseCallReadInfoExtendedGenerator));
		
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
		
		sb.append("Results can be found in: " + getCLI().getMethodFactory().getParameter().getResultWriter().getInfo());
		sb.append('\n');
		
		final String lineSep = "--------------------------------------------------------------------------------";

		sb.append(lineSep);
		sb.append('\n');
		sb.append("Analyzed sites:\t" + getComparisons());
		sb.append('\n');
		sb.append("Elapsed time:\t" + getLogger().getTimer().getTotalTimestring());

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
