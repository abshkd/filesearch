package org.abhishek.dedup;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.openhft.hashing.LongHashFunction;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException {
        long sum;
        Recurse r = new Recurse();
        Files.walkFileTree(Paths.get("G:"), r);
        System.out.println("Total files: " + r.getFilesCount());

        Map entries = r.getEntry();

        entries.forEach((name, hash) -> {
            Set i = r.getKeysByValue(entries, hash);
            if (i.size() > 1) {
                System.out.print(name + ": \t");
                i.forEach(item -> System.out.print(item + ":"));
                System.out.println();
            }
        }
        );

    }

}

class Recurse implements FileVisitor<Path> {

    private long filesCount;
    private final Map entry;
    private static final int offset = 0;
    private static final int length = 204800;

    public Recurse() {
        entry = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        filesCount++;
        if (Files.size(file) > 100000000L) { //100 mb
            FileInputStream fis = new FileInputStream(file.toFile());
            byte[] bs = new byte[204800];
            fis.read(bs, 0, length);

            long hash = LongHashFunction.xx_r39().hashBytes(bs);
            entry.put(file.toAbsolutePath(), hash);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public long getFilesCount() {
        return filesCount;
    }

    public Map getEntry() {
        return entry;
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
