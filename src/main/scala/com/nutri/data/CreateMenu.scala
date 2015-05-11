package com.nutri.data

import akka.actor.{ActorSystem, Props, ActorLogging, Actor}
import akka.util.Timeout
import akka.pattern.ask
import akka.pattern.pipe
import scala.concurrent.Future
import scala.util.{Failure, Success}


/**
 * Created by katerinaglushchenko on 5/4/15.
 */
case class OneCourse(category: List[String], percentageCalories: Int)

case class MenuStructure(ingredientsQuery: List[IngredientQuery], totalCalories: Int, courseList: List[OneCourse])

case class MenuResponse(menu: List[List[Receipt]])

//case class CreateMenuRequest()

class CreateMenu extends Actor with ActorLogging {
  implicit val system = ActorSystem("my-system")
  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)

  implicit def executionContext = system.dispatcher

  val searcher = system.actorOf(Props[SimpleSearch], name = "searcher")

  def createMenu(menuStructure: MenuStructure) = {
    val res = (for(course <- menuStructure.courseList) yield searcher ? SearchByQuery(RequestQuery(menuStructure.ingredientsQuery,List(),
        course.category,menuStructure.totalCalories*course.percentageCalories/100))).map(p=>p.mapTo[List[Receipt]])
    val a = Future.sequence(res)
    a pipeTo sender()
 //   res pipeTo sender
  }

  override def receive: Receive = {
    case req: MenuStructure =>
      createMenu(req)
    case _ => sender ! Fault("no text")
  }
}
