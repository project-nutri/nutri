package com.nutri.api

import com.nutri.api.Main._
import com.nutri.data._
import com.nutri.data.preparetion.parsers.{NutritionInfoDouble, NutritionInfo, FullNutritionInfo, RecipeLine}

/**
 * Created by katerinaglushchenko on 5/18/15.
 */
trait CustomFormats extends DefaultJsonFormats{
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
  implicit val recipeLineFormat = jsonFormat3(RecipeLine)
  implicit val nutritionInfoFormat = jsonFormat4(NutritionInfo)
  implicit val nutritionInfoDoubleFormat = jsonFormat4(NutritionInfoDouble)
  implicit val fullNutritionInfoFormat = jsonFormat2(FullNutritionInfo)
}