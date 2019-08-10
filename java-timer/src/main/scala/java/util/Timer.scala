package java.util

import libuv._
import scalanative.unsafe.Zone
import scala.collection.mutable

class Timer(name: String, isDaemon: Boolean) {
  private class Task {
    implicit val zone: Zone = Zone.open()
    val timerHandle: TimerHandle = TimerHandle()
    timerHandle.init()
  }

  private val tasks = mutable.ListBuffer[Task]()

  def this(isDaemon: Boolean) = this("", isDaemon)
  def this(name: String) = this(name, false)
  def this() = this("", false)

  def cancel(): Unit = {
    while (tasks.nonEmpty) {
      val task = tasks.remove(0)
      task.timerHandle.stop()
      task.zone.close()
    }
  }
  //def purge(): Int
  def schedule(task: TimerTask, delay: Long): Unit = {
    val t = new Task
    t.timerHandle.start(task.run _, delay, 0L)
    tasks += t
  }
  def schedule(task: TimerTask, delay: Long, period: Long): Unit = {
    val t = new Task
    t.timerHandle.start(task.run _, delay, period)
  }
}