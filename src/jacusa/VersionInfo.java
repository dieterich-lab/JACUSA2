package jacusa;

public final class VersionInfo {

	public final static String BRANCH 	= "master";
	public final static String TAG 		= "2.0.0-BETA32-TEST";

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
