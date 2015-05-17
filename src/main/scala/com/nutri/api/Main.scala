package com.nutri.api

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import com.nutri.data._
import spray.routing.SimpleRoutingApp
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
/**
 * Created by katerinaglushchenko on 4/24/15.
 */
object Main extends App with SimpleRoutingApp with DefaultJsonFormats {
  implicit val searchByNameFormat = jsonFormat1(SearchByName)
  implicit val okFormat = jsonFormat1(Ok)
  implicit val faultFormat = jsonFormat1(Fault)
  implicit val receiptFormat = jsonFormat13(Recipe)
  implicit val nutritionQueryFormat = jsonFormat4(NutritionQuery)
  implicit val ingredientQueryFormat = jsonFormat2(IngredientQuery)
  implicit val queryFormat = jsonFormat5(RequestQuery)
  implicit val nutritionPersentageFormat = jsonFormat4(NutritionPersentage)
  implicit val oneCourseFormat = jsonFormat3(OneCourse)
  implicit val menuStructureFormat = jsonFormat3(MenuStructure)
  implicit val menuResponseFormat = jsonFormat1(MenuResponse)
  val conf = ConfigFactory.load("server.conf")
  implicit val system = ActorSystem("my-system", conf)


  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)

  implicit def executionContext = system.dispatcher

  val searcher = system.actorOf(Props[SimpleSearch], name = "searcher")
  val menuCreator = system.actorOf(Props[CreateMenu], name = "menuCreator")

  startServer(interface = "localhost", port = 8080) {
    path("search") {
      post {
        entity(as[RequestQuery]) { q =>
          complete {
            (searcher ? SearchByQuery(q)).mapTo[List[Recipe]]
              .map(result => result)
            //.recover { case _ => "error"}
          }
        }
      }
    }~
    path("createMenu") {
      post {
        entity(as[MenuStructure]) { q =>
          complete {
            (menuCreator ? q).mapTo[List[List[Recipe]]]
              .map(result => result)
            //.recover { case _ => "error"}
          }
        }
      }
    }
  }
}