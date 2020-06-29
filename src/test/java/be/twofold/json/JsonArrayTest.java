package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class JsonArrayTest {

    private final JsonValue value = new JsonArray(List.of(
        new JsonString("John Doe"),
        new JsonNumber(42)
    ));

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
    }

}
