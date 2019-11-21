package monero

import org.json4s.JsonAST.{JInt, JString}
import org.json4s.jackson.JsonMethods.parse
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalaj.http.{Http, HttpResponse}

object Request {
  def getPrice: Future[(Double, Double)] = Future {
    val response: HttpResponse[String] = Http("https://api.cryptonator.com/api/ticker/xmr-usd").asString
    val json                           = parse(response.body)
    val price: Double = json \ "ticker" \ "price" match {
      case JString(s) => s.toDouble
      case _          => 0.0
    }
    val change: Double = json \ "ticker" \ "change" match {
      case JString(s) => s.toDouble
      case _          => 0.0
    }
    (price, change)
  }

  def getStat: Future[(BigInt, BigInt)] = Future {
    val response: HttpResponse[String] = Http("https://moneroblocks.info/api/get_stats").asString
    val json                           = parse(response.body)
    val height: BigInt = json \ "height" match {
      case JInt(s) => s
      case _       => 0
    }
    val difficulty: BigInt = json \ "difficulty" match {
      case JInt(s) => s
      case _       => 0
    }
    (height, difficulty)
  }
}
