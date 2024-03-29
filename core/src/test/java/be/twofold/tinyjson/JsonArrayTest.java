package be.twofold.tinyjson;

import nl.jqno.equalsverifier.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

public class JsonArrayTest {

    private final JsonValue value = Json.array()
        .add("John Doe")
        .add(42);

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonArray.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

    @Test
    public void testIs() {
        assertThat(value.isNull()).isFalse();
        assertThat(value.isBoolean()).isFalse();
        assertThat(value.isNumber()).isFalse();
        assertThat(value.isString()).isFalse();
        assertThat(value.isArray()).isTrue();
        assertThat(value.isObject()).isFalse();
    }

    @Test
    public void testAs() {
        assertThatIllegalStateException().isThrownBy(value::asBoolean);
        assertThatIllegalStateException().isThrownBy(value::asNumber);
        assertThatIllegalStateException().isThrownBy(value::asString);
        assertThat(value.asArray()).isEqualTo(value);
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
        assertThat(copy).isNotSameAs(value);
    }

}
