package com.nutri.data.preparetion.indexers


import com.nutri.data.preparetion.parsers.ParsedRecipe
import com.nutri.data.preparetion.utils.StemmerAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import java.io._


/**
 * Created by katerinaglushchenko on 1/4/15.
 */
class IndexerRecipe {
  private var writer: IndexWriter = null

  @throws(classOf[IOException])
  def this(indexDir: String) {
    this()
    val dir: Directory = FSDirectory.open(new File(indexDir))
    val config: IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, new StemmerAnalyzer)
    writer = new IndexWriter(dir, config)
  }

  @throws(classOf[IOException])
  def close {
    writer.close
  }

  @throws(classOf[IOException])
  def index(recipe: ParsedRecipe) {
    val doc: Document = new Document
    doc.add(new Field("ingredients", recipe.ingrediaents, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("instructions", recipe.instruction, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("name", recipe.name, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("category", recipe.category, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("img", recipe.img, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("time", recipe.time, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("setPortions", recipe.portions, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("url", recipe.url, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("calories", recipe.calories, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("proteins", recipe.proteins, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("fats", recipe.fats, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("carbs", recipe.carbs, Field.Store.YES, Field.Index.ANALYZED))
    writer.addDocument(doc)
    System.out.println("indexed " + writer.numDocs)
  }
}