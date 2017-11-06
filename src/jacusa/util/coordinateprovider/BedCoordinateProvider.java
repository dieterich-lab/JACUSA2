/**
 * 
 */
package jacusa.util.coordinateprovider;

import jacusa.util.Coordinate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author mpiechotta
 *
 */
public class BedCoordinateProvider implements CoordinateProvider {

	private String filename;
	private BufferedReader br;

	private int total;
	
	public BedCoordinateProvider(String filename) {
		this.filename = filename;

		total = 0;
		reset();
		while (hasNext()) {
			next();
			total++;
		}
		reset();
	}

	private void reset() {
		File file = new File(filename);

		try {
			if (br != null) {
				br.close();
			}
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean hasNext() {
		try {
			return br.ready();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Coordinate next() {
		if (hasNext()) {
			String line;
			try {
				line = br.readLine();
				line = line.trim();
				if(line.startsWith("#") || line.isEmpty()) {
					return next();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			Coordinate coordinate = new Coordinate();
			String[] cols = line.split("\t");

			coordinate.setContig(cols[0]);
			coordinate.setStart(Integer.parseInt(cols[1]) + 1);
			coordinate.setEnd(Integer.parseInt(cols[2]));

			return coordinate;
		}

		return null;
	}

	@Override
	public void remove() {
		// not needed
	}

	@Override
	public void close() throws IOException {
		br.close();
	}

	public String getFilename() {
		return filename;
	}

	public int getTotal() {
		return total;
	}

}