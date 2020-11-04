package solr;

/**
 *
 * @author abhis_000
 */
public class Item {

    String id;
    String filetype; //can be used for facet search
    Double size;
    String path;
    String filehash;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilehash() {
        return filehash;
    }

    public void setFilehash(String filehash) {
        this.filehash = filehash;
    }

    public Item(String id, Double size, String path) {
        this.id = id;
        this.size = size;
        this.path = path;
    }

}
