package monero

import java.awt.{BorderLayout, Color, Dimension, Image}
import java.awt.event.{WindowAdapter, WindowEvent}
import java.awt.image.BufferedImage
import java.io.BufferedInputStream
import java.util.{Timer, TimerTask}
import javax.imageio.ImageIO
import javax.sound.sampled.AudioSystem
import javax.swing.{ImageIcon, JFrame, JLabel, JPanel, SwingConstants}
import scala.concurrent.ExecutionContext.Implicits.global

class CheckPrice extends TimerTask {
  def run(): Unit = {
    Request.getPrice.onComplete(data => {
      val (price, change) = if (data.isSuccess) data.get else (0.0, 0.0)
      Main.priceLabel.setText("<html>Price<br>" + price.toFloat + "$</html>")
      if (change < 0.0) Main.alertAudio()
    })

    Request.getStat.onComplete(data => {
      val (height, difficulty) = if (data.isSuccess) data.get else (0, 0)
      Main.statLabel.setText("<html>Height<br>" + height.toString + "<br><br>Difficulty<br>" + difficulty.toString + "</html>")
    })
  }
}

object Main extends App {
  val back: BufferedImage = ImageIO.read(getClass.getResource("/back.png"))
  val picLabel = new JLabel(new ImageIcon(back.getScaledInstance(500,500,Image.SCALE_FAST)))

  val priceLabel = new JLabel("<html>Price<br>$</html>", SwingConstants.LEFT)
  priceLabel.setOpaque(true)
  priceLabel.setFont(priceLabel.getFont.deriveFont(24.0f))
  priceLabel.setBackground(Color.BLACK)
  priceLabel.setForeground(new Color(227,29,26))

  val statLabel = new JLabel("<html>Height<br>0<br><br>Difficulty<br>0</html>", SwingConstants.LEFT)
  statLabel.setOpaque(true)
  statLabel.setFont(priceLabel.getFont.deriveFont(24.0f))
  statLabel.setBackground(Color.BLACK)
  statLabel.setForeground(new Color(227,29,26))

  val panel = new JPanel
  panel.setBackground(Color.BLACK)
  panel.setLayout(new BorderLayout)
  panel.add(priceLabel, BorderLayout.NORTH)
  panel.add(statLabel, BorderLayout.SOUTH)
  panel.setPreferredSize(new Dimension(200,500))

  val frame = new JFrame("Gilfoyle Monero Alert")
  frame.addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent): Unit = {
      timer.cancel()
      timer.purge()
      System.exit(0)
    }
  })
  frame.getContentPane.add(picLabel, BorderLayout.CENTER)
  frame.getContentPane.add(panel, BorderLayout.EAST)
  frame.setSize(new Dimension(700,500))
  frame.setResizable(false)
  frame.setLocationRelativeTo(null)
  frame.setVisible(true)

  val timer = new Timer
  timer.schedule(new CheckPrice, 0, 60000)

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
