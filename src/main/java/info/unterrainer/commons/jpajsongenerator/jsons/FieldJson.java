package info.unterrainer.commons.jpajsongenerator.jsons;

import info.unterrainer.commons.jpajsongenerator.enums.AccessModifier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode()
public class FieldJson {

	private String[] comment;
	private AccessModifier accessModifier;
	private GeneralType type;
	private String name;
	private String jsonProperty;
}
