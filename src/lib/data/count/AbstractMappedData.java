package lib.data.count;

import java.util.HashMap;
import java.util.Map;

import lib.data.Data;

abstract public class AbstractMappedData<T extends AbstractMappedData<T, K, V>, K, V extends Data<V>>
		implements Data<T> {

	private static final long serialVersionUID = 1L;

	protected final Class<K> keyClass;
	protected final Class<V> valueClass;
	protected final Map<K, V> map;

	public AbstractMappedData(final Class<K> keyClass, final Class<V> valueClass) {
		this.keyClass = keyClass;
		this.valueClass = valueClass;
		map = new HashMap<K, V>();
	}

	public AbstractMappedData(T k2v) {
		keyClass = k2v.keyClass;
		valueClass = k2v.valueClass;
		map = new HashMap<K, V>();
		for (final K k : k2v.map.keySet()) {
			map.put(k, k2v.map.get(k).copy());
		}
	}

	@Override
	// public void merge(AbstractMappedData<T, K, V> k2v) {
	public void merge(T k2v) {
		for (final K k : k2v.map.keySet()) {
			if (!map.containsKey(k)) {
				map.put(k, k2v.map.get(k));
			} else {
				map.get(k).merge(k2v.map.get(k));
			}
		}
	}

	public Map<K, V> getMap() {
		return this.map;
	}
}
