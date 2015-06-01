package com.nutri.persistance

import com.datastax.driver.core.Cluster

/**
 * Created by katerinaglushchenko on 6/1/15.
 */
trait CassandraConector {
  private val cluster = Cluster.builder().addContactPoint("127.0.0.1").build()
//  val session = cluster.connect("nutri")
  def getSession = {
    val session = cluster.connect("nutri")
    session
  }

  def close = cluster.close()

}