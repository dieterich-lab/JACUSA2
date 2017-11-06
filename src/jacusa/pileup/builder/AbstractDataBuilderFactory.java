package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.util.WindowCoordinate;
import net.sf.samtools.SAMFileReader;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractDataBuilderFactory<T extends AbstractData>
implements hasLibraryType {

	final private LIBRARY_TYPE libraryType;
	
	public AbstractDataBuilderFactory(final LIBRARY_TYPE libraryType) {
		this.libraryType = libraryType;
	}
	
	/**
	 * 
	 * @param windowCoordinates
	 * @param reader
	 * @param condition
	 * @param parameters
	 * @return
	 */
	public abstract DataBuilder<T> newInstance(
			final WindowCoordinate windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters);

	/**
	 * 
	 * @return
	 */
	final public boolean isStranded() {
		return libraryType != LIBRARY_TYPE.UNSTRANDED;
	}

	@Override
	final public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
}
