package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

import static org.assertj.core.api.Assertions.*;

public class JsonNullTest {

    private final JsonValue value = JsonNull.Null;

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonNull.class)
            .verify();
    }

    @Test
    public void testIs() {
        assertThat(value.isNull()).isTrue();
        assertThat(value.isBoolean()).isFalse();
        assertThat(value.isNumber()).isFalse();
        assertThat(value.isString()).isFalse();
        assertThat(value.isArray()).isFalse();
        assertThat(value.isObject()).isFalse();
    }

}
