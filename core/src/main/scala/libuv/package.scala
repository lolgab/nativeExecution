package libuv
import scalanative.unsafe._
import scalanative.libc.stdlib.malloc
import scala.scalanative.posix.netinet.in._
import scala.collection.mutable
import CApi._

class Loop private (private[libuv] val ptr: Ptr[Byte]) extends AnyVal {
  def run(runMode: RunMode = RunMode.Default): Int = uv_run(ptr, runMode.value)
  def close(): Int = uv_loop_close(ptr)
  def isAlive: Boolean = uv_loop_alive(ptr) != 0
}
  
object Loop {
  scalanative.runtime.ExecutionContext.global.execute(
    new Runnable {
      def run(): Unit = {
        val returnCode = Loop.default.run()
        if(returnCode != 0) {
          System.err.println(libuv.errorMessage(returnCode))
          System.exit(returnCode)
        }
      }
    }
  )
  val default: Loop = new Loop(uv_default_loop())
}

class RunMode private (private[libuv] val value: CInt) extends AnyVal
object RunMode {
  final val Default: RunMode = new RunMode(0)
  final val Once: RunMode = new RunMode(1)
  final val NoWait: RunMode = new RunMode(2)
}

class HandleType private (private[libuv] val value: CInt) extends AnyVal
object HandleType {
  final val Unknown = new HandleType(0)
  final val Async = new HandleType(1)
  final val Check = new HandleType(2)
  final val FsEvent = new HandleType(3)
  final val FsPoll = new HandleType(4)
  final val Handle = new HandleType(5)
  final val Idle = new HandleType(6)
  final val NamedPipe = new HandleType(7)
  final val Poll = new HandleType(8)
  final val Prepare = new HandleType(9)
  final val Process = new HandleType(10)
  final val Stream = new HandleType(11)
  final val Tcp = new HandleType(12)
  final val Timer = new HandleType(13)
  final val Tty = new HandleType(14)
  final val Udp = new HandleType(15)
  final val Signal = new HandleType(16)
  final val File = new HandleType(17)
}

class RequestType private (private[libuv] val value: CInt) extends AnyVal
object RequestType {
  final val Unknown = new RequestType(0)
  final val Req = new RequestType(1)
  final val Connect = new RequestType(2)
  final val Write = new RequestType(3)
  final val Shutdown = new RequestType(4)
  final val UdpSend = new RequestType(5)
  final val Fs = new RequestType(6)
  final val Work = new RequestType(7)
  final val GetAddressInfo = new RequestType(8)
  final val GetNameiInfo = new RequestType(9)
}
class Buffer private [libuv] (private val ptr: Ptr[uv_buf_t]) extends AnyVal {
  def base = ptr._1
  def len = ptr._2

  def base_=(v: Ptr[Byte]): Unit = ptr._1 = v
  def len_=(v: CSize): Unit = ptr._2 = v

  def asBuffers: Buffers = new Buffers(ptr.asInstanceOf[Ptr[uv_buf_t]])
}
object Buffer {
  def apply()(implicit z: Zone) = new Buffer(z.alloc(sizeof[uv_buf_t]).asInstanceOf[Ptr[uv_buf_t]])
}
class Buffers private [libuv] (private[libuv] val ptr: Ptr[uv_buf_t]) extends AnyVal {
  def apply(n: Int): Buffer = new Buffer(ptr + n)
}
class Write private (private[libuv] val ptr: Ptr[Byte]) extends AnyVal
object Write {
  def apply()(implicit z: Zone): Write = {
    new Write(z.alloc(uv_req_size(RequestType.Write.value)))
  }
}
class Handle private[libuv] (private val ptr: Ptr[Byte]) extends AnyVal {
  def close(afterClose: () => Unit): Unit = {
    Handle.afterCloses(ptr.toLong) = afterClose
    uv_close(ptr, Handle.closeCallback)
  }
}
object Handle {
  private val afterCloses: mutable.Map[Long, () => Unit] = mutable.Map.empty
  private val closeCallback = new CFuncPtr1[Ptr[Byte], Unit] {
    def apply(handle: Ptr[Byte]): Unit = {
      val l = handle.toLong
      afterCloses(l)()
    }
  }
}

