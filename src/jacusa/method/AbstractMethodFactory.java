package jacusa.method;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jacusa.JACUSA;
import jacusa.cli.options.AbstractACOption;
import jacusa.cli.options.SAMPathnameArg;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.AbstractData;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.util.Coordinate;
import jacusa.util.coordinateprovider.CoordinateProvider;
import jacusa.util.coordinateprovider.SAMCoordinateProvider;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

/**
 * 
 * @author Michael Piechotta
 *
 */
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

	public abstract void initACOptions();
	protected abstract void initConditionACOptions();
	protected abstract void initGlobalACOptions();

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
	public abstract AbstractWorkerDispatcher<T> getInstance(
			final CoordinateProvider coordinateProvider) throws IOException; 

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
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(160);

		Set<AbstractACOption> acOptions = getACOptions();
		Options options = new Options();
		for (AbstractACOption acoption : acOptions) {
			options.addOption(acoption.getOption());
		}
		
		int conditions = getParameters().getConditions();
		String files = new String();

		switch (conditions) {
		case 1:
			files = "[OPTIONS] BAM1_1[,BAM1_2,BAM1_3,...]";
			break;

		case 2:
			files = "[OPTIONS] BAM1_1[,BAM1_2,BAM1_3,...] BAM2_1[,BAM2_2,BAM2_3,...]";
			break;

		default:
			files = "[OPTIONS] BAM1_1[,BAM1_2,BAM1_3,...] BAM2_1[,BAM2_2,BAM2_3,...] ...";
			
			break;
		}
		
		formatter.printHelp(
				JACUSA.JAR + 
				" " + 
				getName() +
				" " +
				files, 
				options);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void initCoordinateProvider() throws Exception {
		final int conditions = parameters.getConditions();
		final String[][] pathnames = new String[conditions][];

		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			pathnames[conditionIndex] = parameters.getConditionParameters(conditionIndex).getPathnames();
		}

		final List<SAMSequenceRecord> records = getSAMSequenceRecords(pathnames);
		coordinateProvider = new SAMCoordinateProvider(records);
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean parseArgs(String[] args) throws Exception {
		for (int conditionIndex = 0; conditionIndex < args.length; conditionIndex++) {
			SAMPathnameArg pa = new SAMPathnameArg(conditionIndex + 1, parameters.getConditionParameters(conditionIndex));
			pa.processArg(args[conditionIndex]);
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param pathnames
	 * @return
	 * @throws Exception
	 */
	protected List<SAMSequenceRecord> getSAMSequenceRecords(String[] pathnames) throws Exception {
		JACUSA.printLog("Computing overlap between sequence records.");

		SAMFileReader reader 			= new SAMFileReader(new File(pathnames[0]));
		List<SAMSequenceRecord> records = reader.getFileHeader().getSequenceDictionary().getSequences();
		// close readers
		reader.close();

		return records;
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
	 * @param pathnames
	 * @return
	 * @throws Exception
	 */
	protected List<SAMSequenceRecord> getSAMSequenceRecords(String[][] pathnames) throws Exception {
		String error = "Sequence Dictionaries of BAM files do not match";

		List<SAMSequenceRecord> records = getSAMSequenceRecords(pathnames[0]);

		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		Set<String> targetSequenceNames = new HashSet<String>();
		for(SAMSequenceRecord record : records) {
			coordinates.add(new Coordinate(record.getSequenceName(), 1, record.getSequenceLength()));
			targetSequenceNames.add(record.getSequenceName());
		}

		for (int conditionIndex = 0; conditionIndex < pathnames.length; conditionIndex++) {
			if (! isValid(targetSequenceNames, pathnames[conditionIndex])) {
				throw new Exception(error);
			}
		}

		return records;		
	}

	/**
	 * 
	 * @param targetSequenceNames
	 * @param pathnames
	 * @return
	 */
	private boolean isValid(Set<String> targetSequenceNames, String[] pathnames) {
		Set<String> sequenceNames = new HashSet<String>();
		for(String pathname : pathnames) {
			SAMFileReader reader = new SAMFileReader(new File(pathname));
			List<SAMSequenceRecord> records	= reader.getFileHeader().getSequenceDictionary().getSequences();
			for(SAMSequenceRecord record : records) {
				sequenceNames.add(record.getSequenceName());
			}	
			reader.close();
		}

		if(!sequenceNames.containsAll(targetSequenceNames) || !targetSequenceNames.containsAll(sequenceNames)) {
			return false;
		}

		return true;
	}

	public void debug() {};
	
	public abstract T createData();
	public abstract T[] createReplicateData(final int n);
	public abstract T[][] createContainer(final int n);

	public abstract T copyData(final T data);
	public abstract T[] copyReplicateData(final T[] replicateData);
	public abstract T[][] copyContainer(final T[][] container);
	
}
