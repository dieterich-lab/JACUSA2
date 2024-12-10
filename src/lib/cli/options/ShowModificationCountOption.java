package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowModificationCountOption extends AbstractACOption {

    private final GeneralParameter parameter;

    public ShowModificationCountOption(final GeneralParameter parameter) {
        super("M", "show-modifications");
        this.parameter = parameter;
    }

    @Override
    public Option getOption(final boolean printExtendedHelp) {
        return Option.builder(getOpt())
                .hasArg(false)
                .desc("Show modification score")
                .build();
    }

    @Override
    public void process(final CommandLine line) throws Exception {
        parameter.showModificationCount(true);
    }

}
