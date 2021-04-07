package info.unterrainer.commons.jpajsongenerator.enums;

import lombok.Getter;

public enum TargetType {

	JAVA_JSON("Json.java", "/**", " * ", " */"),
	JAVA_JPA("Jpa.java", "/**", " * ", " */"),
	C_SHARP_JSON("Json.cs", "/// <summary>", "///\t", "/// </summary>");

	@Getter
	private final String fileEnding;
	@Getter
	private final String commentStart;
	@Getter
	private final String commentBeginLine;
	@Getter
	private final String commentEnd;

	private TargetType(final String fileEnding, final String commentStart, final String commentBeginLine,
			final String commentEnd) {
		this.fileEnding = fileEnding;
		this.commentStart = commentStart;
		this.commentBeginLine = commentBeginLine;
		this.commentEnd = commentEnd;
	}

	public boolean isJava() {
		return this != C_SHARP_JSON;
	}

	public boolean isJpa() {
		return this == JAVA_JPA;
	}
}
