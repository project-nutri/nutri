package com.nutri.data

import java.io.File

import akka.actor.{ActorLogging, Actor}
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{DirectoryReader, IndexReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{TopDocs, Query, IndexSearcher}
import org.apache.lucene.store.{FSDirectory, Directory}
import org.apache.lucene.util.Version


/**
 * Created by katerinaglushchenko on 4/24/15.
 */
case class SearchByName(name: String)

case class SearchByList(name: List[String])

case class SearchByQuery(query: RequestQuery)

case class Ok(name: String)

case class Fault(name: String)

case class Receipt(ingredients: List[String],
                   instruction: String = "",
                   tags: List[String] = List(),
                   category: String = "",
                   name: String = "",
                   time: String = "",
                   portions: String = "",
                   url: String = "",
                   img: String = "",
                    calories: String ="",
                    proteins:String = "",
                    carbs:String = "",
                    fats:String = "")

case class IngredientQuery(searchType: String, ingredients: List[String])

case class RequestQuery(ingredientsQuery: List[IngredientQuery],
                        name: List[String],
                        course: List[String],
                        calories:Int)

class SimpleSearch extends Actor with ActorLogging {
  def formLuceneStringQuery(query: RequestQuery) = {
    def defDelimiter(searchType: String) = searchType match {
      case "Any" => ""
      case "All" => "+"
      case "None" => "-"
      case _ => ""
    }
    def formFiledQuery(list: List[String], name: String): String = {
     val str = (for(l <- list) yield "+"+l).mkString(" ")
      s"$name: ($str) "
    }
    val ingredients = (for {q <- query.ingredientsQuery
                            delimiter = defDelimiter(q.searchType)
    } yield q.ingredients.map(str => delimiter + str).mkString(" ")).mkString(" ")

    val result = List(if (!ingredients.isEmpty) Some(s"ingredients:($ingredients) ") else None,
      if (query.course.nonEmpty) Some(formFiledQuery(query.course, "category")) else None,
      if (query.name.nonEmpty) Some(formFiledQuery(query.name, "name")) else None,
      if (query.calories!=0) Some(s" calories: [${query.calories - 50} TO ${query.calories + 50}]") else None).map(p=>p).flatMap(p => p).mkString(" AND ")

    println("compose ingr " + result)
    result
  }

  def searchByQuery(query: RequestQuery): List[Receipt] = {
    val dir: Directory = FSDirectory.open(new File("//Users/katerinaglushchenko/indexPovarenok"))
//    println(query)
    log.debug(query.toString)
    val reader:IndexReader = DirectoryReader.open(dir)
    val is: IndexSearcher = new IndexSearcher(reader)
    val parser: QueryParser = new QueryParser(Version.LUCENE_40, "ingredients", new StandardAnalyzer(Version.LUCENE_40))
    val q = formLuceneStringQuery(query)
    val formedQuery = parser.parse(q)
    val hits = is.search(formedQuery, 10)
    val res = for {scoreDoc <- hits.scoreDocs
                   doc = is.doc(scoreDoc.doc)
    } yield Receipt(ingredients = List(doc.get("ingredients")),
//        instruction = doc.get("instructions"),
        category = doc.get("category"),
        name = doc.get("name"),
        time = doc.get("time"),
        portions = doc.get("setPortions"),
        url = doc.get("url"),
        img = doc.get("img"),
      calories = doc.get("calories"),
      proteins = doc.get("proteins"),
      fats = doc.get("fats"),
      carbs = doc.get("carbs"))

    println(res.length)
    reader.close()
    res.toList
  }

  override def receive: Receive = {
    case SearchByQuery(query) =>
      sender ! searchByQuery(query)
    case _ => sender ! Fault("searcher fault")
  }
}
