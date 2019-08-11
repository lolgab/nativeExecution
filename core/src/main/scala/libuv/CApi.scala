package libuv

import scalanative.unsafe._
import scala.scalanative.posix.netinet.in._

@link("uv")
@extern
private [libuv] object CApi {
  type uv_buf_t = CStruct2[Ptr[Byte], CSize]

  def uv_default_loop(): Ptr[Byte] = extern
  def uv_run(loop: Ptr[Byte], mode: CInt): CInt = extern
  def uv_loop_alive(loop: Ptr[Byte]): CInt = extern
  def uv_loop_close(loop: Ptr[Byte]): CInt = extern
  
  def uv_listen(stream: Ptr[Byte], backlog: CInt, cb: CFuncPtr2[Ptr[Byte], CInt, Unit]): CInt = extern
  def uv_accept(stream: Ptr[Byte], client: Ptr[Byte]): CInt = extern
  def uv_read_start(stream: Ptr[Byte], alloc_cb: CFuncPtr3[Ptr[Byte], CSize, Ptr[uv_buf_t], Unit], read_cb: CFuncPtr3[Ptr[Byte], CSize, Ptr[uv_buf_t], Unit]): CInt = extern
  def uv_read_stop(stream: Ptr[Byte]): CInt = extern
  def uv_write(req: Ptr[Byte], handle: Ptr[Byte], bufs: Ptr[uv_buf_t], nbufs: CInt, cb: CFuncPtr2[Ptr[Byte], CInt, Unit]): CInt = extern
  def uv_close(handle: Ptr[Byte], close_cb: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
    
  def uv_ip4_addr(ip: CString, port: CInt, addr: Ptr[sockaddr_in]): CInt = extern
  def uv_tcp_init(loop: Ptr[Byte], handle: Ptr[Byte]): CInt = extern
  def uv_tcp_bind(handle: Ptr[Byte], addr: Ptr[sockaddr_in], flags: CInt): CInt = extern
  
  def uv_pipe_init(loop: Ptr[Byte], handle: Ptr[Byte], ipc: CInt): CInt = extern
  def uv_pipe_open(handle: Ptr[Byte], file: CInt): CInt = extern
  def uv_pipe_bind(handle: Ptr[Byte], name: CString): CInt = extern
  def uv_pipe_connect(req: Ptr[Byte], handle: Ptr[Byte], name: String, cb: CFuncPtr2[Ptr[Byte], CInt, Unit]): Unit = extern
  def uv_pipe_getsockname(handle: Ptr[Byte], buffer: Ptr[Byte], size: Ptr[CSize]): CInt = extern

  def uv_handle_size(tpe: CInt): CSize = extern
  def uv_req_size(tpe: CInt): CSize = extern
  def uv_strerror(err: CInt): CString = extern

  def uv_prepare_init(loop: Ptr[Byte], prepare: Ptr[Byte]): CInt = extern
  def uv_prepare_start(prepare: Ptr[Byte], cb: CFuncPtr1[Ptr[Byte], Unit]): CInt = extern
  def uv_prepare_stop(prepare: Ptr[Byte]): CInt = extern

  def uv_timer_init(loop: Ptr[Byte], timer: Ptr[Byte]): CInt = extern
  def uv_timer_start(timer: Ptr[Byte], cb: CFuncPtr1[Ptr[Byte], Unit], timeout: CLong, repeat: CLong): CInt = extern
  def uv_timer_stop(timer: Ptr[Byte]): CInt = extern

  def uv_err_name(err: CInt): CString = extern
}