class Stream private[libuv] (private val ptr: Ptr[Byte]) extends AnyVal {
  def listen(backlog: Int = 128)(onConnection: Int => Unit): Int = {
    Stream.onConnectionCallbacks += ptr.toLong -> onConnection
    uv_listen(ptr, backlog, Stream.listenCallback)
  }
  def accept(client: Stream): CInt = uv_accept(ptr, client.ptr)
  def startRead(onRead: (Buffer, CSize) => Unit): CInt = {
    Stream.onReadCallbacks += ptr.toLong -> onRead
    uv_read_start(ptr, Stream.allocCallback, Stream.readCallback)
  }
  def write(req: Write, buffers: Buffers, nbufs: CInt)(afterWrite: Int => Unit) = {
    val writePtrLong = req.ptr.toLong
    Stream.afterWriteCallbacks(writePtrLong) = afterWrite
    uv_write(req.ptr, ptr, buffers.ptr, nbufs, Stream.writeCallback)
  }
}
object Stream {
  private val onConnectionCallbacks: mutable.Map[Long, Int => Unit] = mutable.Map.empty
  private val onReadCallbacks: mutable.Map[Long, (Buffer, CSize) => Unit] = mutable.Map.empty
  private var afterWriteCallbacks: mutable.Map[Long, Int => Unit] = mutable.Map.empty
  private val listenCallback = new CFuncPtr2[Ptr[Byte], CInt, Unit] {
    def apply(stream: Ptr[Byte], status: CInt): Unit = { 
      val l: Long = (stream.toLong)
      Stream.onConnectionCallbacks(l)(status)
    }
  }
  private val allocCallback = new CFuncPtr3[Ptr[Byte], CSize, Ptr[uv_buf_t], Unit] {
    def apply(handle: Ptr[Byte], suggestedSize: CSize, buf: Ptr[uv_buf_t]): Unit = {
      val buffer = new Buffer(buf)
      buffer.base = malloc(suggestedSize)
      buffer.len = suggestedSize
    }
  }
  private val readCallback = new CFuncPtr3[Ptr[Byte], CSize, Ptr[uv_buf_t], Unit] {
    def apply(stream: Ptr[Byte], nread: CSize, buf: Ptr[uv_buf_t]): Unit = {
      val l: Long = (stream.toLong)
      Stream.onReadCallbacks(l)(new Buffer(buf), nread)
    }
  }
  private val writeCallback = new CFuncPtr2[Ptr[Byte], CInt, Unit] {
    def apply(req: Ptr[Byte], status: CInt): Unit = {
      val l: Long = (req.toLong)
      afterWriteCallbacks(l)(status)
    }
  }
}
class TcpHandle private (private val ptr: Ptr[Byte]) extends AnyVal {
  def init(loop: Loop = Loop.default): Int = uv_tcp_init(loop.ptr, ptr)
  def bind(address: Ip4, flags: Int = 0)(implicit z: Zone): Int = uv_tcp_bind(ptr, address.ptr, flags)

  def asStream: Stream = new Stream(ptr)
  def asHandle: Handle = new Handle(ptr)
}
object TcpHandle {
  def apply()(implicit z: Zone): TcpHandle = {
    new TcpHandle(z.alloc(uv_handle_size(HandleType.Tcp.value)))
  }
}
class PipeHandle private (private val ptr: Ptr[Byte]) extends AnyVal {
  def init(loop: Loop = Loop.default, ipc: Boolean): Int = uv_pipe_init(loop.ptr, ptr, if(ipc) 1 else 0)
  def bind(name: String)(implicit z: Zone): Int = uv_pipe_bind(ptr, toCString(name))

  def asStream: Stream = new Stream(ptr)
}
object PipeHandle {
  def apply()(implicit z: Zone): PipeHandle = {
    new PipeHandle(z.alloc(uv_handle_size(HandleType.NamedPipe.value)))
  }
}
class Ip4 private (private[libuv] val ptr: Ptr[sockaddr_in]) extends AnyVal
object Ip4 {
  def apply(ip: String, port: Int)(implicit z: Zone): Ip4 = {
    val ptr = z.alloc(sizeof[sockaddr_in]).asInstanceOf[Ptr[sockaddr_in]]
    uv_ip4_addr(toCString(ip), port, ptr)
    new Ip4(ptr)
  }
}
class PrepareHandle private (private val ptr: Ptr[Byte]) extends AnyVal {
  def init(loop: Loop = Loop.default): Int = uv_prepare_init(loop.ptr, ptr)
  def start(callback: () => Unit): Int = {
    PrepareHandle.callbacks += ptr.toLong -> callback
    uv_prepare_start(ptr, PrepareHandle.prepareCallback)
  }
  def stop(): CInt = uv_prepare_stop(ptr)
}
object PrepareHandle {
  private val callbacks: mutable.Map[Long, () => Unit] = mutable.Map.empty
  private val prepareCallback = new CFuncPtr1[Ptr[Byte], Unit] {
    def apply(prepare: Ptr[Byte]): Unit = {
      val l: Long = (prepare.toLong)
      PrepareHandle.callbacks(l)()
    }
  }
  def apply()(implicit z: Zone): PrepareHandle = {
    new PrepareHandle(z.alloc(uv_handle_size(HandleType.Prepare.value)))
  }
}
class TimerHandle private (private val ptr: Ptr[Byte]) extends AnyVal {
  def init(loop: Loop = Loop.default): Int = uv_timer_init(loop.ptr, ptr)
  def start(callback: () => Unit, timeout: Long, repeat: Long): Int = {
    TimerHandle.callbacks += ptr.toLong -> callback
    uv_timer_start(ptr, TimerHandle.timerCallback, timeout, repeat)
  }
  def stop(): CInt = {
    TimerHandle.callbacks -= ptr.toLong
    uv_timer_stop(ptr)
  }
}
object TimerHandle {
  private val callbacks: mutable.Map[Long, () => Unit] = mutable.Map.empty
  private val timerCallback = new CFuncPtr1[Ptr[Byte], Unit] {
    def apply(prepare: Ptr[Byte]): Unit = {
      val l: Long = prepare.toLong
      val p = TimerHandle.callbacks(l)()
    }
  }
  def apply()(implicit z: Zone): TimerHandle = {
    new TimerHandle(z.alloc(uv_handle_size(HandleType.Timer.value)))
  }
}

package object libuv {
  def errorMessage(err: CInt): String = Zone { implicit z =>
    fromCString(uv_err_name(err))
  }
}