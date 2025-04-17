package lib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.SAMPathnameArgument;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.assembler.factory.AbstractDataAssemblerFactory;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.estimate.MinkaParameter;
import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.DeletionStat;
import lib.stat.INDELstat;
import lib.stat.InsertionStat;
import lib.stat.estimation.provider.DeletionEstimateProvider;
import lib.stat.estimation.provider.InsertionEstimateProvider;
import lib.util.coordinate.provider.BedCoordinateProvider;
import lib.util.coordinate.provider.CoordinateProvider;
import lib.util.coordinate.provider.SAMCoordinateAdvancedProvider;
import lib.util.coordinate.provider.WindowedCoordinateStaticProvider;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.FilterFactory;

/**
 * Base class of all methods available in JACUSA2
 */
public abstract class AbstractMethod {
	
	private final String name;
	
	private final AbstractDataAssemblerFactory dataAssemblerFactory;
	
	private final List<AbstractProcessingOption> options;
	
	private final Map<Character, FilterFactory> filterFactories = new HashMap<Character, FilterFactory>();
	private final Map<String, AbstractStatFactory> statisticFactories = new HashMap<String, AbstractStatFactory>();
	private final Map<Character, ResultFormat> resultFormats = new HashMap<Character, ResultFormat>();
	
	private CoordinateProvider coordinateProvider;
	private WorkerDispatcher workerDispatcher;
	
	protected AbstractMethod(
			final String name, 
			final AbstractDataAssemblerFactory dataAssemblerFactory) {
		
		this.name 					= name;
		this.dataAssemblerFactory 	= dataAssemblerFactory;
		
		options = new ArrayList<>(10);
	}
	
	public String getName() {
		return name;
	}
	
	public abstract GeneralParameter getParameter();
	
	public final WorkerDispatcher getWorkerDispatcherInstance() {
		if (workerDispatcher == null) {
			workerDispatcher = new WorkerDispatcher(this);
		}
		
		return workerDispatcher;
	}
	
	public abstract AbstractWorker createWorker(final int threadId);
	
	public AbstractDataAssemblerFactory getDataAssemblerFactory() {
		return dataAssemblerFactory;
	}
	
	public void registerOptions() {
		registerGlobalOptions();
		registerConditionOptions();
	}
	
	protected abstract void registerConditionOptions();
	protected abstract void registerGlobalOptions();
	
	// check state after parameters have been set
	public boolean checkState() {
		if (getParameter().getActiveWindowSize() >= getParameter().getReservedWindowSize()) {
			AbstractTool.getLogger().addError("THREAD-WINDOW-SIZE must be << WINDOW-SIZE");
			return false;
		}
		
		// TODO implement more checks, such as dependency between options
		/* 
		public boolean checkState() {
			// TODO put this somewhere else
			if((line.contains("insertion_ratio") && !cmd.hasOption('i')) || (line.contains("deletion_ratio") && !cmd.hasOption('D')) || (line.contains("modification_count") && !cmd.hasOption('M'))){
				throw new IllegalArgumentException("put options -i, -D, or -M to calculate insertion-, deletion-ratio, or modification-count");
			}

			/* FIXME enforce modification_count triggers reading mods
			if(s.contains("modification_count")){
				modificationOutputRequest = true;
			}
		}
		*/
		
		return true;
	}
	
	public void registerOption(AbstractProcessingOption newOption) {
		checkDuplicate(newOption);
		options.add(newOption);
	}
	
	public void registerFilterFactory(final AbstractFilterFactory filterFactory) {
		filterFactories.put(filterFactory.getID(), filterFactory);
	}
	
	public abstract void registerFilterFactories();
	
	public Map<Character, FilterFactory> getFilterFactories() {
		return Collections.unmodifiableMap(filterFactories);
	}
	
	public void registerStatisticFactory(final AbstractStatFactory statisticFactory) {
		statisticFactories.put(statisticFactory.getName(), statisticFactory);
	}

	public abstract void registerStatisticFactories();
	
	public Map<String, AbstractStatFactory> getStatisticFactories() {
		return Collections.unmodifiableMap(statisticFactories);
	}
	
	public void registerResultFormat(final ResultFormat resultFormat) {
		resultFormats.put(resultFormat.getID(), resultFormat);
	}
	
	public abstract void registerResultFormats();
	
	public Map<Character, ResultFormat> getResultFormats() {
		return Collections.unmodifiableMap(resultFormats);
	}
	
