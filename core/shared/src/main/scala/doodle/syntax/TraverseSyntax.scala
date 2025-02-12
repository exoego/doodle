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
package syntax

import cats.Traverse
import cats.instances.unit._
import doodle.algebra.{Layout, Shape}

trait TraverseSyntax {
  implicit class TraverseOps[T[_], F[_]](val t: T[F[Unit]]) {
    def allOn(implicit layout: Layout[F],
              shape: Shape[F],
              traverse: Traverse[T]): F[Unit] =
      traverse.foldLeft(t, shape.empty) { (accum, img) =>
        layout.on(accum, img)
      }

    def allBeside(implicit layout: Layout[F],
                  shape: Shape[F],
                  traverse: Traverse[T]): F[Unit] =
      traverse.foldLeft(t, shape.empty) { (accum, img) =>
        layout.beside(accum, img)
      }

    def allAbove(implicit layout: Layout[F],
                 shape: Shape[F],
                 traverse: Traverse[T]): F[Unit] =
      traverse.foldLeft(t, shape.empty) { (accum, img) =>
        layout.above(accum, img)
      }
  }
}
