/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solr;

import java.util.concurrent.BlockingQueue;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.SolrClient;
/**
 *
 * @author abhis_000
 */
public class AddDocumentRunner implements Runnable {

    private final SolrInputDocument doc = new SolrInputDocument();
    private final UpdateRequest req = new UpdateRequest();
    private final SolrClient solr;
    private final BlockingQueue<Item> items;

    public AddDocumentRunner(SolrClient solr, BlockingQueue<Item> items) {
        this.solr = solr;
        this.items = items;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
