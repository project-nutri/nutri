package com.nutri.data.preparetion.utils

import java.io.Reader

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents
import org.apache.lucene.analysis.ru.RussianLightStemFilter
import org.apache.lucene.analysis.standard.{StandardTokenizer, StandardFilter}
import org.apache.lucene.util.Version


/**
 * Created by katerinaglushchenko on 5/10/15.
 */
class StemmerAnalyzer extends Analyzer{
  override def createComponents(fieldName: String, reader: Reader): TokenStreamComponents = {
    val tokenizer = new StandardTokenizer(Version.LUCENE_40,reader)
    val filter = new RussianLightStemFilter(new StandardFilter(Version.LUCENE_40,tokenizer))
    new TokenStreamComponents(tokenizer, filter)
  }

//  public TokenStream tokenStream(String fieldName, Reader reader) {
//    TokenStream result = new SynonymFilter(
//      new StopFilter(true,
//        new LowerCaseFilter(
//          new StandardFilter(
//            new StandardTokenizer(
//              Version.LUCENE_30, reader))),
//        StopAnalyzer.ENGLISH_STOP_WORDS_SET),
//      engine );


//  Tokenizer source = new FooTokenizer(reader);
//  TokenStream filter = new FooFilter(source);
//  filter = new BarFilter(filter);
//  return new TokenStreamComponents(source, filter);
}
