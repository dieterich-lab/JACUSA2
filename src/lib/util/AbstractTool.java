package lib.util;

import java.io.PrintStream;
import java.util.List;

import jacusa.VersionInfo;
import lib.cli.CLI;
import lib.worker.WorkerDispatcher;

/**
 * TODO
 */
public abstract class AbstractTool {

	public static final String CALL_PREFIX = "JACUSA2 Version: ";

	private final String name;
	private final VersionInfo versionInfo;

	private final String[] args;

	// command line interface
	private final CLI cli;
	private WorkerDispatcher workerDispatcher;

	private int comparisons;

	private static Logger logger;

	protected AbstractTool(final String name, final VersionInfo versionInfo, final String[] args,
			final List<AbstractMethod.AbstractMethodFactory<?>> factories) {
		this.name = name;
		this.versionInfo = versionInfo;
		this.args = args;

		cli = new CLI(factories);

		comparisons = 0;

		final PrintStream ps = System.err;
		logger = new Logger(ps, this);
	}

	public void run() throws Exception {
		// parse CLI
		if (!cli.processArgs(args)) {
			System.exit(1);
		}

		// prolog printed in logger
		getLogger().addProlog(getProlog());

		// instantiate chosen method
		final AbstractMethod<?> methodFactory = cli.getMethodFactory();

		// run the method...
		workerDispatcher = methodFactory.getWorkerDispatcherInstance();
		comparisons = workerDispatcher.run();

		getLogger().addEpilog(getEpilog());
	}

	protected abstract String getEpilog();

	protected String getProlog() {
		final StringBuilder sb = new StringBuilder();
		final String lineSep = "--------------------------------------------------------------------------------";

		sb.append(lineSep);
		sb.append('\n');
		sb.append(getCall());
		sb.append('\n');
		sb.append(lineSep);
		return sb.toString();
	}

	public String getCall() {
		final StringBuilder sb = new StringBuilder();
		sb.append(CALL_PREFIX);
		sb.append(versionInfo.formatVersion());
		for (final String arg : args) {
			sb.append(" " + arg);
		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return versionInfo.formatVersion();
	}

	public String getLibraries() {
		return versionInfo.formatLibs();
	}

	public static Logger getLogger() {
		return logger;
	}

	public CLI getCLI() {
		return cli;
	}

	public int getComparisons() {
		return comparisons;
	}

	public WorkerDispatcher getWorkerDispatcher() {
		return workerDispatcher;
	}

}
