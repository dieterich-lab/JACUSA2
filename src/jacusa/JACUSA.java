package jacusa;

import jacusa.method.call.CallMethod;
import jacusa.method.pileup.PileupMethod;
import jacusa.method.rtarrest.RTarrestMethod;

import java.util.Arrays;

import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.BaseCallCountFactory;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.data.count.basecallquality.BaseCallQualityCountFactory;
import lib.data.count.basecallquality.MapBaseCallQualityCount;
import lib.util.AbstractTool;

/*
 * JACUSA2 TODO add text
 * Copyright (C) 2018  Michael Piechotta
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
 * @version 2.x 
 */
public class JACUSA extends AbstractTool {

	public static final BaseCallCountFactory<? extends BaseCallCount> bccFactory = 
			new DefaultBaseCallCount.Factory();
	public static final BaseCallQualityCountFactory<? extends BaseCallQualityCount> bcqcFactory = 
			new MapBaseCallQualityCount.Factory();
		
	public JACUSA(final String args[]) {
		super(
				"JACUSA", VersionInfo.get(), 
				args,
				Arrays.asList(
						// calling variants
						new CallMethod.Factory(1),
						new CallMethod.Factory(2),
						// pileup
						new PileupMethod.Factory(1),
						// reverse transcription read arrest
						new RTarrestMethod.Factory()) );
						// linked reverse transcription read arrest
						// new LRTarrestMethod.Factory()) ); 
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
		sb.append(getCLI().getMethodFactory().getParameter().getResultFilename());
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
	
	/**
	 * Main method for JACUSA.
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
