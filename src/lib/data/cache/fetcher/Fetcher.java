package lib.data.cache.fetcher;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lib.data.DataTypeContainer;
import lib.util.Copyable;
import lib.util.Mergeable;

public interface Fetcher<T extends Copyable<T> & Mergeable<T>> {

	public T fetch(DataTypeContainer container);

	static <T extends Copyable<T> & Mergeable<T>> List<T> apply(
			final Fetcher<T> fetcher, 
			final List<DataTypeContainer> containers) {
		
		return Collections.unmodifiableList(
				containers.stream()
					.map(fetcher::fetch)
					.collect(Collectors.toList()) );
	}
}
