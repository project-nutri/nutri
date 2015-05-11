package crawler

import java.io.{InputStreamReader, BufferedReader}
import java.net.URL
import java.nio.charset.Charset
import com.google.common.io.Files

import scala.io.Source

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

//  val domain = "http://en.wikipedia.org"
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
  val timeout = 60000
    val conn = new URL(url).openConnection()
    conn.setRequestProperty("User-Agent" ,"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)
    val inputStream = conn.getInputStream

    val src = Source.fromInputStream(inputStream)(Charset.forName("Windows-1251"))
    println(s"$url 2")
    println(src.mkString) }
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
    new TestCrawler().gethttp2("http://www.povarenok.ru")
  }
}
