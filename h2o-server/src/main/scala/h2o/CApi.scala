package h2o

import scalanative.unsafe._

@native
object CApi {
  type h2o_handler_t = CStruct6[
    CSize,
    CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit],
    CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit],
    CFuncPtr1[Ptr[Byte], Unit],
    CFuncPtr2[Ptr[Byte], Ptr[Byte], CInt],
    CInt
  ]

  

  def h2o_config_register_path(hostconf: Ptr[Byte], path: CString, flags: CInt): Ptr[Byte] = extern
  def h2o_create_handler(pathconf: Ptr[Byte], size: CSize): h2o_handler_t = extern
}