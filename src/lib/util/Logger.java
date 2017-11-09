package lib.util;

import java.io.PrintStream;

public class Logger {

	private final PrintStream ps;
	private final AbstractTool tool;
	
	private SimpleTimer simpleTimer;
	
	public Logger(final PrintStream ps, final AbstractTool tool) {
		this.ps = ps;
		this.tool = tool;

		simpleTimer = new SimpleTimer();
	}

	public boolean isDebug() {
		return tool.getCLI().getMethodFactory().getParameter().isDebug();
	}
	
	public AbstractTool getTool() {
		return tool;
	}
	
	public SimpleTimer getTimer() {
		return simpleTimer;
	}
	
	public void addInfo(final String s) {
		addLine("INFO   ", s);
	}

	public void addError(final String s) {
		addLine("ERROR  ", s);
	}

	public void addWarning(final String s) {
		addLine("WARNING", s);
	}
	
	public void addDebug(final String s) {
		addLine("DEBUG  ", s);
	}

	public void addProlog(final String s) {
		ps.println(s);
	}
	
	public void addEpilog(final String s) {
		ps.println(s);
	}
	
	private void addLine(final String type, final String s) {
		final String time = type + "\t" + simpleTimer.getTotalTimestring() + " ";
		ps.println(time + " " + s);
	}
	
}
