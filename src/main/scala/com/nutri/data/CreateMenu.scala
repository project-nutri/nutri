package com.nutri.data

import java.io.FileNotFoundException

import akka.actor.{ActorSystem, Props, ActorLogging, Actor}
import akka.util.Timeout
import akka.pattern.ask
import akka.pattern.pipe
import com.nutri.api.{CustomFormats, DefaultJsonFormats}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import spray.json._

/**
 * Created by katerinaglushchenko on 5/4/15.
 */
case class NutritionPersentage(cals:Int, prots:Int, fats:Int, carbs:Int)
case class OneCourse(category: List[String], niProportions: NutritionPersentage, time:Int)

case class MenuStructure(ingredientsQuery: List[IngredientQuery], ni: NutritionQuery, courseList: List[OneCourse])

case class MenuResponse(menu: List[List[Recipe]])

class CreateMenu extends Actor with ActorLogging with CustomFormats{
  implicit val system = ActorSystem("my-system")
  import scala.concurrent.duration._
  implicit val timeout = Timeout(2.seconds)

  implicit def executionContext = system.dispatcher

  val searcher = system.actorOf(Props[SimpleSearch], name = "searcher")

  def createMenu(menuStructure: MenuStructure) = {
    def ni(proportion:NutritionPersentage) = {
      val total = menuStructure.ni
      NutritionQuery((total.calories._1*proportion.cals/100,total.calories._2*proportion.cals/100),
        (total.prots._1*proportion.prots/100,total.prots._2*proportion.prots/100),
        (total.fats._1*proportion.fats/100,total.fats._2*proportion.fats/100),
        (total.carbs._1*proportion.carbs/100,total.carbs._2*proportion.carbs/100))
    }
    val res = (for (course <- menuStructure.courseList)
              yield searcher ? SearchByQuery(RequestQuery(menuStructure.ingredientsQuery, List(),
                    course.category,ni(course.niProportions),course.time))).map(p => p.mapTo[List[Recipe]])
    val a = Future.sequence(res)
    a pipeTo sender()
  }

  def createMenuFromTemplate(path: String) = {
    val filePath: String = s"src/main/resources/menu.templates/$path.json"
    val fileString:String = try {
      io.Source.fromFile(filePath, "UTF-8").mkString
    }catch{
      case e:FileNotFoundException =>
        log.error("file not found in service createMenuFromTemplate"+e.getMessage)
        sender ! Fault("no such template")
        ""
    }

    val menuStructure = menuStructureFormat.read(JsonParser(fileString))
    createMenu(menuStructure)
  }

  override def receive: Receive = {
    case req: MenuStructure =>
      createMenu(req)
    case path: String =>
      createMenuFromTemplate(path)
    case _ => sender ! Fault("no text")
  }
}
