package lib.data;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lib.data.cache.fetcher.DataTypeFetcher;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.lrtarrest.Position2baseCallCount;
import lib.data.count.BaseSubstitutionCount;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.util.Data;

public final class DataType<T extends Data<T>> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static int ID = 0;
	
	public static final DataType<PileupCount> PILEUP_COUNT = create("Default", PileupCount.class); 
	public static final DataType<BaseCallCount> BCC = create("Default", BaseCallCount.class);
	public static final DataType<BaseCallCount> ARREST_BCC = create("Arrest", BaseCallCount.class);
	public static final DataType<BaseCallCount> THROUGH_BCC = create("Through", BaseCallCount.class);
	public static final DataType<Position2baseCallCount> AP2BCC = create("Default", Position2baseCallCount.class);
	
	public static final DataType<BaseSubstitutionCount> BASE_SUBST = create("Default", BaseSubstitutionCount.class);
	
	public static final DataType<BaseSubstitutionCount> ARREST_BASE_SUBST = create("Arrest", BaseSubstitutionCount.class);
	public static final DataType<BaseSubstitutionCount> THROUGH_BASE_SUBST = create("Through", BaseSubstitutionCount.class);

	public static final DataType<BaseCallCountFilteredData> F_BCC = create("Default", BaseCallCountFilteredData.class);
	public static final DataType<BooleanWrapperFilteredData> F_BOOLEAN = create("Default", BooleanWrapperFilteredData.class);
	public static final DataType<ArrestPos2BaseCallCountFilteredData> F_AP2BCC = create("Default", ArrestPos2BaseCallCountFilteredData.class);
	
	private final int id;
	private final String name;
	private final Class<T> enclosingClass; 

	private final Fetcher<T> fetcher;
	
	private DataType(final String name, final Class<T> enclosingClass) {
		this.id = ++ID;
		this.name = name;
		this.enclosingClass = enclosingClass;
		fetcher = new DataTypeFetcher<>(this);
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
	
	public static <T extends Data<T>> DataType<T> create(final String name, final Class<T> enclosingClass) {
		return new DataType<T>(name, enclosingClass);
	}

}
