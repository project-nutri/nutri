package com.nutri.persistance

import java.util.Date

import com.datastax.driver.core.querybuilder.{QueryBuilder => QB}

/**
 * Created by katerinaglushchenko on 6/1/15.
 */
//create table users (email varchar primary key, password varchar, name varchar, weight double, height double, date_of_birth timestamp , gender varchar,
// subscription_type varchar, start_subscription timestamp, end_subscription timestamp,
//date_of_registration timestamp, update_date timestamp, delete_date timestamp,
// exception_products varchar, prefer_number_of_calories int, prefer_number_of_eatings int, desirable_weight int);
case class User(email: String, password: String, name: String, weight: Double, height: Double, dateOfBirth: Date, gender: String, subscriptionType: String,
                subscriptionStart: Date, subscriptionEnd: Date, dateOfRegistration: Date, updateDate: Date, deleteDate: Date, exceptionProducts: String, preferNumberOfCalories: Int,
                preferNumberOfEating: Int, desireWeight: Int)

class UsersOperations extends CassandraConector {
  def createUser(user: User) = {
    val statement = QB.insertInto("nutri", "users")
      .value("email", user.email)
      .value("password", user.password.hashCode.toString)
      .value("name", user.name)
      .value("weight", user.weight)
      .value("height", user.height)
      .value("date_of_birth", user.dateOfBirth)
      .value("gender", user.gender)
      .value("subscription_type", user.subscriptionType)
      .value("start_subscription", user.subscriptionStart)
      .value("end_subscription", user.subscriptionEnd)
      .value("date_of_registration", user.dateOfRegistration)
      .value("update_date", user.updateDate)
      .value("delete_date", user.deleteDate)
      .value("exception_products", user.exceptionProducts)
      .value("prefer_number_of_calories", user.preferNumberOfCalories)
      .value("prefer_number_of_eatings", user.preferNumberOfEating)
      .value("desirable_weight", user.desireWeight)
    getSession.execute(statement)
  }

  def findUser(email:String): User={
    val q = QB.select().all().from("nutri","users").where(QB.eq("email",email))
    val r = getSession.execute(q).one
    User(
      r.getString("email"),
      r.getString("password"),
      r.getString("name"),
      r.getDouble("weight"),
      r.getDouble("height"),
      r.getDate("date_of_birth"),
      r.getString("gender"),
      r.getString("subscription_type"),
      r.getDate("start_subscription"),
      r.getDate("end_subscription"),
      r.getDate("date_of_registration"),
      r.getDate("update_date"),
      r.getDate("delete_date"),
      r.getString("exception_products"),
      r.getInt("prefer_number_of_calories"),
      r.getInt("prefer_number_of_eatings"),
      r.getInt("desirable_weight"))
  }
}

object UsersOperations {
  def main(args: Array[String]) {
    val uo = new UsersOperations()
    uo.createUser(
      User("mail", "pass", "name", 45.0, 176.0, new Date(1989, 27, 2), "f", "premium", new Date(2015, 10, 10),
        new Date(2015, 12, 10), new Date(2015, 10, 10), new Date(2015, 10, 10), new Date(205, 10, 10), "", 2000, 3, 55))
//  println(uo.findUser("mail"))
  uo.close
  }

}
