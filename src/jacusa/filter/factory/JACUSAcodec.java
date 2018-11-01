package jacusa.filter.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.tribble.AsciiFeatureCodec;
import htsjdk.tribble.readers.LineIterator;
import lib.method.AbstractMethod;
import lib.util.AbstractTool;

public class JACUSAcodec extends AsciiFeatureCodec<ResultFeature> {

	private static final Pattern CALL_METHOD_PATTERN = 
			Pattern.compile("^" + AbstractTool.CALL_PREFIX + "([^ ]+) ([^ ]+).*");
	
	private final Map<String, AbstractMethod.AbstractFactory> name2methodFactory; 
	
	public JACUSAcodec(final Map<String, AbstractMethod.AbstractFactory> name2methodFactory) {
		super(ResultFeature.class);
		this.name2methodFactory = name2methodFactory;
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
	public ResultFeature decode(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object readActualHeader(LineIterator reader) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
