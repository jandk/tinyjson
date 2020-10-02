package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

import static org.assertj.core.api.Assertions.*;

public class JsonNumberTest {

    private final JsonValue value = new JsonNumber(1);

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonNumber.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

    @Test
    public void testIs() {
        assertThat(value.isNull()).isFalse();
        assertThat(value.isBoolean()).isFalse();
        assertThat(value.isNumber()).isTrue();
        assertThat(value.isString()).isFalse();
        assertThat(value.isArray()).isFalse();
        assertThat(value.isObject()).isFalse();
    }

    @Test
    public void testAs() {
        assertThatIllegalStateException().isThrownBy(value::asBoolean);
        assertThat(value.asNumber()).isEqualTo(1);
        assertThatIllegalStateException().isThrownBy(value::asString);
        assertThatIllegalStateException().isThrownBy(value::asArray);
        assertThatIllegalStateException().isThrownBy(value::asObject);

        assertThat(value.asByte()).isEqualTo((byte) 1);
        assertThat(value.asShort()).isEqualTo((short) 1);
        assertThat(value.asInt()).isEqualTo(1);
        assertThat(value.asLong()).isEqualTo(1);
        assertThat(value.asFloat()).isEqualTo(1);
        assertThat(value.asDouble()).isEqualTo(1);
    }

}
