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
package algebra

import doodle.core.{Cap, Color, Join, PathElement, Point, Transform => Tx}
import doodle.algebra.generic.{DrawingContext, Stroke, Fill}
import java.awt.{Color => AwtColor, BasicStroke, Graphics2D}
// import java.awt.image.BufferedImage
import java.awt.geom.{AffineTransform, Path2D}
import java.awt.geom.Path2D

/** Various utilities for using Java2D */
object Java2D {
  // def fontMetrics(graphics: Graphics2D): Metrics =
  //   FontMetrics(graphics.getFontRenderContext()).boundingBox _

  // val bufferFontMetrics: Metrics = {
  //   val buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
  //   val graphics = this.setup(buffer.createGraphics())

  //   fontMetrics(graphics)
  // }

  def toAwtColor(color: Color): AwtColor = {
    val rgba = color.toRGBA
    new AwtColor(rgba.r.get, rgba.g.get, rgba.b.get, rgba.a.toUnsignedByte.get)
  }

  def setStroke(graphics: Graphics2D, stroke: Stroke) = {
    val width = stroke.width.toFloat
    val cap = stroke.cap match {
      case Cap.Butt   => BasicStroke.CAP_BUTT
      case Cap.Round  => BasicStroke.CAP_ROUND
      case Cap.Square => BasicStroke.CAP_SQUARE
    }
    val join = stroke.join match {
      case Join.Bevel => BasicStroke.JOIN_BEVEL
      case Join.Miter => BasicStroke.JOIN_MITER
      case Join.Round => BasicStroke.JOIN_ROUND
    }
    val jStroke =
      new BasicStroke(width, cap, join)
    val jColor = Java2D.toAwtColor(stroke.color)

    graphics.setStroke(jStroke)
    graphics.setColor(jColor)
  }

  def setFill(graphics: Graphics2D, fill: Fill) = {
    graphics.setColor(Java2D.toAwtColor(fill.color))
  }

  /** Converts to an *open* `Path2D` */
  def toPath2D(elements: List[PathElement]): Path2D = {
    import PathElement._
    import Point.extractors._

    val path = new Path2D.Double()
    path.moveTo(0, 0)
    // path.moveTo(origin.x, origin.y)
    elements.foreach {
      case MoveTo(Cartesian(x, y)) =>
        path.moveTo(x, y)
      case LineTo(Cartesian(x, y)) =>
        path.lineTo(x, y)

      case BezierCurveTo(Cartesian(cp1x, cp1y),
                         Cartesian(cp2x, cp2y),
                         Cartesian(endX, endY)) =>
        path.curveTo(
          cp1x,
          cp1y,
          cp2x,
          cp2y,
          endX,
          endY
        )
    }
    path
  }

  def toAffineTransform(transform: Tx): AffineTransform = {
    val elts = transform.elements
    new AffineTransform(elts(0), elts(3), elts(1), elts(4), elts(2), elts(5))
  }

  def withTransform(graphics: Graphics2D, transform: Tx)(f: => Unit): Unit = {
    val original = graphics.getTransform()
    graphics.transform(toAffineTransform(transform))
    f
    graphics.setTransform(original)
  }

  def strokeAndFill(graphics: Graphics2D,
                    path: Path2D,
                    current: DrawingContext): Unit = {
    current.stroke.foreach { s =>
      setStroke(graphics, s)
      graphics.draw(path)
    }
    current.fill.foreach { f =>
      setFill(graphics, f)
      graphics.fill(path)
    }
  }

}
