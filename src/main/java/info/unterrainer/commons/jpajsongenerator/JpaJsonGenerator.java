package info.unterrainer.commons.jpajsongenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.unterrainer.commons.cliutils.Arg;
import info.unterrainer.commons.cliutils.Cli;
import info.unterrainer.commons.cliutils.CliParser;
import info.unterrainer.commons.jpajsongenerator.enums.SubType;
import info.unterrainer.commons.jpajsongenerator.enums.TargetType;
import info.unterrainer.commons.jpajsongenerator.jsons.ConfigJson;
import info.unterrainer.commons.jpajsongenerator.jsons.ConversionJson;
import info.unterrainer.commons.jpajsongenerator.jsons.FieldJson;
import info.unterrainer.commons.jpajsongenerator.jsons.FileJson;
import info.unterrainer.commons.jpajsongenerator.jsons.GeneralType;
import info.unterrainer.commons.jreutils.Resources;
import info.unterrainer.commons.serialization.JsonMapper;
import info.unterrainer.commons.serialization.exceptions.JsonMappingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JpaJsonGenerator {

	private static JsonMapper jsonMapper = JsonMapper.create();
	private static String newLine = "\n";

	private class LongOpt {
		public static final String CONFIG = "config";
		public static final String CONVERSION = "conversion";
	}

	public static void main(final String[] args) {
		Cli cli = CliParser
				.cliFor(args, "JpaJsonGenerator", "a tool to generate JPA-DTOs for JAVA and JSON-DTOs for JAVA and C#")
				.addArg(Arg.String(LongOpt.CONFIG)
						.shortName("f")
						.description(
								"the path to the config-file to use (default is 'config.json' at program location)")
						.defaultValue("config.json")
						.optional())
				.addArg(Arg.String(LongOpt.CONVERSION)
						.shortName("c")
						.description(
								"the conversion-step to do (one of JAVA_JPA, JAVA_JSON or C_SHARP_JSON) (default is all defined in the config)")
						.optional())
				.create();
		if (cli.isHelpSet())
			System.exit(0);

		String configPath = cli.getArgValue(LongOpt.CONFIG);
		ConfigJson config = null;
		try {
			String configString = Resources.readResource(JpaJsonGenerator.class, configPath);
			config = jsonMapper.fromStringTo(ConfigJson.class, configString);
		} catch (IOException e1) {
			log.error("Could not find/read config-file [{}]", configPath);
			System.exit(1);
		} catch (JsonMappingException me) {
			log.error("Error parsing config-file.", me);
			System.exit(1);
		}

		if (config.getNewLine() != null)
			newLine = config.getNewLine();

		String conv = cli.getArgValue(LongOpt.CONVERSION);
		TargetType targetType = null;
		try {
			if (conv != null)
				targetType = TargetType.valueOf(conv);
		} catch (IllegalArgumentException e) {
			log.error("[{}] is not a valid value for conversion-type. Use one of [{}]", conv, String.join(",",
					Arrays.stream(TargetType.values()).map(TargetType::toString).toArray(size -> new String[size])));
			System.exit(1);
		}

		for (ConversionJson conversion : config.getConversions()) {
			getSourcePath(conversion);
			Path targetPath = getTargetPath(conversion);

			if (targetType == null || targetType.equals(conversion.getTargetType()))
				convert(config, conversion, targetPath, targetType);
		}
	}

	private static Path getSourcePath(final ConversionJson conversion) {
		Path path = null;
		try {
			path = Path.of(conversion.getSourceDir());
		} catch (InvalidPathException e) {
			log.error("SourcePath was invalid.", e);
			System.exit(1);
		}
		if (!Files.exists(path)) {
			log.error("Source-directory [{}] specified in conversion of type [{}] does not exist.",
					conversion.getSourceDir(), conversion.getTargetType());
			System.exit(1);
		}
		return path;
	}

	private static Path getTargetPath(final ConversionJson conversion) {
		Path path = Path.of("");
		if (conversion.getTargetDir() != null) {
			try {
				path = Path.of(conversion.getTargetDir());
			} catch (InvalidPathException e) {
				log.error("TargetPath was invalid.", e);
				System.exit(1);
			}
			try {
				Files.createDirectories(path);
			} catch (Exception e) {
				log.error("TargetPath could not be created.", e);
				System.exit(1);
			}
		}
		return path;
	}

	private static void convert(final ConfigJson config, final ConversionJson conversion, final Path targetPath,
			final TargetType targetType) {
		for (Path path : getFileList(conversion.getSourceDir())) {
			String source = null;
			FileJson sourceJson = null;
			try {
				source = Resources.readResource(JpaJsonGenerator.class, path.toString());
				sourceJson = jsonMapper.fromStringTo(FileJson.class, source);
			} catch (IOException e) {
				log.error("Error reading input-file.", e);
				System.exit(1);
			} catch (JsonMappingException me) {
				log.error("Error parsing input-file.", me);
				System.exit(1);
			}

			String result = convertFile(config, conversion, targetType, sourceJson);
			try {
				Files.write(targetPath.resolve(path.getFileName().toString() + "." + targetType.getFileEnding()),
						result.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				log.error("Error writing generated file.", e);
				System.exit(1);
			}
		}
	}

	private static String convertFile(final ConfigJson config, final ConversionJson conversion,
			final TargetType targetType, final FileJson source) {
		StringBuilder sb = new StringBuilder();

		Set<String> imports = collectImports(conversion);
		if (targetType.isJava()) {
			sb.append(getNamespace(conversion, targetType));
			sb.append(newLine);
			sb.append(getImports(conversion, imports, targetType));
			sb.append(newLine);
			sb.append(getHeader(config));
			sb.append(newLine);
			sb.append(getClassAnnotations(conversion, targetType));
			sb.append(newLine);
			sb.append(getClassDeclaration(conversion, targetType));
			sb.append(newLine);
		} else {
			sb.append(getHeader(config));
			sb.append(newLine);
			sb.append(getImports(conversion, imports, targetType));
			sb.append(newLine);
			sb.append(getNamespace(conversion, targetType));
			sb.append(newLine);
			sb.append(getClassAnnotations(conversion, targetType));
			sb.append(newLine);
			sb.append(getClassDeclaration(conversion, targetType));
			sb.append(newLine);
		}
		sb.append("{");
		sb.append(newLine);
		if (source.getFields() != null)
			for (FieldJson field : source.getFields()) {
				sb.append(getComment(field, targetType));
				sb.append(getFieldAnnotations(field, targetType));
				sb.append(getField(field, targetType));
				sb.append(newLine);
			}
		sb.append("}");
		sb.append(newLine);
		return sb.toString();
	}

	private static String getNamespace(final ConversionJson conversion, final TargetType targetType) {
		StringBuilder sb = new StringBuilder();
		if (targetType.isJava()) {
			sb.append("package ");
			sb.append(conversion.getNamespace());
			sb.append(";");
			sb.append(newLine);
		} else {
			sb.append("namespace ");
			sb.append(conversion.getNamespace());
			sb.append(newLine);
			sb.append("{");
			sb.append(newLine);
		}
		return sb.toString();
	}

	private static String getImports(final ConversionJson conversion, final Set<String> imports,
			final TargetType targetType) {
		StringBuilder sb = new StringBuilder();
		if (targetType.isJava())
			for (String line : imports) {
				sb.append("import ");
				sb.append(line);
				sb.append(";");
				sb.append(newLine);
			}
		else
			for (String line : imports) {
				sb.append("using ");
				sb.append(line);
				sb.append(";");
				sb.append(newLine);
			}
		return sb.toString();
	}

	private static String getClassAnnotations(final ConversionJson conversion, final TargetType targetType) {
		StringBuilder sb = new StringBuilder();
		for (GeneralType line : conversion.getClassAnnotations()) {
			sb.append(line);
			sb.append(newLine);
		}
		return sb.toString();
	}

	private static String getClassDeclaration(final ConversionJson conversion, final TargetType targetType) {
		StringBuilder sb = new StringBuilder();
		if (targetType.isJava()) {
			sb.append("public class ");
			sb.append(conversion.getClass());
			sb.append(" {");
		} else {
			sb.append("\tpublic class ");
			sb.append(conversion.getClass());
			sb.append(newLine);
			sb.append("\t{");
			sb.append(newLine);
		}
		sb.append(newLine);
		return sb.toString();
	}

	private static Set<String> collectImports(final ConversionJson conversion) {
		Set<String> r = new HashSet<>();

		return r;
	}

	private static String getFieldAnnotations(final FieldJson field, final TargetType targetType) {
		StringBuilder sb = new StringBuilder();
		if (field.getJsonProperty() != null)
			if (targetType.isJava()) {
				if (field.getType().getName().equalsIgnoreCase("localdatetime") && targetType.isJpa()) {
					sb.append("\t");
					sb.append("@Convert(converter = LocalDateTimeConverter.class)");
					sb.append(newLine);
				}
				if (field.getSubType() != null && field.getSubType() == SubType.ENUM && targetType.isJpa()) {
					sb.append("\t");
					sb.append("@Convert(converter = LocalDateTimeConverter.class)");
					sb.append(newLine);
				}
				sb.append("\t");
				sb.append("@JsonProperty(\"");
				sb.append(field.getJsonProperty());
				sb.append("\")");
				sb.append(newLine);
				if (field.getDeprecated() != null && field.getDeprecated()) {
					sb.append("\t");
					sb.append("@Deprecated");
					sb.append(newLine);
				}
			} else {
				sb.append("\t\t");
				sb.append("[JsonProperty(\"");
				sb.append(field.getJsonProperty());
				sb.append("\")]");
				sb.append(newLine);
				if (field.getDeprecated() != null && field.getDeprecated()) {
					sb.append("\t\t");
					sb.append("[Obsolete()]");
					sb.append(newLine);
				}
			}
		return sb.toString();
	}

	private static String getField(final FieldJson field, final TargetType targetType) {
		StringBuilder sb = new StringBuilder();
		if (targetType.isJava()) {
			sb.append("\t");
			sb.append(field.getAccessModifier());
			sb.append(" ");
			sb.append(field.getType());
			sb.append(" ");
			sb.append(field.getName());
			sb.append(";");
			sb.append(newLine);

		} else {
			sb.append("\t\t");
			sb.append(field.getAccessModifier());
			sb.append(" ");
			if (field.getSubType() != null && field.getSubType() == SubType.ENUM)
				sb.append(field.getType());
			sb.append(" ");
			sb.append(field.getName());
			sb.append("  { get; set; }");
			sb.append(newLine);
		}
		return sb.toString();
	}

	private static String getComment(final FieldJson field, final TargetType targetType) {
		StringBuilder sb = new StringBuilder();
		if (field.getComment() != null && field.getComment().length > 0) {
			sb.append(targetType.isJava() ? "" : "\t\t");
			sb.append(targetType.getCommentStart());
			sb.append(newLine);
			for (String line : field.getComment()) {
				sb.append(targetType.isJava() ? "" : "\t\t");
				sb.append(targetType.getCommentBeginLine());
				sb.append(line);
				sb.append(newLine);
			}
			sb.append(targetType.isJava() ? "" : "\t\t");
			sb.append(targetType.getCommentEnd());
			sb.append(newLine);
		}
		return sb.toString();
	}

	private static String getHeader(final ConfigJson config) {
		StringBuilder sb = new StringBuilder();
		if (config.getHeader() != null && config.getHeader().length > 0)
			for (String line : config.getHeader()) {
				sb.append(line);
				sb.append(newLine);
			}
		return sb.toString();
	}

	private static List<Path> getFileList(final String path) {
		try {
			return Resources.walk(JpaJsonGenerator.class, path,
					f -> f.getFileName().toString().toLowerCase().endsWith(".json"));
		} catch (IOException e) {
			log.error("Error reading input files.", e);
			System.exit(1);
		}
		return null;
	}
}