package lib.io.codec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import htsjdk.tribble.AsciiFeatureCodec;
import htsjdk.tribble.readers.LineIterator;
import jacusa.JACUSA;
import jacusa.filter.factory.ResultFeature;
import lib.data.DefaultDataContainer;
import lib.data.ParallelData;
import lib.data.has.LibraryType;
import lib.data.result.OneStatResult;
import lib.data.result.Result;
import lib.method.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.Util;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

public class JACUSA2codec extends AsciiFeatureCodec<ResultFeature> {

	public static final int CONTIG_INDEX 	= 0;
	public static final int START_INDEX 	= 1;
	public static final int END_INDEX 		= 2;
	public static final int NAME_INDEX 		= 3;
	public static final int SCORE_INDEX 	= 4;
	public static final int STRAND_INDEX 	= 5;
	
	public static final Pattern CALL_METHOD_PATTERN = 
			Pattern.compile("^" + AbstractTool.CALL_PREFIX + "([^ ]+) ([^ ]+).*");
	
	private final Map<String, AbstractMethod.AbstractFactory> name2methodFactory; 
	
	public JACUSA2codec(final Map<String, AbstractMethod.AbstractFactory> name2methodFactory) {
		super(ResultFeature.class);
		this.name2methodFactory = name2methodFactory;
	}

	public JACUSA2codec() {
		this(JACUSA.getLogger().getTool().getCLI().getMethodFactories().stream()
				.collect(
						Collectors.toMap(
								AbstractMethod.AbstractFactory::getName, 
								Function.identity())) );
	}
	
	@Override
	public boolean canDecode(String path) {
		final File file = new File(path);
		
		try {
			final BufferedReader br = new BufferedReader(
					new FileReader(file));
			
			String line;
			Matcher matcher;
			while ((line = br.readLine()) != null && 
					(matcher = CALL_METHOD_PATTERN.matcher(line)) != null) {
				if (matcher.find()) {
					final String methodName = matcher.group(2);
					br.close();
					return name2methodFactory.containsKey(methodName);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public ResultFeature decode(LineIterator lineIterator) {
		return decode(lineIterator);
	}
	
	@Override
	public ResultFeature decode(String s) {
		final String[] token = s.split(Character.toString(Util.FIELD_SEP)); 
		return decode(token);
	}

	public Coordinate decodeCoordinate(final String[] token) {
		return new Coordinate(
				token[CONTIG_INDEX], 
				Integer.parseInt(token[START_INDEX]), 
				Integer.parseInt(token[END_INDEX]), 
				STRAND.valueOf(token[STRAND_INDEX].charAt(0)) );
	}
	
	// FIXME - only reconstructs coordinates
	public Result createResult(final String[] token) {
		final Coordinate coordinate = decodeCoordinate(token);
		
		final ParallelData.Builder pdBuilder = new ParallelData.Builder(1, Arrays.asList(1));
		DefaultDataContainer.Builder dcBuilder = new DefaultDataContainer.Builder(coordinate, LibraryType.MIXED);
		
		final ParallelData parallelData = 
				pdBuilder.withReplicate(
						0, 0, 
						dcBuilder.build())
				.build();
		
		final double stat = Double.parseDouble(token[SCORE_INDEX]); 
		return new OneStatResult(stat, parallelData); 
	}
	
	public ResultFeature decode(final String[] token) {
		final Result result = createResult(token);
		return new ResultFeature(result);
	}


	public Object readActualHeader(LineIterator lineIterator) {
		String line;
		while (lineIterator.hasNext()) {
	        line = lineIterator.peek();
	        if (isHeaderLine(line)) {
	            lineIterator.next();
	        } else {
	            return null; // break out when we've seen the end of the header
	        }
	    }
		
		return null;
	}

	public boolean isHeaderLine(final String line) {
		return line.startsWith(Util.HEADER);
	}
	
}
