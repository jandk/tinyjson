package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class JsonArrayTest {

    private final JsonValue value = new JsonArray(List.of());

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

}
