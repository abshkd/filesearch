package org.abhishek.dedup;

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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 * Build a Solr Index of Files in any filesystem for fast text search
 *
 */
public class Index {

    public static void main(String[] args) throws IOException {
        String usage
                = "Usage:\tjava - jar dedup.jar [-index dir] \n\nSee https://github.com/abshkd for details.";
        if (args.length == 0 || ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }
        long startTime = System.nanoTime();
        String index = "/";
        for (int i = 0; i < args.length; i++) {
            if ("-index".equals(args[i])) {
                index = args[i + 1];
                i++;
            }
        }
        String urlString = "http://localhost:8983/solr/files";
        SolrClient solr = new HttpSolrClient.Builder(urlString).build();
        long sum;
        Recurse r = new Recurse();
        Files.walkFileTree(Paths.get(index), r);
        sum = r.getFilesCount();
        System.out.println("Total files: " + sum);

        Map entries = r.getEntry();

        entries.forEach((name, size) -> {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", MD5(name.toString()));
            document.addField("path", name.toString());
            document.addField("size", size);
            try {
                UpdateResponse response = solr.add(document);
            } catch (SolrServerException | IOException ex) {
                Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        );
        long estimatedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        System.out.println("Completed in " + estimatedTime + " ms");
    }

    /* As found on StackOverflow.
    * Given a path string compute MD5.
    * Not ideal for File MD5
     */
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
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
        /* It may be easier to build index right here to improve BigO performance
        * as well as avoid using complex HashMaps.
        * 
         */
        filesCount++;
        entry.put(file.toAbsolutePath(), Files.size(file));
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
