package lib.stat;

import org.apache.commons.cli.Option;

import lib.data.has.HasProcessCommandLine;
import lib.stat.dirmult.ProcessCommandLine;
import lib.util.CLIUtil;

/**
 * TODO add documentation
 */
public abstract class AbstractStatFactory implements HasProcessCommandLine {

	private final Option option;
	private final ProcessCommandLine processCommandLine;
	
	public AbstractStatFactory(final Option option, final ProcessCommandLine processComandLine) {
		this.option 			= option;
		this.processCommandLine = processComandLine;
	}

	public AbstractStatFactory(final Option option) {
		this(option, new ProcessCommandLine());
	}
	
	@Override
	public ProcessCommandLine getProcessCommandLine() {
		return processCommandLine;
	}
	
	/**
	 * Returns a new instance of this StatisticCalculator.
	 * @param threshold
	 * @return
	 */
	public abstract AbstractStat newInstance(double threshold, int conditions);

	/**
	 * Return the short name of this StatisticCalculator.
	 * @return
	 */
	public String getName() {
		return option.getOpt();
	}
	
	/**
	 * Return a short description of this StatisticCalculator.
	 * @return
	 */
	public String getDesc() {
		Option tmp = (Option)option.clone();
		CLIUtil.adjustOption(tmp, getProcessCommandLine().getOptions(), tmp.getOpt().length());
		return tmp.getDescription();
	}

	public void process(final String[] args) {
		processCommandLine.process(args);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof AbstractStatFactory)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final AbstractStatFactory asf = (AbstractStatFactory) obj;
		return getName().equals(asf.getName()) && getDesc().equals(asf.getDesc()); 
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + getName().hashCode();
		hash = 31 * hash + getDesc().hashCode();
		return hash;
	}
	
}
