package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

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
        assertThatCode(value::asBoolean).doesNotThrowAnyException();
        assertThatIllegalStateException().isThrownBy(value::asNumber);
        assertThatIllegalStateException().isThrownBy(value::asString);
        assertThatIllegalStateException().isThrownBy(value::asArray);
        assertThatIllegalStateException().isThrownBy(value::asObject);
    }

}
