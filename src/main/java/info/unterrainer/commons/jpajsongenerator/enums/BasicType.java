package info.unterrainer.commons.jpajsongenerator.enums;

import lombok.Getter;

public enum BasicType {

	LONG("Long", "long", "Long"),
	FLOAT("Float", "float", "Float"),
	DOUBLE("Double", "double", "Double"),
	INT("Integer", "int", "Integer"),
	STRING("String", "string", "String"),
	DATETIME("LocalDateTime", "DateTime", "LocalDateTime"),
	BOOL("Boolean", "bool", "Boolean");

	@Getter
	private final String javaValue;
	@Getter
	private final String cSharpValue;
	@Getter
	private final String jpaValue;

	private BasicType(final String javaValue, final String cSharpValue, final String jpaValue) {
		this.javaValue = javaValue;
		this.cSharpValue = cSharpValue;
		this.jpaValue = jpaValue;
	}
}
