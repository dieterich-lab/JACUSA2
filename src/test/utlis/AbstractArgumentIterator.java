package test.utlis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.params.provider.Arguments;

public abstract class AbstractArgumentIterator implements Iterator<Arguments> {

	private final String fileName;
	private final String sep;
	
	private BufferedReader br;
	private String line;
	
	public AbstractArgumentIterator(final String fileName) throws FileNotFoundException {
		this(fileName, ",");
	}

	public AbstractArgumentIterator(final String fileName, final String sep) throws FileNotFoundException {
		this.fileName = fileName;
		this.sep = sep;

		br = new BufferedReader(new FileReader(fileName));
	}

	protected abstract Arguments createArguments(final String[] cols); 
		
	@Override
	public Arguments next() {
		if (! hasNext()) {
			return null;
		}

		final Arguments arguments = createArguments(line.split(sep)); 
		line = null;

		return arguments;
	}
		
	@Override
	public boolean hasNext() {
		if (br == null) {
			return false;
		}
		
		if (line != null) {
			return true;
		} else {
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) {
				try {
					close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			}
			return true;
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void close() throws IOException {
		if (br != null) {
			br.close();
			br = null;
		}
	}

}
