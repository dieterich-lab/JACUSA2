package lib.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

public final class Util {
	
	private Util() {
		throw new AssertionError();
	}

	public static int noRehashCapacity(final int size) {
		return (int)(Math.ceil(size / 0.75d)) + 1;
	}

	public static String format(final double value) {
		return Double.toString(value);
		// return String.format(Locale.ENGLISH, "%.3f", value)
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
	
	public static String printAlpha(final double[] a) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < a.length; ++i) {
			if (i > 0) {
				sb.append('\t');
			}
			sb.append(a[i]);
		}
		return sb.toString();
	}

	public static <T> String pack(final List<T> list, char separator) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				sb.append(separator);
			}
			T element = list.get(i);
			if (element instanceof Double || element instanceof String) {
				sb.append(element.toString());
			} else {
				throw new IllegalArgumentException("Unsupported list element type");
			}
		}

		return sb.toString();
	}



}