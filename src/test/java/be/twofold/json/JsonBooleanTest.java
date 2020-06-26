package be.twofold.json;

import nl.jqno.equalsverifier.*;
import org.junit.*;

public class JsonBooleanTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
            .forClass(JsonBoolean.class)
            .verify();
    }

}
