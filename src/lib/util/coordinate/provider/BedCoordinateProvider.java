/**
 * 
 */
package lib.util.coordinate.provider;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.OneCoordinate;

/**
 * TODO
 */
public class BedCoordinateProvider implements CoordinateProvider {

	private final String filename;
	
	private final List<Coordinate> coordinates;
	private final Iterator<Coordinate> coordinateIterator;
	
	public BedCoordinateProvider(final String filename, final boolean isStranded) {
		this.filename 	= filename;

		coordinates 		= read(filename, isStranded);
		coordinateIterator 	= coordinates.iterator();
	}

	private List<Coordinate> read(final String fileName, final boolean isStranded) {
		final List<Coordinate> cords = new ArrayList<>();
		
		final File file = new File(fileName);
		
		try {
			final BufferedReader br = new BufferedReader(new FileReader(file));	
			while(br.ready()) {
				String line = br.readLine().trim();
				if (line.startsWith("#") || 
						line.startsWith("track ") ||
						line.startsWith("browser ") ||
						line.isEmpty()) {
					continue;
				}

				String[] cols = line.split("\t");
				final Coordinate coordinate = new OneCoordinate(
						cols[0], 
						Integer.parseInt(cols[1]) + 1, Integer.parseInt(cols[2]));

				// try to get strand
				if (cols.length >= 6) {
					coordinate.setStrand(STRAND.valueOf(cols[6]));
				} else if (isStranded) {
					coordinate.setStrand(STRAND.FORWARD);
				}

				cords.add(coordinate);
			}
			br.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		return cords;
	}
	
	@Override
	public boolean hasNext() {
		return coordinateIterator.hasNext();
	}

	@Override
	public Coordinate next() {
		return coordinateIterator.next();

	}

	@Override
	public void remove() {
		// not needed
	}

	@Override
	public void close() throws IOException {
		// nothing to be done closed in read()
	}

	public String getFilename() {
		return filename;
	}

	public int getTotal() {
		return coordinates.size();
	}

}