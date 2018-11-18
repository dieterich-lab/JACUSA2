package jacusa;

public final class VersionInfo {

	public final static String BRANCH 	= "generic_container";
	public final static String TAG 		= "2.0.0-BETA28";

	private VersionInfo() {
		throw new AssertionError();
	}
	
	public static String get() {
		final StringBuilder sb = new StringBuilder();
		sb.append(TAG);

		if (! BRANCH.equals("master")) {
			sb.append(" (");
			sb.append(VersionInfo.BRANCH);
			sb.append(')');
		}

		return sb.toString();
	}
	
}