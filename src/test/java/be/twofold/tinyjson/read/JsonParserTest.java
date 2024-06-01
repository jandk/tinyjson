package be.twofold.tinyjson.read;

import be.twofold.tinyjson.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

public class JsonParserTest {

    private static final Path Root = Paths.get("src/test/resources/JSONTestSuite/parsing");

    public static List<String> getFileNames() throws Exception {
        return Files.list(Root)
            .filter(path -> path.getFileName().toString().endsWith(".json"))
            .map(path -> path.getFileName().toString())
            .collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("getFileNames")
    public void test(String filename) {
        String type = filename.substring(0, 2);
        switch (type) {
            case "i_":
                testUndetermined(filename);
                break;
            case "n_":
                testForFailure(filename);
                break;
            case "y_":
                testForSuccess(filename);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void testForFailure(String filename) {
        assertThatExceptionOfType(JsonException.class)
            .isThrownBy(() -> parseFile(filename));
    }

    private void testForSuccess(String filename) {
        assertThatCode(() -> parseFile(filename))
            .doesNotThrowAnyException();
    }

    private void testUndetermined(String filename) {
        try {
            parseFile(filename);
        } catch (JsonException e) {
            System.err.println("F -- " + filename + " -- " + getRootCause(e).getClass().getSimpleName());
        }
        System.out.println("P -- " + filename);
    }

    private static void parseFile(String filename) {
        try (Reader reader = Files.newBufferedReader(Root.resolve(filename))) {
            Json.parse(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Throwable getRootCause(Throwable throwable) {
        List<Throwable> list = new ArrayList<>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

}
