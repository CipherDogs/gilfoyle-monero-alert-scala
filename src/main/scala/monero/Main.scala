package monero

import java.awt.{BorderLayout, Color, Dimension, Image}
import java.awt.event.{WindowAdapter, WindowEvent}
import java.awt.image.BufferedImage
import java.io.BufferedInputStream

import javax.imageio.ImageIO
import javax.sound.sampled.AudioSystem
import javax.swing.{ImageIcon, JFrame, JLabel, JPanel, SwingConstants}
import monix.execution.Scheduler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App {
  val back: BufferedImage = ImageIO.read(getClass.getResource("/back.png"))
  val picLabel            = new JLabel(new ImageIcon(back.getScaledInstance(500, 500, Image.SCALE_FAST)))

  val priceLabel = new JLabel("<html>Price<br>$</html>", SwingConstants.LEFT)
  priceLabel.setOpaque(true)
  priceLabel.setFont(priceLabel.getFont.deriveFont(24.0f))
  priceLabel.setBackground(Color.BLACK)
  priceLabel.setForeground(new Color(227, 29, 26))

  val statLabel = new JLabel("<html>Height<br>0<br><br>Difficulty<br>0</html>", SwingConstants.LEFT)
  statLabel.setOpaque(true)
  statLabel.setFont(priceLabel.getFont.deriveFont(24.0f))
  statLabel.setBackground(Color.BLACK)
  statLabel.setForeground(new Color(227, 29, 26))

  val panel = new JPanel
  panel.setBackground(Color.BLACK)
  panel.setLayout(new BorderLayout)
  panel.add(priceLabel, BorderLayout.NORTH)
  panel.add(statLabel, BorderLayout.SOUTH)
  panel.setPreferredSize(new Dimension(200, 500))

  val frame = new JFrame("Gilfoyle Monero Alert")
  frame.addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent): Unit = {
      scheduler.shutdown()
      System.exit(0)
    }
  })
  frame.getContentPane.add(picLabel, BorderLayout.CENTER)
  frame.getContentPane.add(panel, BorderLayout.EAST)
  frame.setSize(new Dimension(700, 500))
  frame.setResizable(false)
  frame.setLocationRelativeTo(null)
  frame.setVisible(true)

  val scheduler = Scheduler.io("Gilfoyle")
  scheduler.scheduleAtFixedRate(0.seconds, 60.seconds) {
    for {
      dataPrice <- Request.getPrice
      dataStat  <- Request.getStat
    } {
      val (price, change) = dataPrice
      priceLabel.setText("<html>Price<br>" + price.toFloat + "$</html>")
      if (change < 0.0) alertAudio()

      val (height, difficulty) = dataStat
      statLabel.setText("<html>Height<br>" + height.toString + "<br><br>Difficulty<br>" + difficulty.toString + "</html>")
    }
  }

  def alertAudio(): Unit = {
    val is    = getClass.getResourceAsStream("/baaah.wav")
    val buf   = new BufferedInputStream(is)
    val audio = AudioSystem.getAudioInputStream(buf)
    val clip  = AudioSystem.getClip
    clip.open(audio)
    clip.start()
    Thread.sleep(2000)
    clip.stop()
  }
}
