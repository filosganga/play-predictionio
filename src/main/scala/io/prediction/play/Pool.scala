package io.prediction.play

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ArrayBlockingQueue

import scala.concurrent.duration._
import scala.collection.JavaConversions._

/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class Pool[T](initial: Int = 5, max: Int = 20, timeout: Duration = 60.seconds)(init: () => T, dispose: T => Unit) {

  private val size = new AtomicInteger(0)
  private val pool = new ArrayBlockingQueue[T](max)

  (0 until initial).map(x=> create).foreach(pool.offer)

  def apply[A](f: T => A): A = {
    val x = borrow()

    try {
      val result = f(x)
      giveBack(x)
      result
    } catch {
      case t: Throwable =>
        invalidate(x)
        throw t
    }
  }

  def shutdown() {
    pool.foreach(x=> invalidate(x))
    pool.clear()
  }

  def borrow(): T = {
    pool.poll match {
      case null => createOrBlock
      case x: T => return x
    }
  }

  def giveBack(x: T) {
    pool.offer(x)
  }

  def invalidate(x: T) {
    dispose(x)
    size.decrementAndGet
  }

  private def createOrBlock: T = {
    size.get match {
      case e: Int if e == max => block
      case _  => create
    }
  }

  private def create: T = {
    size.incrementAndGet match {
      case e: Int if e > max => {
        size.decrementAndGet
        borrow()
      }
      case e: Int => init()
    }
  }

  private def block: T = {
    pool.poll() match {
      case x: T => x
      case _ => throw new RuntimeException("Couldn't acquire a client in %d seconds.".format(timeout.toSeconds))
    }
  }
}