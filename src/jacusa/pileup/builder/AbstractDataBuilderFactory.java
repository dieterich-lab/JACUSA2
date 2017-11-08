package jacusa.pileup.builder;

import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.AbstractData;
import lib.util.WindowCoordinate;

import htsjdk.samtools.SamReader;

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
			final SamReader reader, 
			final JACUSAConditionParameters<T> condition,
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
