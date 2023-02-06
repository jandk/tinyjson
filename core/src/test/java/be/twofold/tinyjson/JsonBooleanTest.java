package be.twofold.tinyjson;

import nl.jqno.equalsverifier.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

public class JsonBooleanTest {

    private final JsonValue value = JsonBoolean.True;

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonBoolean.class)
            .verify();
    }

    @Test
    public void testIs() {
        assertThat(value.isNull()).isFalse();
        assertThat(value.isBoolean()).isTrue();
        assertThat(value.isNumber()).isFalse();
        assertThat(value.isString()).isFalse();
        assertThat(value.isArray()).isFalse();
        assertThat(value.isObject()).isFalse();
    }

    @Test
    public void testAs() {
        assertThat(value.asBoolean()).isTrue();
        assertThatIllegalStateException().isThrownBy(value::asNumber);
        assertThatIllegalStateException().isThrownBy(value::asString);
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
