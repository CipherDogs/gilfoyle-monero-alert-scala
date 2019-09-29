package monero

import java.awt.{BorderLayout, Color, Dimension, Image}
import java.awt.event.{WindowAdapter, WindowEvent}
import java.awt.image.BufferedImage
import java.io.BufferedInputStream
import java.util.{Timer, TimerTask}

import javax.imageio.ImageIO
import javax.sound.sampled.AudioSystem
import javax.swing.{ImageIcon, JFrame, JLabel, SwingConstants}
import org.json4s.JsonAST.{JString, JValue}
import org.json4s.jackson.JsonMethods._
import scalaj.http._

class CheckPrice extends TimerTask {
  def run(): Unit = {
    val (price, change) = Monero.getData
    Monero.priceLabel.setText(price + "$")
    if (change < 0.0) Monero.alertAudio()
  }
}

object Monero {
  val back: BufferedImage = ImageIO.read(getClass.getResource("/back.png"))
  val picLabel = new JLabel(new ImageIcon(back.getScaledInstance(500,500,Image.SCALE_FAST)))

  val priceLabel = new JLabel("$", SwingConstants.CENTER)
  priceLabel.setOpaque(true)
  priceLabel.setFont(priceLabel.getFont.deriveFont(44.0f))
  priceLabel.setBackground(Color.BLACK)
  priceLabel.setForeground(new Color(227,29,26))

  val frame = new JFrame("Gilfoyle Monero Alert")
  frame.addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent): Unit = {
      timer.cancel()
      timer.purge()
      System.exit(0)
    }
  })

  val timer = new Timer
  timer.schedule(new CheckPrice, 0, 60000)

  def main(args: Array[String]): Unit = {
    frame.getContentPane.add(picLabel, BorderLayout.CENTER)
    frame.getContentPane.add(priceLabel, BorderLayout.NORTH)
    frame.setSize(new Dimension(500,500))
    frame.setResizable(false)
    frame.setLocationRelativeTo(null)
    frame.setVisible(true)
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

  def alertAudio(): Unit = {
    val is = getClass.getResourceAsStream("/baaah.wav")
    val buf = new BufferedInputStream(is)
    val audio = AudioSystem.getAudioInputStream(buf)
    val clip = AudioSystem.getClip
    clip.open(audio)
    clip.start()
    Thread.sleep(2000)
    clip.stop()
  }
}
