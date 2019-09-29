package monero

import java.awt.{Dimension, Image}
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JFrame, JLabel, WindowConstants}
import org.json4s.JsonAST.{JString, JValue}
import org.json4s.jackson.JsonMethods._
import scalaj.http._

object Monero {
  def main(args: Array[String]): Unit = {

    gui()

    val (price, change) = getData

    println(s"Price: $price")
    if (change < 0.0) {
      println("Alert!")
    }
  }

  def request(): JValue = {
    val response: HttpResponse[String] = Http("https://api.cryptonator.com/api/ticker/xmr-usd").asString
    parse(response.body)
  }

  def getData: (Double, Double) = {
    val json = request()

    val price: Double = json \ "ticker" \ "price" match {
      case JString(s) => s.toDouble
      case _ => 0
    }

    val change: Double = json \ "ticker" \ "change" match {
      case JString(s) => s.toDouble
      case _ => 0.0
    }

    (price, change)
  }

  def gui(): Unit = {

    val back = ImageIO.read(getClass.getResource("/back.png"))
    val picLabel = new JLabel(new ImageIcon(back.getScaledInstance(500,500,Image.SCALE_FAST)))

    val frame = new JFrame("Gilfoyle Monero Alert")
    frame.getContentPane.add(picLabel)
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setSize(new Dimension(500,500))
    frame.setResizable(false)
    frame.setLocationRelativeTo(null)
    frame.setVisible(true)
  }
}
