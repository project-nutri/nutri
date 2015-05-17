import com.nutri.data.preparetion.parsers.PrepareReceipt
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created by katerinaglushchenko on 5/16/15.
 */
class CaloriesCounterSpecs extends Specification {
  class DefaultScope extends Scope{
    def calculateCals(path:String)={
      val rec = io.Source.fromFile(path, "UTF-8").mkString
      val parser = new PrepareReceipt(rec)
      val parsedReceipt = parser.parseReceipt
      parser.calculateCaloriesPer100(parsedReceipt.toList)
    }
  }
  "calculate calories" should {
    "count calories in borsch " in new DefaultScope{
      val result = calculateCals("src/test/resources/borsch.txt")//57.7
      result.calories.toDouble should beBetween(30.0,55.0)
    }

    "count calories in ragu " in new DefaultScope {
      val result = calculateCals("src/test/resources/ragu.txt")
      result.calories.toDouble should beBetween(25.0,50.0)
    }

    "count calories in ragu_cal " in new DefaultScope {
      val result = calculateCals("src/test/resources/ragu_cal.txt")
      result.calories.toDouble should beBetween(23.0,26.0)
    }

    "count calories in ragu_40 " in new DefaultScope {
      val result = calculateCals("src/test/resources/ragu_40.txt")
      result.calories.toDouble should beBetween(35.0,45.0)
    }

    "count calories in golubcy_22 " in new DefaultScope {
      val result = calculateCals("src/test/resources/golubcy_22.txt")
      result.calories.toDouble should beBetween(20.0,23.0)
    }

    "count calories in soup " in new DefaultScope {
      val result = calculateCals("src/test/resources/soup.txt")
      result.calories.toDouble should beBetween(20.0,23.0)
    }

  }
}
