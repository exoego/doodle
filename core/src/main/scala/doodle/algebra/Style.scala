/*
 * Copyright 2015 noelwelsh
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
package algebra

import doodle.core.{Cap, Color, Join}

/**
  * Apply styling to a image.
  */
trait Style[F[_]] {
  def fillColor[A](image: F[A], fillColor: Color): F[A]

  def strokeColor[A](image: F[A], strokeColor: Color): F[A]
  def strokeWidth[A](image: F[A], strokeWidth: Double): F[A]
  def strokeCap[A](image: F[A], strokeCap: Cap): F[A]
  def strokeJoin[A](image: F[A], strokeJoin: Join): F[A]

  def noFill[A](image: F[A]): F[A]
  def noStroke[A](image: F[A]): F[A]
}