	private void checkDuplicate(final AbstractProcessingOption newOption) {
		for (final AbstractProcessingOption option : options) {
			try {
				if (option.getOpt() != null && 
						option.getOpt().equals(newOption.getOpt())) {
					throw new IllegalArgumentException("Duplicate opt '" + newOption.getOpt() + 
							"' for object: " + newOption.toString() + " and " + option.toString());
				}
				if (option.getOpt() != null && 
						option.getLongOpt().equals(newOption.getLongOpt())) {
					throw new IllegalArgumentException("Duplicate longOpt '" + newOption.getLongOpt() + 
							"' for object" + newOption.toString() + " and " + option.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public List<AbstractProcessingOption> getOptions() {
		return options;
	}

	public void printUsage(final boolean printExtendedHelp) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(200);

		formatter.printHelp(
				AbstractTool.getLogger().getTool().getName() + 
				" " + 
				name +
				" " +
				getFiles(), 
				getOptions(printExtendedHelp));
	}
	
	protected String getFiles() {
		switch (getParameter().getConditionsSize()) {
		case 1:
			return "[OPTIONS] BAM1_1[,BAM1_2,...]";

		case 2:
			return "[OPTIONS] BAM1_1[,BAM1_2,...] BAM2_1[,BAM2_2,...]";

		default:
			return "[OPTIONS] BAM1_1[,BAM1_2,...] [BAM2_1,...] [BAMn_1,...]";
		}
	}
	
	protected Options getOptions(final boolean printExtendedHelp) {
		final List<AbstractProcessingOption> tmpOptions = getOptions();
		final Options options = new Options();
		for (final AbstractProcessingOption acOption : tmpOptions) {
			if (! acOption.isHidden()) {
				options.addOption(acOption.getOption(printExtendedHelp));
			}
		}
		return options;
	}
	
	/**
	 * Initializes coordinateProvider. Coordinates can come from SAMSequenceDirectory or from a BED-file 
	 * @throws Exception
	 */
	public void initCoordinateProvider() throws Exception {
		final int conditionSize = getParameter().getConditionsSize();
		final String[][] recordFilenames = new String[conditionSize][];

		for (int conditionIndex = 0; conditionIndex < conditionSize; conditionIndex++) {
			recordFilenames[conditionIndex] = getParameter().getConditionParameter(conditionIndex).getRecordFilenames();
		}
		
		boolean isStranded = false;
		for (final ConditionParameter conditionParameter : getParameter().getConditionParameters()) {
			if (conditionParameter.getLibraryType() != LibraryType.UNSTRANDED) {
				isStranded = true;
				break;
			}
		}
		
		final List<SAMSequenceRecord> sequenceRecords = getSAMSequenceRecords(recordFilenames);
		if (getParameter().getInputBedFilename().isEmpty()) {
			coordinateProvider = new SAMCoordinateAdvancedProvider(isStranded, sequenceRecords, getParameter());
		} else {
			coordinateProvider = new BedCoordinateProvider(getParameter().getInputBedFilename(), isStranded);
			// wrap chosen provider
			if (getParameter().getMaxThreads() > 1) {
				coordinateProvider = new WindowedCoordinateStaticProvider(isStranded,
						coordinateProvider, getParameter().getReservedWindowSize());
			}
		}
	}
	
	/**
	 * Parses args that correspond to paths to BAM filenames. Expected format: BAM11,...BAM1N_1 BAM12,...BAM2N_2.
	 * 
	 * @param args array of ","-separated strings that point to BAM filenames
	 * @throws Exception
	 */
	public void parseArgs(final String[] args) throws Exception {
		for (int conditionIndex = 0; conditionIndex < args.length; conditionIndex++) {
			SAMPathnameArgument pathnameArgument = new SAMPathnameArgument(conditionIndex, getParameter().getConditionParameter(conditionIndex));
			pathnameArgument.processArg(args[conditionIndex]);
		}
	}
	
	public CoordinateProvider getCoordinateProvider() {
		return coordinateProvider;
	}

	public List<INDELstat> getINDELstats(final MinkaParameter minkaParameter) {
		final List<INDELstat> indelStats = new ArrayList<INDELstat>();
		if (getParameter().showINDELcounts()) {
			if (getParameter().showDeletionCount()) {
				indelStats.add(
						new DeletionStat(
								minkaParameter,
								new DeletionEstimateProvider(minkaParameter.getMaxIterations())));
			}
			if (getParameter().showInsertionCount() ||
					getParameter().showInsertionStartCount()) {
				indelStats.add(
						new InsertionStat(
								minkaParameter,
								new InsertionEstimateProvider(minkaParameter.getMaxIterations())));
			}
		}
		return indelStats;
	}
	
	public abstract List<ParallelDataValidator> createParallelDataValidators();
	
	/**
	 * Reads recordFilenames, creates Objects of type SAMSequenceDictionary and checks if all sequence dictionaries are the same.
	 * If not, throws an Exception.
	 * 
	 * @param recordFilenames 2dim array of BAM files; 1st dim: conditions; 2nd dim: filenames
	 * @return a list of Objects of type SAMSequenceDictionary 
	 * @throws Exception
	 */
	protected List<SAMSequenceRecord> getSAMSequenceRecords(final String[][] recordFilenames) throws Exception {
		AbstractTool.getLogger().addInfo("Computing overlap between sequence records.");
		final String error = "Sequence Dictionaries of BAM files do not match";
		
		String lastRecordFilename = null;
		SAMSequenceDictionary lastSequenceDictionary = null;
		for (String[] outer : recordFilenames) {
			for (String recordFilename :  outer) {
				final SAMSequenceDictionary sequenceDictionary = Util.getSAMSequenceDictionary(recordFilename);
				if (lastSequenceDictionary == null) {
					lastSequenceDictionary = sequenceDictionary;
					lastRecordFilename = recordFilename;
				}
				if (! lastSequenceDictionary.isSameDictionary(sequenceDictionary)) {
					throw new SAMException(error + " " + lastRecordFilename + " and " + recordFilename);
				}
			}
		}
		
		return lastSequenceDictionary.getSequences();
	}

	public void debug() {}

	/*
	 * Builder
	 */

	public abstract static class AbstractFactory {

		private final String name;
		private final String desc;
		private final int conditions;

		public AbstractFactory(final String name, final String desc, final int conditions) {
			this.name 		= name;
			this.desc 		= desc;
			this.conditions = conditions;
		}

		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return desc;
		}

		public int getConditions() {
			return conditions;
		}

		public abstract AbstractMethod createMethod();
		public abstract AbstractFactory createFactory(final int conditions);

	}

}
