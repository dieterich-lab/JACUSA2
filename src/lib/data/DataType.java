package lib.data;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import lib.data.count.BaseSubstitutionCount;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.DataTypeFetcher;
import lib.data.fetcher.Fetcher;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;

public final class DataType<T extends Data<T>> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static int ID = 0;
	private static Map<Class<?>, Map<String, DataType<?>>> CLASS2NAME2TYPE = new HashMap<>();

	private static String DEFAULT = "Default";
	
	public static final DataType<PileupCount> PILEUP_COUNT = 
			create(DEFAULT, PileupCount.class);
	
	public static final DataType<BaseCallCount> BCC = 
			create(DEFAULT, BaseCallCount.class);
	
	public static final DataType<BaseCallCount> ARREST_BCC = 
			create("Arrest", BaseCallCount.class);
	
	public static final DataType<BaseCallCount> THROUGH_BCC = 
			create("Through", BaseCallCount.class);
	
	public static final DataType<ArrestPosition2baseCallCount> AP2BCC = 
			create(DEFAULT, ArrestPosition2baseCallCount.class);
	
	public static final DataType<BaseSubstitutionCount> BASE_SUBST = 
			create(DEFAULT, BaseSubstitutionCount.class);
	
	public static final DataType<BaseSubstitutionCount> ARREST_BASE_SUBST = 
			create("Arrest", BaseSubstitutionCount.class);
	
	public static final DataType<BaseSubstitutionCount> THROUGH_BASE_SUBST = 
			create("Through", BaseSubstitutionCount.class);

	public static final DataType<BaseCallCountFilteredData> F_BCC = 
			create(DEFAULT, BaseCallCountFilteredData.class);
	
	public static final DataType<BooleanFilteredData> F_BOOLEAN = 
			create(DEFAULT, BooleanFilteredData.class);
	
	public static final DataType<ArrestPos2BaseCallCountFilteredData> F_AP2BCC = 
			create(DEFAULT, ArrestPos2BaseCallCountFilteredData.class);

	
	private final int id;
	private final String name;
	private final Class<T> enclosingClass; 

	private final Fetcher<T> fetcher;
	
	private DataType(final String name, final Class<T> enclosingClass) {
		this.id 			= ++ID;
		this.name 			= name;
		this.enclosingClass = enclosingClass;
		fetcher 			= new DataTypeFetcher<>(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof DataType)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		final DataType<?> dataType = (DataType<?>) obj;
		return getId() == dataType.getId(); 
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
	
	public int getId() {
		return id;
	}
	
	public Class<T> getEnclosingClass() {
		return enclosingClass;
	}

	public T newInstance() {
		try {
			if (enclosingClass.isInterface()) {
				final Method method = enclosingClass.getMethod("newInstance");
				return enclosingClass.cast(method.invoke(null));
			}
			return enclosingClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Fetcher<T> getFetcher() {
		return fetcher;
	}
	
	@Override
	public String toString() {
		return String.format("id: %d, name: %s, class: %s", id, name, enclosingClass.getName());
	}
	
	public synchronized static <T extends Data<T>> DataType<T> retrieve(final String name, final Class<T> enclosingClass) {
		DataType<T> dataType = get(name, enclosingClass);
		if (dataType == null) {
			dataType = create(name, enclosingClass);
		}
		return dataType;
	}
	
	public synchronized static <T extends Data<T>> DataType<T> create(final String name, final Class<T> enclosingClass) {
		if (! CLASS2NAME2TYPE.containsKey(enclosingClass)) {
			CLASS2NAME2TYPE.put(enclosingClass, new HashMap<>());
		}
		final Map<String, DataType<?>> name2type = CLASS2NAME2TYPE.get(enclosingClass); 
		if (name2type.containsKey(name)) {
			throw new IllegalArgumentException(
					"Duplicate name: " + name +  
					" for class: " + enclosingClass.getCanonicalName());
		}
		final DataType<T> dataType = new DataType<T>(name, enclosingClass);
		name2type.put(name, dataType);		
		return dataType;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Data<T>> DataType<T> get(final String name, final Class<T> enclosingClass) {
		if (! CLASS2NAME2TYPE.containsKey(enclosingClass)) {
			return null;
		}
		final Map<String, DataType<?>> name2type = CLASS2NAME2TYPE.get(enclosingClass); 
		if (name2type.containsKey(name)) {
			return (DataType<T>)name2type.get(name);
		}
		return null;
	}
	
}
