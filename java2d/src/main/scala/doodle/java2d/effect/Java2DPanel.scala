/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package java2d
package effect

import java.awt.{Dimension, Graphics, Graphics2D}
import cats.effect.IO
import doodle.algebra.generic.BoundingBox
import doodle.algebra.generic.reified.Reified
import doodle.core.{Transform}
import doodle.java2d.algebra.Java2D
import java.awt.{Dimension, Graphics, Graphics2D}
import java.util.NoSuchElementException
import javax.swing.{JPanel, SwingUtilities}
import scala.concurrent.SyncVar

final class Java2DPanel(frame: Frame) extends JPanel {
  import Java2DPanel.RenderRequest

  private val channel: SyncVar[RenderRequest[_]] = new SyncVar()
  private var lastBoundingBox: BoundingBox = _
  private var lastImage: List[Reified] = _

  frame.background.foreach(color =>
    this.setBackground(Java2D.toAwtColor(color)))

  def resize(width: Double, height: Double): Unit = {
    setPreferredSize(new Dimension(width.toInt, height.toInt))
    SwingUtilities.windowForComponent(this).pack()
  }

  def render[A](request: RenderRequest[A]): Unit = {
    channel.put(request)
    // println("Java2DPanel put in the channel")
    this.repaint()
    // println("Java2DPanel repaint request sent")
  }

  override def paintComponent(context: Graphics): Unit = {
    // println("Java2DPanel painting")
    val gc = context.asInstanceOf[Graphics2D]
    Java2d.setup(gc)

    try {
      val rr = channel.take(10L)
      // println("Java2DPanel got new rr to paint")
      lastBoundingBox = rr.boundingBox
      resize(rr.width, rr.height)
      lastImage = rr.reify.unsafeRunSync()
    } catch {
      case _: NoSuchElementException => ()
    }
    if (lastImage != null) {
      frame.background.foreach(color =>
        gc.setBackground(Java2D.toAwtColor(color)))
      gc.clearRect(0, 0, getWidth, getHeight)
      val tx = Java2d.transform(lastBoundingBox,
                                getWidth.toDouble,
                                getHeight.toDouble,
                                frame.center)
      Java2d.render(gc, lastImage, tx)
    }
  }
}
object Java2DPanel {
  final case class RenderRequest[A](boundingBox: BoundingBox,
                                    width: Double,
                                    height: Double,
                                    renderable: Renderable[A],
                                    cb: Either[Throwable, A] => Unit) {
    def reify: IO[List[Reified]] = {
      IO {
        val (_, fa) = renderable.run(Transform.identity).value
        val (reified, a) = fa.run.value
        cb(Right(a))

        reified
      }
    }
  }
}
