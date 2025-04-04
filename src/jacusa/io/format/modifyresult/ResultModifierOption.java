package jacusa.io.format.modifyresult;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;

public class ResultModifierOption extends AbstractProcessingOption{

	private final ResultModifier resultModifier;
	private final List<ResultModifier> selected;
	
	public ResultModifierOption(
			final ResultModifier resultModifier,
			final List<ResultModifier> selected) {
		super(resultModifier.getID(), resultModifier.getID());

		this.resultModifier = resultModifier;
		this.selected = selected;
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder()
				.longOpt(getLongOpt())
				.desc(resultModifier.getDesc())
				.build();
	}
	
	@Override
	public void process(CommandLine cmd) throws Exception {
		if (!selected.contains(resultModifier)) {
			selected.add(resultModifier);
		}
	}
	
}
