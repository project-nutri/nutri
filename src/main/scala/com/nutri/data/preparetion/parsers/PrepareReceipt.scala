package com.nutri.data.preparetion.parsers

import java.io.File
import java.util.Date

import com.nutri.data.preparetion.utils.StemmerAnalyzer
import com.nutri.preparation.ReceiptParser
import com.nutri.preparation.dto.DocumentDto
import com.nutri.preparation.indexer.IndexerReciept
import com.nutri.preparation.parser.page.SeleniumParserPovarenok
import org.apache.lucene.index.{DirectoryReader, IndexReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.{FSDirectory, Directory}
import org.apache.lucene.util.Version

/**
 * Created by katerinaglushchenko on 5/11/15.
 */
case class ReceiptLine(product: String, weight: String, measure: String)

case class NutritionInfo(calories: String, proteins: String, carbs: String, fats: String)

class PrepareReceipt(ingredients: String) {

  def runRecieptIndexer() = {
    val time: Long = new Date().getTime
    System.out.println("start app")
    val urlParser: SeleniumParserPovarenok = new SeleniumParserPovarenok("http://www.povarenok.ru/recipes/", 3)
    val indexDir: String = "/Users/katerinaglushchenko/indexPovarenok2"
    val indexer: IndexerReciept = new IndexerReciept(indexDir)
    val startPage: Int = 106000
    val endPage: Int = 107415

    for (i <- startPage until endPage) {
      val href: String = "http://www.povarenok.ru/recipes/show/" + i + "/"
      val documentDto: DocumentDto = urlParser.parseDocumentPovarenok(href)
      if (documentDto != null) {
        val parser = new PrepareReceipt(documentDto.getIngredients)
        //        parser.parseReceipt.foreach(println)
        val parsedReceipt = parser.parseReceipt
        val ni = parser.calculateCalories(parsedReceipt.toList,documentDto.getPortions.toInt)
        documentDto.setCalories(ni.calories)
        documentDto.setFats(ni.fats)
        documentDto.setCarbs(ni.carbs)
        documentDto.setProteins(ni.proteins)
        println("ready: "+ documentDto)
        indexer.index(documentDto)
      }
      if(i%20 == 0) {
        print("ZzzzZzzz...")
        Thread.sleep(20000)
      }
    }
    indexer.close()
    System.out.println("end " + (new Date().getTime - time))
  }

  def calculateCalories(reciept: List[ReceiptLine], portions:Int) = {
    def convertToPortion(i:Double) = (i/portions).toString
    val dir: Directory = FSDirectory.open(new File("/Users/katerinaglushchenko/productsNutriShortWithMeasuresStemmed"))
    val reader:IndexReader = DirectoryReader.open(dir)
    val is: IndexSearcher = new IndexSearcher(reader)
    val parser: QueryParser = new QueryParser(Version.LUCENE_40, "name", new StemmerAnalyzer())
    val res = for {item <- reciept
                   if !item.weight.isEmpty
                   hits = is.search(parser.parse(item.product), 10)
                   if hits.scoreDocs.size > 0
                   doc = is.doc(hits.scoreDocs(0).doc)
                   coef = findCoef(item.product, item.measure)
                   calories = calculateNutrition(doc.get("calories").toDouble, item.weight.toDouble, coef)
                   fats = calculateNutrition(doc.get("fats").toDouble, item.weight.toDouble, coef)
                   carbs = calculateNutrition(doc.get("carbs").toDouble, item.weight.toDouble, coef)
                   proteins = calculateNutrition(doc.get("proteins").toDouble, item.weight.toDouble, coef)
    } yield {println(s"${doc.get("name")} - ${item.weight.toDouble}${item.measure}: ${doc.get("calories")} - $calories, $proteins, $carbs, $fats");(calories,proteins,carbs,fats)}
    val finalNutriInfo = res.foldLeft((0.0,0.0,0.0,0.0))((a:(Double,Double, Double, Double),b:(Double,Double, Double, Double))=>((a._1+b._1, a._2 +b._2,a._3 + b._3, a._4 +b._4)))
    reader.close()
    val ni = NutritionInfo(convertToPortion(finalNutriInfo._1),convertToPortion(finalNutriInfo._2),convertToPortion(finalNutriInfo._3),convertToPortion(finalNutriInfo._4))
    print("total NI for reciept"+ni)
    ni
  }

  // coeficicent - from current measure to gramms
  def calculateNutrition(value: Double, weight: Double, coeficicent: Double) = {
    println(s"$value / 100 * $weight * $coeficicent = "+ value / 100 * weight * coeficicent)
    value / 100 * weight * coeficicent
  }

  def findCoefInDb(product: String, measure: String): Double = {
    val dirPath = "/Users/katerinaglushchenko/productsNutriShortWithMeasuresStemmed"
    val dir: Directory = FSDirectory.open(new File(dirPath))
    val reader:IndexReader = DirectoryReader.open(dir)
    val is: IndexSearcher = new IndexSearcher(reader)
    val parser: QueryParser = new QueryParser(Version.LUCENE_40, "name", new StemmerAnalyzer())
    val q = product
    val formedQuery = parser.parse(q)
    val hits = is.search(formedQuery, 1)
    println("q : " + formedQuery)
    val productName = is.doc(hits.scoreDocs(0).doc).get("name")
    //    reader.close()
    val field = measure match {
      case "st" => Some(("glassBig",250))
      case "stl" => Some(("spoonBig",18))
      case "chl" => Some(("spoonSmall",5))
      case "sht" => Some(("item",100))
      case _ => None
    }
    if (field.isDefined) {
      if (hits.scoreDocs.size > 0 && is.doc(hits.scoreDocs(0).doc).get(field.get._1)!="0") {
        val foundValue = is.doc(hits.scoreDocs(0).doc).get(field.get._1)
        println(s"for $productName was found coefficient from table $foundValue for $measure" )
        foundValue.toDouble
      }else{
      //some abstract default measures
      println(s"$productName was not found in table, was taken some default coefficient value ${field.get._2} for $measure" )
      field.get._2
      }
    }else{
      println(s"for $productName has unknown measure - $measure 100 was taken as coefficient")
      100
    }
  }

  def findCoef(product: String, measure: String): Double = {
    println(product)
    measure match {
      case "kg" => 0.001
      case "mg" => 1000.0
      case "ml" => 1.0
      case "g" => 1.0
      case _ =>
        findCoefInDb(product, measure)
    }
  }

  def parseReceipt = {
    for {ingredient <- ingredients.split("\n")
         p = ReceiptParser.parseReciept(ingredient)} yield ReceiptLine(p.get(0), p.get(1), p.get(2))
 }
}

object testCalloriesCounter {
  def main(args: Array[String]) {
    val pr = new PrepareReceipt("")
    pr.runRecieptIndexer()
//
//    val rec = io.Source.fromFile("src/main/resources/rec2.txt", "UTF-8").mkString
//    val parser = new PrepareReceipt(rec)
//    //    parser.parseReceipt.foreach(println)
//    val parsedReceipt = parser.parseReceipt //.foreach(println)
//    parser.calculateCalories(parsedReceipt.toList,1)
  }
}