package h2o

import CApi._
import scalanative.unsafe._
import scalanative.libc.string.memcmp

object H2oServer {
  @inline
  def h2o_memis(_target: Ptr[Byte], target_len: CSize, _test: Ptr[Byte], test_len: CSize): Boolean = {
    val target: Ptr[Byte] = _target
    val test: Ptr[Byte] = _test;
    if (target_len != test_len) false
    else if (target_len == 0) true
    else if (target(0) != test(0)) false
    else memcmp(target + 1, test + 1, test_len - 1) == 0
  }

  def registerHandler(hostconf: Ptr[Byte], path: CString, on_req: CFuncPtr2[Ptr[Byte], Ptr[Byte], CInt]): Ptr[Byte] = {
    val pathconf = h2o_config_register_path(hostconf, path, 0)
    val handler = h2o_create_handler(pathconf, sizeof[Ptr[Byte]])
    handler.on_req = on_req
    pathconf
  }

  def post_test(req: Request): CInt = {
    if (h2o_memis(req.method.base, req.method.len, c"POST", 4) &&
        h2o_memis(req->path_normalized.base, req->path_normalized.len, c"/post-test/", 11)) {
        val generator = Generator()
        req.res.status = 200
        req.res.reason = c"OK"
        h2o_add_header(req.pool, req.res.headers, H2O_TOKEN_CONTENT_TYPE, null, c"text/plain; charset=utf-8", 26);
        h2o_start_response(req, &generator);
        h2o_send(req, &req->entity, 1, 1);
        0
    } else -1
  }

  def main(args: Array[String]): Unit = {
    val hostconf = stackalloc[Byte]
    val path = c""
    val on_req = new CFuncPtr2[Ptr[Byte], Ptr[Byte], CInt] {
      def apply(p: Ptr[Byte], p2: Ptr[Byte]): CInt = 1
    }
    registerHandler(hostconf, path, on_req)
  }
}
