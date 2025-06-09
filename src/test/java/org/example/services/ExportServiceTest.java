package org.example.services;

import org.example.Storage.NewsArticle;
import org.example.export.Exporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportServiceTest {

    // Простая заглушка Exporter, возвращающая заранее заданный Path
    static class DummyExporter implements Exporter {
        private final Path returned;

        DummyExporter(Path returned) {
            this.returned = returned;
        }

        @Override
        public Path export(String dir, List<NewsArticle> data) {
            // проверяем, что передали именно тот же dir и data
            assertEquals("someDir", dir);
            assertNotNull(data);
            return returned;
        }
    }

    @Test
    void supports_knownAndUnknown() {
        var csv = new DummyExporter(Path.of("csv"));
        var json = new DummyExporter(Path.of("json"));
        var svc = new ExportService(List.of(csv, json));

        // ключи: "dummyexporter" (т.к. оба — один и тот же класс),
        // но поскольку в конструкторе берётся SimpleName.toLowerCase(),
        // ключ будет "dummyexporter"
        assertTrue(svc.supports("dummyexporter"));
        assertFalse(svc.supports("doesNotExist"));
    }

    @Test
    void export_delegatesToCorrectExporter(@TempDir Path temp) throws Exception {
        // создаём два разных класса, чтобы ключи различались
        class AExporter implements Exporter {
            @Override public Path export(String dir, List<NewsArticle> data) {
                return Path.of(dir, "a.out");
            }
        }
        class BExporter implements Exporter {
            @Override public Path export(String dir, List<NewsArticle> data) {
                return Path.of(dir, "b.out");
            }
        }

        var aExp = new AExporter();
        var bExp = new BExporter();
        var svc = new ExportService(List.of(aExp, bExp));

        // ключи — simpleName.toLowerCase(): "aexporter", "bexporter"
        Path pa = svc.export("aexporter", temp.toString(), List.of());
        assertEquals(temp.resolve("a.out"), pa);

        Path pb = svc.export("bexporter", temp.toString(), List.of());
        assertEquals(temp.resolve("b.out"), pb);
    }

    @Test
    void export_unknownFormat_throws() {
        var svc = new ExportService(List.of());
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> svc.export("nope", "any", List.of())
        );
        assertTrue(ex.getMessage().contains("unknown fmt"));
    }
}
