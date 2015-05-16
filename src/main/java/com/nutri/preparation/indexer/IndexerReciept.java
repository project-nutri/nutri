package com.nutri.preparation.indexer;

/**
 * Created by katerinaglushchenko on 1/15/15.
 */

import com.nutri.data.preparetion.utils.StemmerAnalyzer;
import com.nutri.preparation.dto.DocumentDto;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;

//import org.apache.lucene.index.IndexWriterConfig;

/**
 * Created by katerinaglushchenko on 1/4/15.
 */
public class IndexerReciept {

    private IndexWriter writer;
    public IndexerReciept(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(new File(indexDir));
        //val config: IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40));

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new StemmerAnalyzer());
        writer = //new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_30,new StandardAnalyzer(Version.LUCENE_30)));
                new IndexWriter(dir,config);

    }

    public void close() throws IOException {
        writer.close();
    }


    public void index(DocumentDto docDto) throws IOException {
        Document doc = new Document();
        doc.add(new Field("ingredients", docDto.getIngredients(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("instructions", docDto.getInstruction(), Field.Store.YES, Field.Index.ANALYZED));
//        doc.add(new Field("tags", docDto.getTags(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("name", docDto.getName(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("category", docDto.getCategory(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("img", docDto.getImg(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("time", docDto.getTime(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("setPortions", docDto.getPortions(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("url", docDto.getUrl(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("calories", docDto.getCalories(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("proteins", docDto.getProteins(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("fats", docDto.getFats(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("carbs", docDto.getCarbs(), Field.Store.YES, Field.Index.ANALYZED));
        writer.addDocument(doc);
        System.out.println("indexed "+writer.numDocs());

    }
}