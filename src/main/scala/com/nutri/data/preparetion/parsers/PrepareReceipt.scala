package com.nutri.data.preparetion.parsers

import java.io.{FileNotFoundException, File}
import java.net.URL
import java.nio.charset.Charset
import java.util.Date

import com.nutri.data.preparetion.indexers.IndexerRecipe
import com.nutri.data.preparetion.utils.{ReadConf, StemmerAnalyzer}
import com.nutri.preparation.ReceiptParser
import org.apache.lucene.index.{DirectoryReader, IndexReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{TopDocs, IndexSearcher}
import org.apache.lucene.store.{FSDirectory, Directory}
import org.apache.lucene.util.Version
import org.jsoup.Jsoup

import scala.io.Source

/**
 * Created by katerinaglushchenko on 5/11/15.
 */
case class RecipeLine(product: String, weight: String, measure: String)

case class NutritionInfo(calories: String, proteins: String, carbs: String, fats: String)

case class ParsedRecipe(name: String,
                        ingredients: String,
                        category: String,
                        instruction: String,
                        time: String,
                        url: String,
                        portions: String,
                        img: String,
                        tags: String = "",
                        calories: String = "",
                        proteins: String = "",
                        fats: String = "",
                        carbs: String = "")

class PrepareReceipt(ingredients: String) extends ReadConf {
  def getHttp(url: String): Option[String] = {
    val timeout = 60000
    try {
      val conn = new URL(url).openConnection()
      conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
      conn.setConnectTimeout(timeout)
      conn.setReadTimeout(timeout)
      val inputStream = conn.getInputStream
      val src = Source.fromInputStream(inputStream)(Charset.forName("Windows-1251"))
      Some(src.mkString)
    } catch {
      case e: FileNotFoundException =>
        println(s"No such url $url")
        None
      case other =>
        println("Oops! " + other.getMessage)
        None
    }
  }

  def parseRecipe(url: String): Option[ParsedRecipe] = {
    import scala.collection.JavaConversions._
    val ingridientToken = "recipe-ing"
    val webPage = getHttp(url)
    if (webPage.isDefined) {
      val parsedPage = Jsoup.parse(webPage.get)
      val links = parsedPage.select("a[href]")
      val allElements = parsedPage.getElementById("print_body")
      val title = allElements.getElementsByTag("h1")(0).text()
      val ingridientsList = parsedPage.getElementsByClass(ingridientToken)(0).getElementsByTag("li")
      val ingridientsStr = ingridientsList.map(item => item.text()).mkString("; ")

      val categoryList = allElements.getElementsByClass("recipe-infoline")(0).getElementsByTag("a")
      val category = if (!categoryList.isEmpty) categoryList(0).text() else return None
      val instruction = parsedPage.getElementsByClass("recipe-text").text()
      val rTime = allElements.getElementsByTag("time").text()
      val dishPeaces = allElements.getElementsByClass("recipe-time-peaces").text()
      val portions = if (dishPeaces.contains("Количество порций: ")) dishPeaces.split(":").last.trim else return None
      val imgTag = allElements.getElementsByClass("recipe-img")(0).getElementsByTag("img")(0).attr("src")
      val img = if (!imgTag.equals("http://www.povarenok.ru/images/recipes/1.gif")) imgTag else return None

      Some(ParsedRecipe(title, ingridientsStr, category, instruction, rTime, url, portions, img))
    }
    else None
  }

  def runRecieptIndexer() = {
    val time: Long = new Date().getTime
    System.out.println("start app")

    val indexer = new IndexerRecipe(indexDir)
    val startPage: Int = 108000
    val endPage: Int = 108010

    for (i <- startPage until endPage) {
      val href: String = "http://www.povarenok.ru/recipes/show/" + i + "/"
      val documentDto = parseRecipe(href)
      if (documentDto.isDefined) {
        val recipe = documentDto.get
        val parser = new PrepareReceipt(recipe.ingredients)
        val parsedReceipt = parser.parseReceipt
        val ni = parser.calculateCalories(parsedReceipt.toList, recipe.portions.toInt)
        val newRecipe = recipe.copy(calories = ni.calories, fats = ni.fats, proteins = ni.proteins, carbs = ni.carbs)
        indexer.index(newRecipe)

      }
      val initRandom = new scala.util.Random()
      val sleepCoef = initRandom.nextFloat()
      val sleepAmount = sleepCoef*5000

      Thread.sleep(sleepAmount.toInt)
      println(s"ZzzzZzzz... for ${sleepAmount.toInt} msec")
    }
    indexer.close
    System.out.println("end " + (new Date().getTime - time))
  }

  def calculateCaloriesPer100(reciept: List[RecipeLine]) = {
    val totalWeight: Double = reciept.foldLeft(0.0)((old, `new`) => old + `new`.weight.toDouble)
    val portions = totalWeight / 100//).toInt
    println(s"total weight $totalWeight, portions - $portions")
    calculateCalories(reciept, portions)
  }

  def calculateCalories(reciept: List[RecipeLine], portions: Double) = {
    def convertToPortion(i: Double) = (i / portions).toString

    val dir: Directory = FSDirectory.open(new File(prodNutriLocation))
    val reader: IndexReader = DirectoryReader.open(dir)
    val is: IndexSearcher = new IndexSearcher(reader)
    val parser: QueryParser = new QueryParser(Version.LUCENE_40, "name", new StemmerAnalyzer())

    def logFind(hits: TopDocs, item: RecipeLine) = {
      val hits = is.search(parser.parse(item.product), 10)
      if (hits.scoreDocs.size > 0)
        true
      else {
        println(s"Not found item ${item.product}")
        false
      }
    }
   //TODO: reduce another call to lucene when looking for coef, save all in one instance
    val res = for {item <- reciept
                   if !item.weight.isEmpty
                   hits = is.search(parser.parse(item.product), 1)
                   if logFind(hits, item)
                   doc = is.doc(hits.scoreDocs(0).doc)
                   coef = findCoef(item.product, item.measure)
                   calories = calculateNutrition(doc.get("calories").toDouble, item.weight.toDouble, coef, "calories")
                   fats = calculateNutrition(doc.get("fats").toDouble, item.weight.toDouble, coef, "fats")
                   carbs = calculateNutrition(doc.get("carbs").toDouble, item.weight.toDouble, coef, "carbs")
                   proteins = calculateNutrition(doc.get("proteins").toDouble, item.weight.toDouble, coef, "proteins")
    } yield {
      println(s"For ${item.product}: ${doc.get("name")} - ${item.weight.toDouble}${item.measure} coef = $coef : ${doc.get("calories")} - $calories, $proteins, $carbs, $fats")
      (calories, proteins, carbs, fats)
    }
    val finalNutriInfo = res.foldLeft((0.0, 0.0, 0.0, 0.0))((a: (Double, Double, Double, Double), b: (Double, Double, Double, Double)) => (a._1 + b._1, a._2 + b._2, a._3 + b._3, a._4 + b._4))
    reader.close()
    val niTotal = NutritionInfo(finalNutriInfo._1.toString, finalNutriInfo._2.toString, finalNutriInfo._3.toString, finalNutriInfo._4.toString)
    println("total NI for reciept" + niTotal)
    val ni = NutritionInfo(convertToPortion(finalNutriInfo._1), convertToPortion(finalNutriInfo._2), convertToPortion(finalNutriInfo._3), convertToPortion(finalNutriInfo._4))
    print("NI for reciept for portion" + ni)
    ni
  }

  // coeficicent - from current measure to gramms
  def calculateNutrition(value: Double, weight: Double, coeficicent: Double, label: String) = {
    println(s"$label $value / 100 * $weight * $coeficicent = " + value / 100 * weight * coeficicent)
    value / 100 * weight * coeficicent
  }

  def findCoefInDb(product: String, measure: String): Double = {

    val dir: Directory = FSDirectory.open(new File(prodNutriLocation))
    val reader: IndexReader = DirectoryReader.open(dir)
    val is: IndexSearcher = new IndexSearcher(reader)
    val parser: QueryParser = new QueryParser(Version.LUCENE_40, "name", new StemmerAnalyzer())
    val q = product
    val formedQuery = parser.parse(q)
    val hits = is.search(formedQuery, 1)
    println("q : " + formedQuery)
    val productName = is.doc(hits.scoreDocs(0).doc).get("name")
    val field = measure match {
      case "st" => Some(("glassBig", 250))
      case "stl" => Some(("spoonBig", 18))
      case "chl" => Some(("spoonSmall", 5))
      case "sht" => Some(("item", 100))
      case _ => None
    }
    if (field.isDefined) {
      if (hits.scoreDocs.size > 0 && is.doc(hits.scoreDocs(0).doc).get(field.get._1) != "0") {
        val foundValue = is.doc(hits.scoreDocs(0).doc).get(field.get._1)
        println(s"for $productName was found coefficient from table $foundValue for $measure")
        foundValue.toDouble
      } else {
        //some abstract default measures
        println(s"$productName was not found in table, was taken some default coefficient value ${field.get._2} for $measure")
        field.get._2
      }
    } else {
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
         p = ReceiptParser.parseReciept(ingredient)} yield RecipeLine(p.get(0), p.get(1), p.get(2))
  }
}

object ParseSite {
  def main(args: Array[String]) {
    val pr = new PrepareReceipt("")
    pr.runRecieptIndexer()
  }
}