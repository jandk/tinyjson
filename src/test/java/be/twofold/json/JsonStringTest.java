package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

import static org.assertj.core.api.Assertions.*;

public class JsonStringTest {

    private final JsonValue value = new JsonString("");

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

}
