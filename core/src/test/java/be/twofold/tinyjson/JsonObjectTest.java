package be.twofold.tinyjson;

import nl.jqno.equalsverifier.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

public class JsonObjectTest {

    private final JsonValue value = Json.object()
        .add("name", "John Doe")
        .add("age", 42);

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

    @Test
    public void testCopy() {
        JsonValue copy = value.copy();
        assertThat(copy).isNotSameAs(value);
    }

}
