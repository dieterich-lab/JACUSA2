package lib.method;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.cli.options.AbstractACOption;
import lib.cli.options.SAMPathnameArg;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.factory.AbstractDataBuilderFactory;
import lib.data.generator.DataGenerator;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.data.result.Result;
import lib.data.validator.ParallelDataValidator;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.provider.BedCoordinateProvider;
import lib.util.coordinate.provider.CoordinateProvider;
import lib.util.coordinate.provider.SAMCoordinateProvider;
import lib.util.coordinate.provider.WindowedCoordinateProvider;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.SamReaderFactory.Option;

public abstract class AbstractMethodFactory<T extends AbstractData, R extends Result<T>> 
implements DataGenerator<T> {

	private final String name;
	private final String desc;
	private final AbstractDataBuilderFactory<T> dataBuilderFactory;
	private final DataGenerator<T> dataGenerator;

	private AbstractParameter<T, R> parameter;

	private final List<AbstractACOption> acOptions;

	private CoordinateProvider coordinateProvider;
	private WorkerDispatcher<T, R> workerDispatcher;
	
	public AbstractMethodFactory(final String name, final String desc, 
			final AbstractParameter<T, R> parameters,
			final AbstractDataBuilderFactory<T> dataBuilderFactory,
			final DataGenerator<T> dataGenerator) {

		this.name = name;
		this.desc = desc;
		this.dataBuilderFactory = dataBuilderFactory;
		this.dataGenerator = dataGenerator;

		setParameter(parameters);
		acOptions = new ArrayList<AbstractACOption>(10);
	}
	
	// needed for Methods where the number of conditions is unknown... 
	public void initGeneralParameter(final int conditions) { }
	
	protected void setParameter(final AbstractParameter<T, R> parameter) {
		parameter.setMethodFactory(this);
		this.parameter = parameter;
		
	}
	
	public AbstractParameter<T, R> getParameter() {
		return parameter;
	}

	public final WorkerDispatcher<T, R> getWorkerDispatcher() {
		if (workerDispatcher == null) {
			workerDispatcher = new WorkerDispatcher<T, R>(this);
		}
		
		return workerDispatcher;
	}

	public abstract AbstractWorker<T, R> createWorker(final int threadId);

	public void initACOptions() {
		getACOptions().clear();
		
		initGlobalACOptions();
		initConditionACOptions();
	}
	
	protected abstract void initConditionACOptions();
	protected abstract void initGlobalACOptions();
	
	// check state after parameters have been set
	final public boolean checkState() {
		if (getParameter().getActiveWindowSize() >= getParameter().getReservedWindowSize()) {
			AbstractTool.getLogger().addError("THREAD-WINDOW-SIZE must be << WINDOW-SIZE");
			return false;
		}
		
		return true;
	}

	public DataGenerator<T> getDataGenerator() {
		return dataGenerator;
	}
	
	@Override
	public T createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return getDataGenerator().createData(libraryType, coordinate);
	}

	@Override
	public T[] createReplicateData(final int n) {
		return dataGenerator.createReplicateData(n);
	}

	@Override
	public T[][] createContainerData(final int n) {
		return dataGenerator.createContainerData(n);
	}

	@Override
	public T copyData(final T data) {
		return dataGenerator.copyData(data);
	}
	
	@Override
	public T[] copyReplicateData(final T[] replicateData) {
		return dataGenerator.copyReplicateData(replicateData);
	}
	
	@Override
	public T[][] copyContainerData(final T[][] containerData) {
		return dataGenerator.copyContainerData(containerData);
	}
	
	protected void addACOption(AbstractACOption newACOption) {
		if (checkDuplicate(newACOption)) {
			acOptions.add(newACOption);
		}
	}
	
	private boolean checkDuplicate(final AbstractACOption newACOption) {
		for (final AbstractACOption ACOption : acOptions) {
			try {
				if (ACOption.getOpt() != null && 
						ACOption.getOpt().equals(newACOption.getOpt())) {
					throw new Exception("Duplicate opt '" + newACOption.getOpt() + 
							"' for object: " + newACOption.toString() + " and " + ACOption.toString());
				}
				if (ACOption.getOpt() != null && 
						ACOption.getLongOpt().equals(newACOption.getLongOpt())) {
					throw new Exception("Duplicate longOpt '" + newACOption.getLongOpt() + 
							"' for object" + newACOption.toString() + " and " + ACOption.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		return true;
	}

	/**
	 * 
	 * @return
	 */
	public List<AbstractACOption> getACOptions() {
		return acOptions;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return desc;
	}
	
	/**
	 * 
	 * @param options
	 */
	public void printUsage() {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(160);
		
		formatter.printHelp(
				AbstractTool.getLogger().getTool().getName() + 
				" " + 
				getName() +
				" " +
				getFiles(), 
				getOptions());
	}
	
	protected String getFiles() {
		switch (getParameter().getConditionsSize()) {
		case 1:
			return "[OPTIONS] BAM1_1[,BAM1_2,BAM1_3,...]";

		case 2:
			return "[OPTIONS] BAM1_1[,BAM1_2,BAM1_3,...] BAM2_1[,BAM2_2,BAM2_3,...]";

		default:
			return "[OPTIONS] BAM1_1[,BAM1_2,BAM1_3,...] BAM2_1[,BAM2_2,BAM2_3,...] ...";
		}
	}
	
	protected Options getOptions() {
		final List<AbstractACOption> acOptions = getACOptions();
		final Options options = new Options();
		for (final AbstractACOption acOption : acOptions) {
			if (! acOption.isHidden()) {
				options.addOption(acOption.getOption());
			}
		}
		return options;
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void initCoordinateProvider() throws Exception {
		final int conditionSize = parameter.getConditionsSize();
		final String[][] recordFilenames = new String[conditionSize][];

		for (int conditionIndex = 0; conditionIndex < conditionSize; conditionIndex++) {
			recordFilenames[conditionIndex] = parameter.getConditionParameter(conditionIndex).getRecordFilenames();
		}
		
		boolean isStranded = false;
		for (final AbstractConditionParameter<T> conditionParameter : parameter.getConditionParameters()) {
			if (conditionParameter.getLibraryType() != LIBRARY_TYPE.UNSTRANDED) {
				isStranded = true;
				break;
			}
		}
		
		final List<SAMSequenceRecord> sequenceRecords = getSAMSequenceRecords(recordFilenames);
		if (parameter.getInputBedFilename().isEmpty()) {
			coordinateProvider = new SAMCoordinateProvider(isStranded, sequenceRecords);
		} else {
			// FIXME what if bed is stranded
			coordinateProvider = new BedCoordinateProvider(isStranded, parameter.getInputBedFilename());
		}

		// wrap chosen coordinate provider 
		if (parameter.getMaxThreads() > 1) {
			coordinateProvider = new WindowedCoordinateProvider(isStranded,
					coordinateProvider, parameter.getReservedWindowSize());
		}
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean parseArgs(final String[] args) throws Exception {
		for (int conditionIndex = 0; conditionIndex < args.length; conditionIndex++) {
			SAMPathnameArg pa = new SAMPathnameArg(conditionIndex + 1, parameter.getConditionParameter(conditionIndex));
			pa.processArg(args[conditionIndex]);
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param recordFilename
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	protected SAMSequenceDictionary getSAMSequenceDictionary(final String recordFilename) throws IOException {
		final File file = new File(recordFilename);
		final SamReader reader = SamReaderFactory
				.make()
				.setOption(Option.CACHE_FILE_BASED_INDEXES, true)
				.setOption(Option.DONT_MEMORY_MAP_INDEX, false) // disable memory mapping
				.validationStringency(ValidationStringency.LENIENT)
				.open(file);

		final SAMSequenceDictionary sequenceDictionary = reader.getFileHeader().getSequenceDictionary();
		reader.close();

		return sequenceDictionary;
	}
	
	/**
	 * 
	 * @return
	 */
	public CoordinateProvider getCoordinateProvider() {
		return coordinateProvider;
	}

	public List<ParallelDataValidator<T>> getParallelDataValidators() {
		final List<ParallelDataValidator<T>> parallelDataValidators = new ArrayList<ParallelDataValidator<T>>(5);
		return parallelDataValidators;
	}
	
	/**
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
			for (String recordFilename :  outer) {
				final SAMSequenceDictionary sequenceDictionary = getSAMSequenceDictionary(recordFilename);
				if (lastSequenceDictionary == null) {
					lastSequenceDictionary = sequenceDictionary;
					lastRecordFilename = recordFilename;
				} else if (! lastSequenceDictionary.isSameDictionary(sequenceDictionary)) {
					throw new SAMException(error + " " + lastRecordFilename + " and " + recordFilename);
				} else {
					lastSequenceDictionary = sequenceDictionary;
					lastRecordFilename = recordFilename;
				}
			}
		}
		
		return lastSequenceDictionary.getSequences();
	}

	public void debug() {};
	
	public AbstractDataBuilderFactory<T> getDataBuilderFactory() {
		return dataBuilderFactory;
	}
	
}
