package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

import static org.assertj.core.api.Assertions.*;

public class JsonStringTest {

    private final JsonValue value = new JsonString("foo");

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonString.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

    @Test
    public void testIs() {
        assertThat(value.isNull()).isFalse();
        assertThat(value.isBoolean()).isFalse();
        assertThat(value.isNumber()).isFalse();
        assertThat(value.isString()).isTrue();
        assertThat(value.isArray()).isFalse();
        assertThat(value.isObject()).isFalse();
    }

    @Test
    public void testAs() {
        assertThatIllegalStateException().isThrownBy(value::asBoolean);
        assertThatIllegalStateException().isThrownBy(value::asNumber);
        assertThat(value.asString()).isEqualTo("foo");
        assertThatIllegalStateException().isThrownBy(value::asArray);
        assertThatIllegalStateException().isThrownBy(value::asObject);

        assertThatIllegalStateException().isThrownBy(value::asByte);
        assertThatIllegalStateException().isThrownBy(value::asShort);
        assertThatIllegalStateException().isThrownBy(value::asInt);
        assertThatIllegalStateException().isThrownBy(value::asLong);
        assertThatIllegalStateException().isThrownBy(value::asFloat);
        assertThatIllegalStateException().isThrownBy(value::asDouble);
    }

    @Test
    public void testCopy() {
        JsonValue copy = value.copy();
        assertThat(copy).isSameAs(value);
    }
}
