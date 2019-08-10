package libuv

import scala.concurrent._
import scala.collection.mutable
import libuv._
import scalanative.unsafe.Zone

object ExecutionContext {
  Future(Loop.default.run())(scalanative.runtime.ExecutionContext.global)
  val global: ExecutionContextExecutor = new ExecutionContextExecutor {
    private val taskQueue = mutable.ListBuffer[Runnable]()
    private implicit val zone: Zone = Zone.open()
    
    private val callback = () => {
      while (taskQueue.nonEmpty) {
        val runnable = taskQueue.remove(0)
        try {
          runnable.run()
        } catch {
          case t: Throwable => reportFailure(t)
        }
      }
    }

    private val p = PrepareHandle()
    p.init()
    p.start(callback)

    def reportFailure(cause: Throwable): Unit = cause.printStackTrace()
    def execute(runnable: Runnable): Unit = taskQueue += runnable 
  }
}