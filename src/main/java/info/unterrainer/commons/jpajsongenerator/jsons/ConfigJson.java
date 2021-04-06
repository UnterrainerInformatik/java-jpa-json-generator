package info.unterrainer.commons.jpajsongenerator.jsons;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode()
public class ConfigJson {

	private String[] header;
	private String sourceDir;
	private ConversionJson[] conversions;
}