package jacusa.data;

import jacusa.phred2prob.Phred2Prob;

import java.util.Arrays;

public class BaseCallConfig {

	// dictionary to convert byte to int -> index to ALL, or valid
	public static final int[] BYTE2INT = new int[84 + 1];
	static {
		for (int i = 0; i < BYTE2INT.length; ++i) {
			BYTE2INT[i] = -1;
		}
		BYTE2INT[65] = 0; // 65 A
		BYTE2INT[67] = 1; // 67 C
		BYTE2INT[71] = 2; // 71 G
		BYTE2INT[84] = 3; // 84 T
		BYTE2INT[78] = 4; // 78 N
	}

	private static BaseCallConfig instance;
	
	
	private byte minBQ;
	
	// this bases are the one that are used for computation
	public static final char[] BASES = { 'A', 'C', 'G', 'T' };
	public static final char[] BASES_COMPLEMENT = { 'T', 'G', 'C', 'A' };

	private char[] bases;
	private int[] byte2int;
	
	private int[] baseIndex;
		
	// complement
	private int[] complement_byte2int;

	private BaseCallConfig() {
		processBases(BASES);
	}
	
	public static BaseCallConfig getInstance() {
		if (instance == null) {
			instance = new BaseCallConfig();
		}
		
		return instance;
	}
	
	/**
	 * Returns a mapping of byte(s) base calls to int(s)
	 * 
	 * @param bases
	 * @return
	 */
	private int[] byte2int(final char[] bases) {
		final int[] byte2int = new int[BYTE2INT.length];
		Arrays.fill(byte2int, -1);

		for (int i = 0; i < bases.length; ++i) {
			byte2int[(int)bases[i]] = i;
		}

		return byte2int;
	}

	/**
	 * 
	 * @param base
	 * @return
	 */
	public char complementBase(char base) {
		int baseIndex = BYTE2INT[base];

		if (baseIndex < 0) {
			return 'N';
		}

		return BASES_COMPLEMENT[baseIndex];
	}

	public int[] complementbyte2int(final char[] bases) {
		final int[] byte2int = new int[BYTE2INT.length];
		Arrays.fill(byte2int, -1);

		for (int i = 0; i < bases.length; ++i) {
			char base = complementBase(bases[i]);
			byte2int[(int)base] = getBaseIndex((byte)bases[i]);
		}
		return byte2int;
	}
	
	public int getComplementBaseIndex(byte base) {
		return complement_byte2int[base];
	}

	public char int2byte(final int baseIndex) {
		return bases[baseIndex];
	}
	
	public char[] int2byte(final int[] baseIndexs) {
		final char[] bases = new char[baseIndexs.length];
		for (int i = 0; i < bases.length; i++) {
			bases[i] = int2byte(baseIndexs[i]);
		}
		return bases;
	}
	
	public int getBaseIndex(byte base) {
		return byte2int[base];
	}

	public char[] getBases() {
		return bases;
	}

	public int[] getbyte2int() {
		return byte2int;
	}
	
	public int[] getComplementbyte2int() {
		return complement_byte2int;
	}
	
	private void processBases(char[] bases) {
		this.bases = bases;
		byte2int = byte2int(bases);
		complement_byte2int = complementbyte2int(bases);

		baseIndex = new int[bases.length];
		for (int i = 0; i < bases.length; ++i) {
			baseIndex[i] = i;
		}
	}

	public int[] getBaseIndex() {
		return baseIndex;
	}

	public byte getMaxBQ() {
		return Phred2Prob.MAX_Q;
	}
	
	public byte getMinBQ() {
		return minBQ;
	}
	
	public void setMinBQ(final byte minBQ) {
		this.minBQ = minBQ;
	}
}
