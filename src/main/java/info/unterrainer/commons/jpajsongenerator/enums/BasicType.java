package info.unterrainer.commons.jpajsongenerator.enums;

import info.unterrainer.commons.jpajsongenerator.jsons.GeneralType;
import lombok.Getter;

public enum BasicType {

	LONG("Long", "long", "Long", new GeneralType[] {}, new GeneralType[] {}, new GeneralType[] {}),
	FLOAT("Float", "float", "Float", new GeneralType[] {}, new GeneralType[] {}, new GeneralType[] {}),
	DOUBLE("Double", "double", "Double", new GeneralType[] {}, new GeneralType[] {}, new GeneralType[] {}),
	INT("Integer", "int", "Integer", new GeneralType[] {}, new GeneralType[] {}, new GeneralType[] {}),
	STRING("String", "string", "String", new GeneralType[] {}, new GeneralType[] {}, new GeneralType[] {}),
	DATETIME("LocalDateTime", "DateTime", "LocalDateTime", new GeneralType[] {}, new GeneralType[] {},
			new GeneralType[] { GeneralType.builder()
					.name("@Enumerated(EnumType.STRING)")
					.fqn("javax.persistence.Enumerated")
					.build(), GeneralType.builder().fqn("javax.persistence.EnumType").build() }),
	BOOL("Boolean", "bool", "Boolean", new GeneralType[] {}, new GeneralType[] {}, new GeneralType[] {});

	@Getter
	private final String javaValue;
	@Getter
	private final String cSharpValue;
	@Getter
	private final String jpaValue;
	@Getter
	private final GeneralType[] javaTypes;
	@Getter
	private final GeneralType[] cSharpTypes;
	@Getter
	private final GeneralType[] jpaTypes;

	private BasicType(final String javaValue, final String cSharpValue, final String jpaValue,
			final GeneralType[] javaTypes, final GeneralType[] cSharpTypes, final GeneralType[] jpaTypes) {
		this.javaValue = javaValue;
		this.cSharpValue = cSharpValue;
		this.jpaValue = jpaValue;
		this.javaTypes = javaTypes;
		this.cSharpTypes = cSharpTypes;
		this.jpaTypes = jpaTypes;
	}
}
