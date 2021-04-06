package info.unterrainer.commons.jpajsongenerator.enums;

import java.util.HashMap;
import java.util.Map;

public enum BasicType {

	LONG("Long", "long"),
	FLOAT("Float", "float"),
	DOUBLE("Double", "double"),
	INT("Integer", "int"),
	STRING("String", "string"),
	DATETIME("LocalDateTime", "DateTime"),
	BOOL("Boolean", "bool");

	private static final Map<String, BasicType> BY_JAVA_VALUE = new HashMap<>();
	private static final Map<String, BasicType> BY_C_SHARP_VALUE = new HashMap<>();

	static {
		for (BasicType e : values())
			BY_JAVA_VALUE.put(e.javaValue, e);
		for (BasicType e : values())
			BY_C_SHARP_VALUE.put(e.cSharpValue, e);
	}

	public final String javaValue;
	public final String cSharpValue;

	private BasicType(final String javaValue, final String cSharpValue) {
		this.javaValue = javaValue;
		this.cSharpValue = cSharpValue;
	}

	public static BasicType ofJavaValue(final String javaValue) {
		return BY_JAVA_VALUE.get(javaValue);
	}

	public static BasicType ofCSharpValue(final String cSharpValue) {
		return BY_C_SHARP_VALUE.get(cSharpValue);
	}
}
