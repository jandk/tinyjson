package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

public class JsonObjectTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonObject.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

}
