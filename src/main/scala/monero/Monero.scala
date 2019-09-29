package monero

import org.json4s.JsonAST.{JString, JValue}
import org.json4s.jackson.JsonMethods._
import scalaj.http._

object Monero {
  def main(args: Array[String]): Unit = {
    val json = request()

    val price = json \ "ticker" \ "price" match {
      case JString(s) => s.toInt
      case _ => 0
    }

    val change: Double = json \ "ticker" \ "change" match {
      case JString(s) => s.toDouble
      case _ => 0.0
    }

    println(s"Price: $price")
    if (change < 0.0) {
      println("Alert!")
    }
  }

  def request(): JValue = {
    val response: HttpResponse[String] = Http("https://api.cryptonator.com/api/ticker/xmr-usd").asString
    parse(response.body)
  }
}
