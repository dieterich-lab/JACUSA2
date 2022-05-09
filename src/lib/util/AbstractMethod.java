package lib.util;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.AbstractACOption;
import lib.cli.options.SAMPathnameArg;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataContainer.AbstractDataContainerBuilderFactory;
import lib.data.assembler.factory.AbstractDataAssemblerFactory;
import lib.data.validator.paralleldata.ParallelDataValidator;
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

/**
 * TODO
 */
public abstract class AbstractMethod<T extends AbstractDataContainerBuilderFactory> {

	private final String name;

	private final GeneralParameter parameter;
	private final AbstractDataAssemblerFactory<T> dataAssemblerFactory;

	private final List<AbstractACOption> acOptions;

	private CoordinateProvider coordinateProvider;
	private WorkerDispatcher workerDispatcher;

	protected AbstractMethod(final String name, final GeneralParameter parameter,
			final AbstractDataAssemblerFactory<T> dataAssemblerFactory) {

		this.name = name;
		this.parameter = parameter;
		this.dataAssemblerFactory = dataAssemblerFactory;

		acOptions = new ArrayList<>(10);
	}

	public String getName() {
		return name;
	}

	public GeneralParameter getParameter() {
		return parameter;
	}

	public final WorkerDispatcher getWorkerDispatcherInstance() {
		if (workerDispatcher == null) {
			workerDispatcher = new WorkerDispatcher(this);
		}

		return workerDispatcher;
	}

	public abstract AbstractWorker<T> createWorker(final int threadId);

	public AbstractDataAssemblerFactory<T> getDataAssemblerFactory() {
		return dataAssemblerFactory;
	}

	public void initACOptions() {
		getACOptions().clear();

		initGlobalACOptions();
		initConditionACOptions();
	}

	protected abstract void initConditionACOptions();

	protected abstract void initGlobalACOptions();

	// check state after parameters have been set
	public final boolean checkState() {
		if (getParameter().getActiveWindowSize() >= getParameter().getReservedWindowSize()) {
			AbstractTool.getLogger().addError("THREAD-WINDOW-SIZE must be << WINDOW-SIZE");
			return false;
		}

		return true;
	}

	protected void addACOption(AbstractACOption newACOption) {
		checkDuplicate(newACOption);
		acOptions.add(newACOption);
	}

	private void checkDuplicate(final AbstractACOption newACOption) {
		for (final AbstractACOption ACOption : acOptions) {
			try {
				if (ACOption.getOpt() != null && ACOption.getOpt().equals(newACOption.getOpt())) {
					throw new IllegalArgumentException("Duplicate opt '" + newACOption.getOpt() + "' for object: "
							+ newACOption.toString() + " and " + ACOption.toString());
				}
				if (ACOption.getOpt() != null && ACOption.getLongOpt().equals(newACOption.getLongOpt())) {
					throw new IllegalArgumentException("Duplicate longOpt '" + newACOption.getLongOpt() + "' for object"
							+ newACOption.toString() + " and " + ACOption.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public List<AbstractACOption> getACOptions() {
		return acOptions;
	}

	/**
	 * TODO
	 * 
	 * @param options
	 */
	public void printUsage(final boolean printExtendedHelp) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(200);

		formatter.printHelp(AbstractTool.getLogger().getTool().getName() + " " + name + " " + getFiles(),
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
		final List<AbstractACOption> tmpAcOptions = getACOptions();
		final Options options = new Options();
		for (final AbstractACOption acOption : tmpAcOptions) {
			if (!acOption.isHidden()) {
				options.addOption(acOption.getOption(printExtendedHelp));
			}
		}
		return options;
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	public void initCoordinateProvider() throws Exception {
		final int conditionSize = parameter.getConditionsSize();
		final String[][] recordFilenames = new String[conditionSize][];

		for (int condI = 0; condI < conditionSize; condI++) {
			recordFilenames[condI] = parameter.getConditionParameter(condI).getRecordFilenames();
		}

		boolean isStranded = false;
		for (final ConditionParameter conditionParameter : parameter.getConditionParameters()) {
			if (conditionParameter.getLibraryType() != LibraryType.UNSTRANDED) {
				isStranded = true;
				break;
			}
		}

		final List<SAMSequenceRecord> sequenceRecords = getSAMSequenceRecords(recordFilenames);
		if (parameter.getInputBedFilename().isEmpty()) {
			coordinateProvider = new SAMCoordinateAdvancedProvider(isStranded, sequenceRecords, parameter);
		} else {
			coordinateProvider = new BedCoordinateProvider(parameter.getInputBedFilename(), isStranded);
			// wrap chosen provider
			if (parameter.getMaxThreads() > 1) {
				coordinateProvider = new WindowedCoordinateStaticProvider(isStranded, coordinateProvider,
						parameter.getReservedWindowSize());
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean parseArgs(final String[] args) throws Exception {
		for (int condI = 0; condI < args.length; condI++) {
			SAMPathnameArg pa = new SAMPathnameArg(condI + 1, parameter.getConditionParameter(condI));
			pa.processArg(args[condI]);
		}

		return true;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public CoordinateProvider getCoordinateProvider() {
		return coordinateProvider;
	}

	public abstract List<ParallelDataValidator> createParallelDataValidators();

	/**
	 * TODO
	 * 
	 * @param recordFilenames
	 * @return
	 * @throws Exception
	 */
	protected List<SAMSequenceRecord> getSAMSequenceRecords(final String[][] recordFilenames) throws Exception {
		AbstractTool.getLogger().addInfo("Computing overlap between sequence records.");
		final String error = "Sequence Dictionaries of BAM files do not match";

		String lastRecordFilename = null;
		SAMSequenceDictionary lastSequenceDictionary = null;
		for (String[] outer : recordFilenames) {
			for (String recordFilename : outer) {
				final SAMSequenceDictionary sequenceDictionary = Util.getSAMSequenceDictionary(recordFilename);
				if (lastSequenceDictionary == null) {
					lastSequenceDictionary = sequenceDictionary;
					lastRecordFilename = recordFilename;
				}
				if (!lastSequenceDictionary.isSameDictionary(sequenceDictionary)) {
					throw new SAMException(error + " " + lastRecordFilename + " and " + recordFilename);
				}
			}
		}

		return lastSequenceDictionary.getSequences();
	}

	public void debug() {
	}

	/*
	 * Builder
	 */

	public abstract static class AbstractMethodFactory<T extends AbstractDataContainerBuilderFactory> {

		private final String name;
		private final String desc;
		private final int conditions;

		public AbstractMethodFactory(final String name, final String desc, final int conditions) {
			this.name = name;
			this.desc = desc;
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

		public abstract AbstractMethod<T> createMethod();

		public abstract AbstractMethodFactory<T> createFactory(final int conditions);

	}

}
