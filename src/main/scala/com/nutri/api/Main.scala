package com.nutri.api

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import com.nutri.data._
import com.nutri.data.preparetion.parsers.{NutritionInfoDouble, FullNutritionInfo, RecipeLine}
import spray.routing.SimpleRoutingApp
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
/**
 * Created by katerinaglushchenko on 4/24/15.
 */
object Main extends App with SimpleRoutingApp with CustomFormats {
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
    pathPrefix("createMenu") {
      pathEnd{
        post {
          entity(as[MenuStructure]) { q =>
            complete {
              (menuCreator ? q).mapTo[List[List[Recipe]]]
                .map(result => result)
              //.recover { case _ => "error"}
            }
          }
        }
      }~
      path(Rest) {name=>
        get {
            complete {
              (menuCreator ? name).mapTo[List[List[Recipe]]]
                .map(result => result)
              //.recover { case _ => "error"}
            }

        }
      }
    }~
    path("ingridientsFromMenu"){
      post{
        entity(as[List[Recipe]]){recipe =>
        complete{
          (searcher ? recipe).mapTo[List[RecipeLine]].map(result=>result)//TODO maybe should take List[List[Recipe]] as menu described?
        }
        }
      }
    }~
    path("describeCaloriesCounting"){
      parameters("forHundred".as[Boolean] ? false) { forHundred =>
        post {
          entity(as[Recipe]) { recipe =>
            complete {
              (searcher ? GetNutritionByRecipe(recipe, forHundred)).mapTo[FullNutritionInfo].map(result => result)
            }
          }
        }
      }
    }~
    path("caloriesForProduct"){
      post{
        entity(as[List[RecipeLine]]){products =>
        complete{
          (searcher ? GetNutritionByProducts(products)).mapTo[List[NutritionInfoDouble]].map(r => r)
          }
        }
      }
    }
}
}