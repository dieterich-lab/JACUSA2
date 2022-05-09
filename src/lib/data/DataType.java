package lib.data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lib.data.fetcher.Aggregate;
import lib.data.fetcher.Fetcher;

public final class DataType<T extends Data<T>> implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int dataTypeID = 0;
	private static final Map<Class<?>, Map<String, DataType<?>>> CLASS2NAME2DATAYPE = new HashMap<Class<?>, Map<String, DataType<?>>>();
	private static final Set<DataType<?>> DATATYPES = new HashSet<DataType<?>>();

	private final int id;
	private final String name;
	private final Class<T> enclosingClass;
	private Fetcher<T> fetcher;

	private DataType(final String name, final Class<T> enclosingClass, final Fetcher<T> fetcher) {
		this.id = ++dataTypeID;
		this.name = name;
		this.enclosingClass = enclosingClass;
		this.fetcher = fetcher;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DataType)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		final DataType<?> dataType = (DataType<?>) obj;
		return id == dataType.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public String getName() {
		return name;
	}

	protected void merge(Object o1, Object o2) {
		enclosingClass.cast(o1).merge(enclosingClass.cast(o2));
	}

	public Class<T> getEnclosingClass() {
		return enclosingClass;
	}

	public boolean isFetcher() {
		return fetcher != null;
	}

	public T newInstance() {
		try {
			T data;
			if (enclosingClass.isInterface()) {
				final Method method = enclosingClass.getMethod("newInstance");
				data = enclosingClass.cast(method.invoke(null));
			} else {
				data = enclosingClass.newInstance();
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return null;
	}

	public T fetch(final DataContainer dataContainer) throws Exception {
		return fetcher.fetch(dataContainer);
	}

	@Override
	public String toString() {
		return String.format("id: %d, name: %s, class: %s", id, name, enclosingClass.getName());
	}

	public List<T> extract(final List<DataContainer> containers) {
		final List<T> data = new ArrayList<T>();
		for (final DataContainer container : containers) {
			data.add(container.get(this));
		}
		return Collections.unmodifiableList(data);
	}
	
	public static synchronized <T extends Data<T>> DataType<T> retrieve(final String name,
			final Class<T> enclosingClass) {
		DataType<T> dataType = get(name, enclosingClass);
		if (dataType == null) {
			dataType = create(name, enclosingClass);
		}
		return dataType;
	}
	
	public static synchronized <T extends Data<T>> DataType<T> retrieve(final String name,
			final Class<T> enclosingClass, final Fetcher<T> fetcher) {
		DataType<T> dataType = get(name, enclosingClass);
		if (dataType == null) {
			dataType = create(name, enclosingClass, fetcher);
		}
		return dataType;
	}

	public static synchronized <T extends Data<T>> DataType<T> create(final String name, final Class<T> enclosingClass,
			final Fetcher<T> fetcher) {
		if (CLASS2NAME2DATAYPE.containsKey(enclosingClass)
				&& CLASS2NAME2DATAYPE.get(enclosingClass).containsKey(name)) {
			throw new IllegalArgumentException(
					"Duplicate name: " + name + " for class: " + enclosingClass.getCanonicalName());
		}
		final DataType<T> dataType = new DataType<>(name, enclosingClass, null);
		CLASS2NAME2DATAYPE.get(enclosingClass).put(name, dataType);
		DATATYPES.add(dataType);
		return dataType;
	}

	public static synchronized <T extends Data<T>> DataType<T> create(final String name,
			final Class<T> enclosingClass) {
		return create(name, enclosingClass, null);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Data<T>> DataType<T> get(final String name, final Class<T> enclosingClass) {
		final Map<String, DataType<?>> NAME2DATAYPE = CLASS2NAME2DATAYPE.get(enclosingClass);
		if (CLASS2NAME2DATAYPE.get(enclosingClass) == null) {
			return null;
		}

		final DataType<?> dataType = NAME2DATAYPE.get(name);
		if (dataType.enclosingClass != enclosingClass) {
			return null;
		}

		return (DataType<T>) dataType;
	}

	// TODO new ExtractFilter<FilteredBoolean, BooleanData>(getID(), dataType)
	DataType<IntegerData> THROUGH_COVERAGE = create("through_coverage", IntegerData.class);
	DataType<IntegerData> ARREST_COVERAGE = create("arrest_coverage", IntegerData.class);
	DataType<IntegerData> TOTAL_COVERAGE = create("total_coverage", IntegerData.class,
			new Aggregate<IntegerData>(IntegerData.class, Arrays.asList(THROUGH_COVERAGE, ARREST_COVERAGE)));
}
