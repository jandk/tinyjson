package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class JsonObjectTest {

    private final JsonValue value = new JsonObject(Map.of(
        "name", new JsonString("John Doe"),
        "age", new JsonNumber(42)
    ));

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonObject.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

    @Test
    public void testIs() {
        assertThat(value.isNull()).isFalse();
        assertThat(value.isBoolean()).isFalse();
        assertThat(value.isNumber()).isFalse();
        assertThat(value.isString()).isFalse();
        assertThat(value.isArray()).isFalse();
        assertThat(value.isObject()).isTrue();
    }

    @Test
    public void testAs() {
        assertThatIllegalStateException().isThrownBy(value::asBoolean);
        assertThatIllegalStateException().isThrownBy(value::asNumber);
        assertThatIllegalStateException().isThrownBy(value::asString);
        assertThatIllegalStateException().isThrownBy(value::asArray);
        assertThat(value.asObject()).isEqualTo(value);

        assertThatIllegalStateException().isThrownBy(value::asByte);
        assertThatIllegalStateException().isThrownBy(value::asShort);
        assertThatIllegalStateException().isThrownBy(value::asInt);
        assertThatIllegalStateException().isThrownBy(value::asLong);
        assertThatIllegalStateException().isThrownBy(value::asFloat);
        assertThatIllegalStateException().isThrownBy(value::asDouble);
    }

}
