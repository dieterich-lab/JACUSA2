package test.jacusa.filter.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.cache.Homopolymer;
import jacusa.filter.cache.Homopolymer.HomopolymerBuilder;
import lib.util.Base;

@DisplayName("Test HomopolymerBuilder")
class HomopolymerBuilderTest {

	static public final int RELATIVE_HOMOPOLYMER_POSITION = 0;
	
	/**
	 * Test method for {@link jacusa.filter.cache.HomopolymerBuilder#process(byte)}.
	 */
	@DisplayName("Should find the correct Homopolymers")
	@ParameterizedTest(name = "Seq: {0} should have {1} homopolymers, {2} position(s), and {3} lengths")
	@MethodSource("testAddNumberOfIdentifiedHomopolymers")
	void testAddNumberOfIdentifiedHomopolymers(String sequence, 
			int expectedHomopolymerCount, List<Integer> expectedPositions, List<Integer> expectedLengths, int minLength) {

		final HomopolymerBuilder homopolymerBuilder = new HomopolymerBuilder(RELATIVE_HOMOPOLYMER_POSITION, minLength);
		for (final char c : sequence.toCharArray()) {
			homopolymerBuilder.add(Base.valueOf(c));
		}
		final Collection<Homopolymer> homopolymers = homopolymerBuilder.build();
		assertEquals(expectedHomopolymerCount, homopolymers.size());

		List<Integer> positions = homopolymers.stream()
				.map(h -> h.getPosition())
				.collect(Collectors.toList());
		assertTrue(expectedPositions.equals(positions));
	}

	static Stream<Arguments> testAddNumberOfIdentifiedHomopolymers() {
		final int p = RELATIVE_HOMOPOLYMER_POSITION;
		return Stream.of(
				// format:
				// input sequence, 
				// expected # of homopolymers, 
				// expected positions, 
				// expected lengths, 
				// required minLength

				Arguments.of("ACGTAAAAAAATGT", 1, Arrays.asList(p + 5), Arrays.asList(7), 7),
				Arguments.of("ACGCCCCCCCGGT", 1, Arrays.asList(p + 4), Arrays.asList(7), 7),
				Arguments.of("AGGGACGGTTTAAANCCC", 4, Arrays.asList(p + 2, p + 9, p + 12, p + 16), Arrays.asList(3, 3, 3, 3), 3),
				Arguments.of("ACGATTTTATTTAGT", 1, Arrays.asList(p + 5), Arrays.asList(4), 4),
				Arguments.of("ACGNNNNNNNNNNNCGT", 0, Arrays.asList(), Arrays.asList(), 7),

				Arguments.of("ACGTAAAAAAATGT", 0, Arrays.asList(), Arrays.asList(), 8),
				Arguments.of("ACGGCCCCCCCGGT", 0, Arrays.asList(), Arrays.asList(), 8),
				Arguments.of("ACGCGGGGGGGCGT", 0, Arrays.asList(), Arrays.asList(), 8),
				Arguments.of("ACAATTTTTTTAGT", 0, Arrays.asList(), Arrays.asList(), 8),
				Arguments.of("ACGNCGGGGNGGGCGT", 0, Arrays.asList(), Arrays.asList(), 8));
	}
	
}
