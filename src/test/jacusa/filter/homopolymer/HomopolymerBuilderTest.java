package test.jacusa.filter.homopolymer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import jacusa.filter.homopolymer.Homopolymer;
import jacusa.filter.homopolymer.Homopolymer.HomopolymerBuilder;
import lib.util.Base;
import test.utlis.IntegerListArgumentConverter;

/**
 * Tests jacusa.filter.homopolymer.Homopolymer.HomopolymerBuilder
 */
class HomopolymerBuilderTest {

	@DisplayName("Should find the correct Homopolymers")
	@ParameterizedTest(
			name = "Seq: {0} should have {1} homopolymer(s), {2} position(s), and {3} lengths")
	/* 
	 * format:
	 * 1. int()
	 * 2. String(input sequence)
	 * 3. List<Integer>(expected # of homopolymers)
	 * 4. List<Integer>(expected lengths)
	 * 5. List<Integer>(expected minLength)
	 */
	@CsvSource(
			delimiter = '\t',
			value = {
					"ACGTAAAAAAATGT	1	5	7	7",
					"ACGCCCCCCCGGT	1	4	7	7",
					"AGGGACGGTTTAAANCCC	4	2,9,12,16	3,3,3,3	3",
					"ACGATTTTATTTAGT	1	5	4	4",
					"ACGNNNNNNNNNNNCGT	0	*	*	7",

					"ACGTAAAAAAATGT	0	*	*	8",
					"ACGGCCCCCCCGGT	0	*	*	8",
					"ACGCGGGGGGGCGT	0	*	*	8",
					"ACAATTTTTTTAGT	0	*	*	8",
					"ACGNCGGGGNGGGCGT	0	*	*	8"					
			})
	void testAdd(
			String sequence, 
			int expectedHomopolymerCount, 
			@ConvertWith(IntegerListArgumentConverter.class) List<Integer> expectedPositions, 
			@ConvertWith(IntegerListArgumentConverter.class) List<Integer> expectedLengths, 
			int minLength) {

		final HomopolymerBuilder testInstance = new HomopolymerBuilder(1, minLength);
		for (final char c : sequence.toCharArray()) {
			testInstance.add(Base.valueOf(c));
		}
		
		// check number of homopolymers
		final Collection<Homopolymer> homopolymers = testInstance.build();
		assertEquals(expectedHomopolymerCount, homopolymers.size());

		// check positions
		List<Integer> actualPositions = homopolymers.stream()
				.map(h -> h.getPosition())
				.collect(Collectors.toList());
		assertEquals(expectedPositions, actualPositions);
		
		// check length
		List<Integer> actualLengths = homopolymers.stream()
				.map(h -> h.getLength())
				.collect(Collectors.toList());
		assertEquals(expectedLengths, actualLengths);
	}
	
}
