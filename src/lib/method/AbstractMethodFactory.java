package lib.method;

import jacusa.data.validator.ParallelDataValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lib.cli.options.AbstractACOption;
import lib.cli.options.SAMPathnameArg;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.util.AbstractTool;
import lib.util.coordinateprovider.BedCoordinateProvider;
import lib.util.coordinateprovider.CoordinateProvider;
import lib.util.coordinateprovider.SAMCoordinateProvider;
import lib.util.coordinateprovider.WindowedCoordinateProvider;
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

public abstract class AbstractMethodFactory<T extends AbstractData> 
implements DataGenerator<T> {

	private final String name;
	private final String desc;
	private final DataGenerator<T> dataGenerator;

	private AbstractParameter<T> parameters;

	private final Set<AbstractACOption> ACOptions;

	private CoordinateProvider coordinateProvider;
	private WorkerDispatcher<T> workerDispatcher;
	
	public AbstractMethodFactory(final String name, final String desc, 
			final AbstractParameter<T> parameters, final DataGenerator<T> dataGenerator) {
		this.name = name;
		this.desc = desc;
		this.dataGenerator = dataGenerator;

		setParameters(parameters);
		ACOptions 		= new HashSet<AbstractACOption>(10);
	}
	
	// needed for Methods where the number of conditions is unknown... 
	public void initGeneralParameter(final int conditions) { }
	
	protected void setParameters(final AbstractParameter<T> parameters) {
		parameters.setMethodFactory(this);
		this.parameters = parameters;
		
	}
	
	public AbstractParameter<T> getParameter() {
		return parameters;
	}

	public final WorkerDispatcher<T> getWorkerDispatcher() {
		if (workerDispatcher == null) {
			workerDispatcher = new WorkerDispatcher<T>(this);
		}
		
		return workerDispatcher;
	}

	public abstract AbstractWorker<T> createWorker(final int threadId);

	public abstract void initACOptions();
	protected abstract void initConditionACOptions();
	protected abstract void initGlobalACOptions();
	
	// check state after parameters have been set
	public abstract boolean checkState();

	public DataGenerator<T> getDataGenerator() {
		return dataGenerator;
	}
	
	@Override
	public T createData() {
		return getDataGenerator().createData();
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
			ACOptions.add(newACOption);
		}
	}
	
	private boolean checkDuplicate(final AbstractACOption newACOption) {
		for (final AbstractACOption ACOption : ACOptions) {
			try {
				if (ACOption.getOpt() != null && 
						ACOption.getOpt().equals(newACOption.getOpt())) {
					throw new Exception("Duplicate opt '" + newACOption.getOpt() + 
							"' for object: " + newACOption.toString());
				}
				if (ACOption.getOpt() != null && 
						ACOption.getLongOpt().equals(newACOption.getLongOpt())) {
					throw new Exception("Duplicate longOpt '" + newACOption.getLongOpt() + 
							"' for object" + newACOption.toString());
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
	public Set<AbstractACOption> getACOptions() {
		return ACOptions;
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
		final Set<AbstractACOption> acOptions = getACOptions();
		final Options options = new Options();
		for (AbstractACOption acoption : acOptions) {
			options.addOption(acoption.getOption());
		}
		return options;
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void initCoordinateProvider() throws Exception {
		final int conditionSize = parameters.getConditionsSize();
		final String[][] recordFilenames = new String[conditionSize][];

		for (int conditionIndex = 0; conditionIndex < conditionSize; conditionIndex++) {
			recordFilenames[conditionIndex] = parameters.getConditionParameter(conditionIndex).getRecordFilenames();
		}
		
		final List<SAMSequenceRecord> sequenceRecords = getSAMSequenceRecords(recordFilenames);
		if (parameters.getInputBedFilename().isEmpty()) {
			coordinateProvider = new SAMCoordinateProvider(sequenceRecords);
		} else {
			coordinateProvider = new BedCoordinateProvider(parameters.getInputBedFilename());
		}
	
		// wrap chosen coordinate provider 
		if (parameters.getMaxThreads() > 1) {
			coordinateProvider = new WindowedCoordinateProvider(
					coordinateProvider, parameters.getReservedWindowSize());
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
			SAMPathnameArg pa = new SAMPathnameArg(conditionIndex + 1, parameters.getConditionParameter(conditionIndex));
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
	
}
