package crawler

import java.io.{InputStreamReader, BufferedReader}
import java.net.URL
import java.nio.charset.Charset
import com.google.common.io.Files
import com.nutri.preparation.dto.DocumentDto
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import sun.net.www.http.HttpClient

import scala.io.Source
import scala.collection.JavaConversions._
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import org.jsoup.Jsoup
//import dispatch.jsoup.JSoupHttp._


import java.net._

/**
 * Created by katerinaglushchenko on 5/10/15.
 */
class TestCrawler {

  def time[T](f: => T): T = {
    val start = System.nanoTime
    val r = f
    val end = System.nanoTime
    val time = (end - start)/1e6
    println("time = " + time +"ms")
    r
  }


  val startPage = "/wiki/Main_Page"
  val linkRegex = """\"/wiki/[a-zA-Z\-_]+\"""".r

//
//  def getLinks(html: String): Set[String] =
//    linkRegex.findAllMatchIn(html).map(_.toString.replace("\"", "")).toSet

  def getHttp(url: String) = {
    val in = Source.fromURL(url, "utf8")
    val response = in.getLines.mkString
    in.close()
    println(response)
    response
  }

  def gethttp2(url:String) ={

    val proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("31.170.178.41", 8080))
    val timeout = 60000
    val conn = new URL(url).openConnection(proxy)
    conn.setRequestProperty("User-Agent" ,"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)

    val inputStream = conn.getInputStream
    val src = Source.fromInputStream(inputStream)(Charset.forName("Windows-1251"))
    src.mkString

  }
  def parseRecipe(url:String) = {
    val ingridientToken = "recipe-ing"
    val webPage = new TestCrawler().gethttp2(url)
    val parsedPage = Jsoup.parse(webPage)
    val links = parsedPage.select("a[href]")
    val allElements = parsedPage.getElementById("print_body")
    val title = allElements.getElementsByTag("h1")(0).text()
    val ingridientsList = parsedPage.getElementsByClass(ingridientToken)(0).getElementsByTag("li")
    val ingridientsStr = ingridientsList.map(item =>  item.text()).mkString("; ")

    val categoryList = allElements.getElementsByClass("recipe-infoline")(0).getElementsByTag("a")
    val category = if (! categoryList.isEmpty()) categoryList(0).text() else throw new NoSuchElementException(s"$title doesn't contain category")


    val instruction = parsedPage.getElementsByClass("recipe-text").text()
    val rTime = allElements.getElementsByTag("time").text()
    val deeshPeaces = allElements.getElementsByClass("recipe-time-peaces").text()
    val portions = if (deeshPeaces.contains("Количество порций: ")) deeshPeaces.split(":").last.trim
    else throw new NoSuchElementException(s"$title doesn't contain setPortions info")

    val imgTag = allElements.getElementsByClass("recipe-img")(0).getElementsByTag("img")(0).attr("src")
    val img = if (!imgTag.equals("http://www.povarenok.ru/images/recipes/1.gif")) imgTag else ""
    var parsedDoc = new DocumentDto

    parsedDoc.setName(title)
    parsedDoc.setIngredients(ingridientsStr)
    parsedDoc.setCategory(category)
    parsedDoc.setInstruction(instruction)
    parsedDoc.setTime(rTime)
    parsedDoc.setPortions(portions)
    parsedDoc.setImg(img)
    parsedDoc.setUrl(url)
    parsedDoc

  }
//
//  val links = getLinks(getHttp(startPage))
//  links.foreach(println)
//  println(links.size)
//
//  val allLinks = time(links.par.flatMap(link => getLinks(getHttp(link))))
//  println(allLinks.size)
}

object TestCrawler{
  def main(args: Array[String]) {
//    new TestCrawler().getHttp("http://ogoloda.li/search#tags=+baranina")
//    new TestCrawler().getHttp("http://www.povarenok.ru")
//    new TestCrawler().gethttp2("http://ogoloda.li/search#tags=+baranina")

//    val doc = Jsoup.connect("http://www.wikiwand.com/en/Amharic").get()
//    val links = doc.select("a[href]")
//
//    for (i <- links) print(i)
    new TestCrawler().parseRecipe("http://www.povarenok.ru/recipes/show/19054/")

  }
}
