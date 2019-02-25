package jacusa;

/**
 * This class stores version info and optional branch information.
 */
public final class VersionInfo {

	public final static String BRANCH 	= "develop";
	public final static String TAG 		= "2.0.0-BETA36-TEST";

	private VersionInfo() {
		throw new AssertionError();
	}
	
	public static String format() {
		final StringBuilder sb = new StringBuilder();
		sb.append(TAG);

		// only add branch when not on master
		if (! BRANCH.equals("master")) {
			sb.append(" (");
			sb.append(VersionInfo.BRANCH);
			sb.append(')');
		}

		return sb.toString();
	}
	
}
