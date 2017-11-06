package jacusa.util;

import java.io.PrintStream;

public class Logger {

	private final PrintStream ps;
	
	private final String name;
	private final String version;
	private final String[] args; 

	private SimpleTimer simpleTimer;
	
	public Logger(final PrintStream ps, final String name, final String version, final String[] args) {
		this.ps = ps;
		this.name = name;
		this.version = version;
		this.args = args;

		simpleTimer = new SimpleTimer();

		addProlog(args);
	}

	public void addInfo(final String s) {
		addLine("INFO   ", s);
	}

	public void addError(final String s) {
		addLine("ERROR  ", s);
	}

	public void addWarning(String s) {
		addLine("WARNING", s);
	}
	
	public void addDebug(final String s) {
		addLine("DEBUG  ", s);
	}

	public String[]	getArgs() {
		return args;
	}
	
	private void addLine(final String type, final String s) {
		final String time = type + "\t" + simpleTimer.getTotalTimestring() + " ";
		ps.println(time + " " + s);
	}

	private void addProlog(final String[] args) {
		String lineSep = "--------------------------------------------------------------------------------";

		ps.println(lineSep);

		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" Version: ");
		sb.append(version);
		for(String arg : args) {
			sb.append(" " + arg);
		}
		ps.println(sb.toString());

		ps.println(lineSep);
	}
	
	public void addEpilog() {
		/*
		System.err.println("Results can be found in: " + cli.getMethodFactory().getParameters().getOutput().getInfo());

		String lineSep = "--------------------------------------------------------------------------------";

		System.err.println(lineSep);
		System.err.println("Analyzed Parallel Pileups:\t" + comparisons);
		System.err.println("Elapsed time:\t\t\t" + getSimpleTimer().getTotalTimestring());
		*/
	}
	
}
