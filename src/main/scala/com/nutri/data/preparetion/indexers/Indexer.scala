package com.nutri.data.preparetion.indexers

import java.io.File

import com.nutri.data.preparetion.utils.StemmerAnalyzer
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.{Document, Field}
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.{Directory, FSDirectory}
import org.apache.lucene.util.Version

/**
 * Created by katerinaglushchenko on 4/26/15.
 */
case class Product(name: String, calories: String, protein: String, fat: String, carbs: String,
                   glassBig: String, glassSmall: String, spoonBig: String, spoonSmall: String, item: String)

class Indexer(indexDir: String) {
  val dir: Directory = FSDirectory.open(new File(indexDir))
  //    val writer = new IndexWriter(dir, new RussianLightStemmer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED)
//  val config: IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40))
//  val analyzer:Analyzer = new RussianLightStemmer
  val analyzer:Analyzer = new StemmerAnalyzer//(Version.LUCENE_40)
  val config: IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, analyzer)
  val writer = new IndexWriter(dir, config)

  def close() {
    writer.close()
  }

  def indexProduct(product: Product) {
    val doc: Document = new Document
    doc.add(new Field("name", product.name, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("calories", product.calories, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("carbs", product.carbs, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("fats", product.fat, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("proteins", product.protein, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("glassBig", product.glassBig, Field.Store.YES, Field.Index.ANALYZED)) //250ml
    doc.add(new Field("glassSmall", product.glassSmall, Field.Store.YES, Field.Index.ANALYZED)) //200 ml
    doc.add(new Field("spoonBig", product.spoonBig, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("spoonSmall", product.spoonSmall, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("item", product.item, Field.Store.YES, Field.Index.ANALYZED)) //штук

    writer.addDocument(doc)
    System.out.println("indexed " + writer.numDocs)
  }
}