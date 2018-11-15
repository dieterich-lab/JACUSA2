package lib.data;

public final class GeneralDataType<T> {
	
	private static int ID = 0;
	
	private final int id;
	private final String name;
	private final Class<T> enclosingClass; 
	
	private GeneralDataType(final String name, final Class<T> enclosingClass) {
		this.id = ++ID;
		this.name = name;
		this.enclosingClass = enclosingClass;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof GeneralDataType)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		final GeneralDataType<?> dataType = (GeneralDataType<?>) obj;
		return getId() == dataType.getId(); 
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public Class<T> getEnclosingClass() {
		return enclosingClass;
	}
	
	@Override
	public String toString() {
		return String.format("id: %d, name: %s, class: %s", id, name, enclosingClass.getName());
	}
	
	public static <T> GeneralDataType<T> create(final String name, final Class<T> enclosingClass) {
		return new GeneralDataType<T>(name, enclosingClass);
	}

}
