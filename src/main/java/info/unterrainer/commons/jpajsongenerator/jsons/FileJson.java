package info.unterrainer.commons.jpajsongenerator.jsons;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode()
public class FileJson {

	private String table;
	private GeneralType extend;
	private GeneralType[] implement;
	private FieldJson[] fields;
}
