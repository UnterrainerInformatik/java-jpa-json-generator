package info.unterrainer.commons.jpajsongenerator.jsons;

import info.unterrainer.commons.jpajsongenerator.enums.TargetType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode()
public class ConversionJson {

	private TargetType targetType;
	private String sourceDir;
	private String targetDir;
	private String namespace;
	private GeneralType[] classAnnotations;
	private GeneralType extend;
	private GeneralType[] implement;
}
