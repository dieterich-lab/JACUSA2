package lib.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeneralDataTypeContainer {
	
	private final Map<GeneralDataType<?>, Object> map;
	
	private GeneralDataTypeContainer(final int threads) {
		map = new ConcurrentHashMap<>();
	}

	public <T> T get(GeneralDataType<T> dataType) {
		if (! contains(dataType)) {
			return null;
		}
		return dataType.getEnclosingClass().cast(map.get(dataType));
	}

	public <T> void put(final GeneralDataType<T> dataType, T data) {
		if (map.containsKey(dataType)) {
			throw new IllegalArgumentException("Duplicate dataType: " + dataType); 
		}
		map.put(dataType, dataType.getEnclosingClass().cast(data));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof GeneralDataTypeContainer)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final GeneralDataTypeContainer container = (GeneralDataTypeContainer)obj;
		return map.equals(container.map);
	}

	public <T> boolean contains(GeneralDataType<T> dataType) {
		return map.containsKey(dataType);
	}
	
	public Collection<GeneralDataType<?>> getDataTypes() {
		return Collections.unmodifiableCollection(map.keySet());
	}
	
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder();

		for (final GeneralDataType<?> dataType : getDataTypes()) {
			sb.append(dataType.toString());
			sb.append('\n');
			// sb.append(get(dataType).toString());
			// sb.append('\n');
		}

		return sb.toString();
	}
	
}
