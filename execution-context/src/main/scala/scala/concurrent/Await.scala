package scala.concurrent

import scala.concurrent.duration._
import libuv.Loop
import libuv.RunMode

object Await {
  /**
   * Await the "completed" state of an `Awaitable`.
   *
   * Although this method is blocking, the internal use of [[scala.concurrent.blocking blocking]] ensures that
   * the underlying [[ExecutionContext]] is prepared to properly manage the blocking.
   *
   * @param  awaitable
   *         the `Awaitable` to be awaited
   * @param  atMost
   *         maximum wait time, which may be negative (no waiting is done),
   *         [[scala.concurrent.duration.Duration.Inf Duration.Inf]] for unbounded waiting, or a finite positive
   *         duration
   * @return the `awaitable`
   * @throws InterruptedException     if the current thread is interrupted while waiting
   * @throws TimeoutException         if after waiting for the specified time this `Awaitable` is still not ready
   * @throws IllegalArgumentException if `atMost` is [[scala.concurrent.duration.Duration.Undefined Duration.Undefined]]
   */
  @throws(classOf[TimeoutException])
  @throws(classOf[InterruptedException])
  def ready[T](awaitable: Awaitable[T], atMost: Duration): awaitable.type =  {
    val time = System.currentTimeMillis()
    while(System.currentTimeMillis() < time + atMost.toMillis && !awaitable.asInstanceOf[Future[T]].isCompleted)
      Loop.default.run(RunMode.NoWait)
    awaitable.ready(0.nano)(AwaitPermission)
  }
  /**
   * Await and return the result (of type `T`) of an `Awaitable`.
   *
   * Although this method is blocking, the internal use of [[scala.concurrent.blocking blocking]] ensures that
   * the underlying [[ExecutionContext]] to properly detect blocking and ensure that there are no deadlocks.
   *
   * @param  awaitable
   *         the `Awaitable` to be awaited
   * @param  atMost
   *         maximum wait time, which may be negative (no waiting is done),
   *         [[scala.concurrent.duration.Duration.Inf Duration.Inf]] for unbounded waiting, or a finite positive
   *         duration
   * @return the result value if `awaitable` is completed within the specific maximum wait time
   * @throws InterruptedException     if the current thread is interrupted while waiting
   * @throws TimeoutException         if after waiting for the specified time `awaitable` is still not ready
   * @throws IllegalArgumentException if `atMost` is [[scala.concurrent.duration.Duration.Undefined Duration.Undefined]]
   */
  @throws(classOf[Exception])
  def result[T](awaitable: Awaitable[T], atMost: Duration): T = {
    val time = System.currentTimeMillis()
    while(System.currentTimeMillis() < time + atMost.toMillis && !awaitable.asInstanceOf[Future[T]].isCompleted)
      Loop.default.run(RunMode.NoWait)
    awaitable.result(0.nano)(AwaitPermission)
  }
}