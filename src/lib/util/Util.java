package lib.util;

import java.io.File;
import java.io.IOException;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

public final class Util {
	
	private Util() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param recordFilename
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public static SAMSequenceDictionary getSAMSequenceDictionary(final String recordFilename) throws IOException {
		final File file = new File(recordFilename);
		final SamReader reader = SamReaderFactory
				.make()
				.setOption(htsjdk.samtools.SamReaderFactory.Option.CACHE_FILE_BASED_INDEXES, false)
				.setOption(htsjdk.samtools.SamReaderFactory.Option.DONT_MEMORY_MAP_INDEX, true) // disable memory mapping
				.validationStringency(ValidationStringency.LENIENT)
				.open(file);
		final SAMSequenceDictionary sequenceDictionary = reader.getFileHeader().getSequenceDictionary();
		reader.close();
		return sequenceDictionary;
	}
	
	public static String printAlpha(final double a[]) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < a.length; ++i) {
			if (i > 0) {
				sb.append('\t');
			}
			sb.append(a[i]);
		}
		return sb.toString();
	}
	
	/*
	public static String printAlpha2(double a[]) {
		DecimalFormat df = new DecimalFormat("0.0000"); 
		StringBuilder sb = new StringBuilder();

		sb.append(df.format(a[0]));
		for (int i = 1; i < a.length; ++i) {
			sb.append("  ");
			sb.append(df.format(a[i]));
		}
		return sb.toString();
	}
	
	public static String printMatrix(final double m[][]) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < m.length; ++i) {
			for (int j = 0; j < m[i].length; ++j) {
				if (j > 0) {
					sb.append('\t');
				}
				sb.append(m[i][j]);
			}
			sb.append('\n');
		}

		return sb.toString();
	}
	*/

}