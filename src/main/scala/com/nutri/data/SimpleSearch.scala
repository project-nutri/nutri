package com.nutri.data

import java.io.File

import akka.actor.{ActorLogging, Actor}
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
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

case class Recipe(ingredients: List[String],
                  instruction: String = "",
                  tags: List[String] = List(),
                  category: String = "",
                  name: String = "",
                  time: String = "",
                  portions: String = "",
                  url: String = "",
                  img: String = "",
                  calories: String = "",
                  proteins: String = "",
                  carbs: String = "",
                  fats: String = "")

case class IngredientQuery(searchType: String, ingredients: List[String])

case class NutritionQuery(calories: (Int, Int), prots: (Int, Int), fats: (Int, Int), carbs: (Int, Int))

case class RequestQuery(ingredientsQuery: List[IngredientQuery],
                        name: List[String],
                        course: List[String],
                        ni: NutritionQuery,
                        time: Int)

class SimpleSearch extends Actor with ActorLogging {
  def formLuceneStringQuery(query: RequestQuery) = {
    def defDelimiter(searchType: String) = searchType match {
      case "Any" | "Only" => ""
      case "All"  => "+"
      case "None" => "-"
      case _ => ""
    }
    def formFiledQuery(list: List[String], name: String): String = {
      val str = (for (l <- list) yield "+" + l).mkString(" ")
      s"$name: ($str) "
    }

    val ingredients = (for {q <- query.ingredientsQuery
                            delimiter = defDelimiter(q.searchType)
    } yield q.ingredients.map(str => delimiter + str).mkString(" ")).mkString(" ")
    def niQuery(from: Int, to: Int) = {
      if (from == 0 && to == 0) None
      else if (to == 0) Some(s" calories: [$from TO 5000]")
      else Some(s" calories: [$from TO $to]")
    }
    val result = List(if (!ingredients.isEmpty) Some(s"ingredients:($ingredients) ") else None,
      if (query.course.nonEmpty) Some(formFiledQuery(query.course, "category")) else None,
      if (query.name.nonEmpty && query.name.forall(s => !s.isEmpty)) Some(formFiledQuery(query.name, "name")) else None,
      niQuery(query.ni.calories._1, query.ni.calories._2),
      niQuery(query.ni.prots._1, query.ni.prots._2),
      niQuery(query.ni.fats._1, query.ni.fats._2),
      niQuery(query.ni.carbs._1, query.ni.carbs._2),
      if (query.time != 0) Some(s" time: [${query.time - 10} TO ${query.time + 10}]") else None).map(p => p).flatMap(p => p).mkString(" AND ")
    log.debug("formed query " + result)
    result
  }

  def searchByQuery(query: RequestQuery) = {
    val dir: Directory = FSDirectory.open(new File("//Users/katerinaglushchenko/indexPovarenok")) //TODO open once in instance of actor
    val reader: IndexReader = DirectoryReader.open(dir)
    val is: IndexSearcher = new IndexSearcher(reader)
    val parser: QueryParser = new QueryParser(Version.LUCENE_40, "ingredients", new StandardAnalyzer(Version.LUCENE_40))
    val q = formLuceneStringQuery(query)
    val formedQuery = parser.parse(q)
    val hits = is.search(formedQuery, 10)

    def checkDog(doc: Document) = {
      val docLst = doc.get("ingredients").trim.init.split(";")
      val filteredOnly = query.ingredientsQuery.filter(q=>q.searchType=="Only")
      docLst.forall(ingr=>filteredOnly.map(i=>i.ingredients.exists{l=>ingr.contains(l)}).contains(true))
    }

    val res = for {scoreDoc <- hits.scoreDocs
                   doc = is.doc(scoreDoc.doc)
                   if !query.ingredientsQuery.map(i=>i.searchType=="Only").contains(true) || checkDog(doc)
    } yield Recipe(ingredients = List(doc.get("ingredients")),
        category = doc.get("category"),
        name = doc.get("name"),
        time = doc.get("time"),
        portions = doc.get("portions"),
        url = doc.get("url"),
        img = doc.get("img"),
        calories = doc.get("calories"),
        proteins = doc.get("proteins"),
        fats = doc.get("fats"),
        carbs = doc.get("carbs"))

    log.debug(s"found ${res.size} recipe(s)")
    reader.close()
    res.toList
  }

  override def receive: Receive = {
    case SearchByQuery(query) =>
      sender ! searchByQuery(query)
    case _ =>
      sender ! Fault("searcher fault")
  }
}
