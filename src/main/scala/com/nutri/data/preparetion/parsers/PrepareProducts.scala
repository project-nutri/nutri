package com.nutri.data.preparetion.parsers

import com.nutri.data.preparetion.indexers.{Product, Indexer}



/**
 * Created by katerinaglushchenko on 4/26/15.
 */
object PrepareProducts {
  def parseProduct() = {
    val filePath: String = "src/main/resources/nutr_products_short_with_measures.csv"
    val fileString = io.Source.fromFile(filePath, "UTF-8")
    val rowList = fileString.mkString.split("\n").toList
    val productList: List[Option[Product]] = for {row <- rowList
                                                  itemList = row.split(",")
    } yield
      itemList.toList match {
        case List(title, protein, fat, carbs, calories, glassBig, glassSmall, spoonBig, spoonSmall, item) => Some(Product(title, calories, protein, fat, carbs,
          glassBig, glassSmall, spoonBig, spoonSmall, item))
        case _ => println("error: " + itemList.length + " "); itemList.foreach(println); None
      }
    //    println("success "+productList.mkString(" "))
    productList
  }

  def main(args: Array[String]) {
    val products: List[Option[Product]] = parseProduct()
    println("lenght " + products.length)
    println("lenght " + products.flatMap(p => p).length)
//    val indexer = new Indexer("/Users/katerinaglushchenko/productsNutriShortWithMeasuresStemmed")
    val indexer = new Indexer("/Users/taras-sereda/IdeaProjects/nutri/data/productsNutriShortWithMeasuresStemmed")

    //    val indexer = new Indexer("/Users/katerinaglushchenko/productsNutri2")
    products.flatMap(products => products).map(product => indexer.indexProduct(product))
    indexer.close()

//    val parser = new PrepareReceipt("")
//    parser.runRecieptIndexer()
  }
}

