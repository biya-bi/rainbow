package org.rainbow.asset.explorer.persistence.dao;

import java.util.Collections;
import java.util.List;

public class NumberHelper {
	public static int getLeastAvailable(final List<Integer> numbers) {
		if (numbers.isEmpty()) {
			return 1;
		}
		Collections.sort(numbers);
		final Integer max = numbers.get(numbers.size() - 1);

		if (max == null || max == 0) {
			return 1;
		}
		for (int i = 1; i < max; i++) {
			if (!numbers.contains(i))
				return i;
		}
		return max + 1;
	}
}