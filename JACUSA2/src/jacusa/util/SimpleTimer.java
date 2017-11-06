package jacusa.util;

import java.util.concurrent.TimeUnit;

/**
 * Implements a simple timer for benchmarking purposes.
 * 
 * @author Sebastian Fröhler
 *
 */
public class SimpleTimer {

	private long time;
	private long totalTime;
	
	public SimpleTimer(){
		this.startTimer();
	}
	
	public void startTimer(){
		time = System.currentTimeMillis();
		totalTime = System.currentTimeMillis();
	}
	
	public synchronized long getTime(){
		long currentTime = (System.currentTimeMillis() - time);
		time = System.currentTimeMillis();
		return currentTime;
	}
	
	public long getTotalTime(){
		return (System.currentTimeMillis() - totalTime);
	}

	public String getTotalTimestring(){
		long totalTime = getTotalTime();
		
		final long d = TimeUnit.MILLISECONDS.toDays(totalTime);
		final long hr = TimeUnit.MILLISECONDS.toHours(
				totalTime - TimeUnit.DAYS.toMillis(d));
        final long min = TimeUnit.MILLISECONDS.toMinutes(
        		totalTime - TimeUnit.DAYS.toMillis(d) - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(
        		totalTime - TimeUnit.DAYS.toMillis(d) - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));

        if (d > 0) {
        	return String.format("(%02d) %02d:%02d:%02d", d, hr, min, sec);
        } else {
        	return String.format("%02d:%02d:%02d", hr, min, sec);
        }
	}

}