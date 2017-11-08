package lib.util;

import java.io.PrintStream;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Inspired by https://stackoverflow.com/questions/4573123/java-updating-text-in-the-command-line-without-a-new-line
 * 
 * @author Michael Piechotta
 */

public class ProgressIndicator {

	private final PrintStream ps;
	
	public ProgressIndicator(final PrintStream ps) {
		this.ps = ps;
	}

	// taken from https://stackoverflow.com/questions/4573123/java-updating-text-in-the-command-line-without-a-new-line
	public void update(final String s, final long start, final long current, final long total) {
		if (total == 0) {
			return;
		}

		final long eta = getETA(start, current, total);
		final String formattedETA = formatETA(eta, current, total);

		int percent = 0; 
		if (current > 0) {
			percent = (int) (current * 100 / total);
		}

		// TODO make this half size
		final StringBuilder sb = new StringBuilder()
		.append('\r')
    	.append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
    	.append(String.format(" %d%% [", percent))
    	.append(String.join("", Collections.nCopies(percent, "=")))
    	.append('>')
    	.append(String.join("", Collections.nCopies(100 - percent, " ")))
    	.append(']');
		if (current > 0) {
			sb.append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")));
		} else {
			sb.append(String.join("", Collections.nCopies((int) (Math.log10(total)), " ")));
		}
       	sb.append(String.format(" %d/%d, ETA: %s", current, total, formattedETA));

		ps.print(sb.toString());
	}
	
	public void print(String s) {
		ps.print(s);
	}
	
	private long getETA(final long start, final long current, final long total) {
		if (current == 0 || total == 0) {
			return 0;
		}

		return (total - current) * (System.currentTimeMillis() - start) / current;
	}

	private String formatETA(final long eta, final long current, final long total) {
		if (current == 0 || total == 0) {
			return "n/a";
		}
		
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));
	}

}
