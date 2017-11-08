package lib.method;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lib.cli.options.AbstractACOption;
import lib.cli.options.SAMPathnameArg;
import lib.cli.parameters.AbstractParameters;
import lib.data.AbstractData;
import lib.util.AbstractTool;
import lib.util.coordinateprovider.BedCoordinateProvider;
import lib.util.coordinateprovider.CoordinateProvider;
import lib.util.coordinateprovider.SAMCoordinateProvider;
import lib.util.coordinateprovider.WindowedCoordinateProvider;
import lib.worker.AbstractWorker;
import lib.worker.AbstractWorkerDispatcher;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.SamReaderFactory.Option;

public abstract class AbstractMethodFactory<T extends AbstractData> {

	private final String name;
	private final String desc;

	private AbstractParameters<T> parameters;

	private final Set<AbstractACOption> ACOptions;

	private CoordinateProvider coordinateProvider;
	
	public AbstractMethodFactory(final String name, final String desc, 
			final AbstractParameters<T> parameters) {
		this.name = name;
		this.desc = desc;

		setParameters(parameters);
		ACOptions 		= new HashSet<AbstractACOption>(10);
	}
	
	// needed for Methods where the number of conditions is unknown... 
	public void initParameters(final int conditions) { }
	
	protected void setParameters(final AbstractParameters<T> parameters) {
		parameters.setMethodFactory(this);
		this.parameters = parameters;
		
	}
	
	public AbstractParameters<T> getParameters() {
		return parameters;
	}

	public abstract AbstractWorkerDispatcher<T> getWorkerDispatcher();
	public abstract AbstractWorker<T> createWorker();

	public abstract void initACOptions();
	protected abstract void initConditionACOptions();
	protected abstract void initGlobalACOptions();
	
	public boolean check() {
		return true;
	}

	protected void addACOption(AbstractACOption newACOption) {
		if (checkDuplicate(newACOption)) {
			ACOptions.add(newACOption);
		}
	}
	
	private boolean checkDuplicate(final AbstractACOption newACOption) {
		for (final AbstractACOption ACOption : ACOptions) {
			try {
				if (! ACOption.getOpt().isEmpty() && 
						ACOption.getOpt().equals(newACOption.getOpt())) {
					throw new Exception("Duplicate opt '" + newACOption.getOpt() + 
							"' for object: " + newACOption.toString());
				}
				if (! ACOption.getOpt().isEmpty() && 
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
	 * @param pathnames
	 * @param coordinateProvider
	 * @return
	 * @throws IOException
	 */
	public abstract AbstractWorkerDispatcher<T> getInstance() throws IOException; 

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
		switch (getParameters().getConditionsSize()) {
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
			recordFilenames[conditionIndex] = parameters.getConditionParameters(conditionIndex).getRecordFilenames();
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
			SAMPathnameArg pa = new SAMPathnameArg(conditionIndex + 1, parameters.getConditionParameters(conditionIndex));
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
	
	public abstract T createData();
	public abstract T[] createReplicateData(final int n);
	public abstract T[][] createContainer(final int n);

	public abstract T copyData(final T data);
	public abstract T[] copyReplicateData(final T[] replicateData);
	public abstract T[][] copyContainer(final T[][] container);
	
}
