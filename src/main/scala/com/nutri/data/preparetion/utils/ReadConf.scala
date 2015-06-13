package com.nutri.data.preparetion.utils

import java.net.InetAddress
import com.typesafe.config.ConfigFactory

/**
 * Created by taras-sereda on 5/17/15.
 */

trait ReadConf {
  val computername = InetAddress.getLocalHost().getHostName()
  val config = ConfigFactory.load("server.conf")
  val prefix = if (computername == "Tarass-MacBook-Pro-2.local") "taras" else "katerina"
  val prodNutriLocation = config.getString(s"$prefix.prodNutriLocation")
  val indexDir = config.getString(s"$prefix.indexDir")
}

object Try{
  def main(args: Array[String]) {
    val computername = InetAddress.getLocalHost().getHostName()
    println(computername)
  }
}


