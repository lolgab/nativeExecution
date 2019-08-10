package object h2o {
  implicit class h2o_handler_t_ops(val handler: h2o_handler_t) extends AnyVal {
    def on_context_init: CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit] = handler._2
    def on_context_dispose: CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit] = handler._3
    def dispose: CFuncPtr1[Ptr[Byte], Unit] = handler._4
    def on_req: CFuncPtr2[Ptr[Byte], Ptr[Byte], CInt] = handler._5
    
    def on_context_init_=(v: CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit]): Unit = handler._2 = v
    def on_context_dispose_=(v: CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit]): Unit = handler._3 = v
    def dispose_=(v: CFuncPtr1[Ptr[Byte], Unit]): Unit = handler._4 = v
    def on_req_=(v: CFuncPtr2[Ptr[Byte], Ptr[Byte], CInt]): Unit = handler._5 = v
  }

  object Token {
    final val Authority =  h2o__tokens + 0
    final val Method =  h2o__tokens + 1
    final val Path =  h2o__tokens + 2
    final val Scheme =  h2o__tokens + 3
    final val Status =  h2o__tokens + 4
    final val Accept =  h2o__tokens + 5
    final val AcceptCharset =  h2o__tokens + 6
    final val AcceptEncoding =  h2o__tokens + 7
    final val AcceptLanguage =  h2o__tokens + 8
    final val AcceptRanges =  h2o__tokens + 9
    final val AccessControlAllowOrigin =  h2o__tokens + 10
    final val Age =  h2o__tokens + 11
    final val Allow =  h2o__tokens + 12
    final val Authorization =  h2o__tokens + 13
    final val CacheControl =  h2o__tokens + 14
    final val CacheDigest =  h2o__tokens + 15
    final val Connection =  h2o__tokens + 16
    final val ContentDisposition =  h2o__tokens + 17
    final val ContentEncoding =  h2o__tokens + 18
    final val ContentLanguage =  h2o__tokens + 19
    final val ContentLength =  h2o__tokens + 20
    final val ContentLocation =  h2o__tokens + 21
    final val ContentRange =  h2o__tokens + 22
    final val ContentType =  h2o__tokens + 23
    final val Cookie =  h2o__tokens + 24
    final val Date =  h2o__tokens + 25
    final val Etag =  h2o__tokens + 26
    final val Expect =  h2o__tokens + 27
    final val Expires =  h2o__tokens + 28
    final val From =  h2o__tokens + 29
    final val Host =  h2o__tokens + 30
    final val Http2Settings =  h2o__tokens + 31
    final val IfMatch =  h2o__tokens + 32
    final val IfModifiedSince =  h2o__tokens + 33
    final val IfNoneMatch =  h2o__tokens + 34
    final val IfRange =  h2o__tokens + 35
    final val IfUnmodifiedSince =  h2o__tokens + 36
    final val KeepAlive =  h2o__tokens + 37
    final val LastModified =  h2o__tokens + 38
    final val Link =  h2o__tokens + 39
    final val Location =  h2o__tokens + 40
    final val MaxForwards =  h2o__tokens + 41
    final val ProxyAuthenticate =  h2o__tokens + 42
    final val ProxyAuthorization =  h2o__tokens + 43
    final val Range =  h2o__tokens + 44
    final val Referer =  h2o__tokens + 45
    final val Refresh =  h2o__tokens + 46
    final val RetryAfter =  h2o__tokens + 47
    final val Server =  h2o__tokens + 48
    final val SetCookie =  h2o__tokens + 49
    final val StrictTransportSecurity =  h2o__tokens + 50
    final val Te =  h2o__tokens + 51
    final val TransferEncoding =  h2o__tokens + 52
    final val Upgrade =  h2o__tokens + 53
    final val UserAgent =  h2o__tokens + 54
    final val Vary =  h2o__tokens + 55
    final val Via =  h2o__tokens + 56
    final val WwwAuthenticate =  h2o__tokens + 57
    final val XCompressHint =  h2o__tokens + 58
    final val XForwardedFor =  h2o__tokens + 59
    final val XReproxyUrl =  h2o__tokens + 60
    final val XTraffic =  h2o__tokens + 61
  }

  class Method(private val ptr: Ptr[Byte]) extends AnyVal {

  }

  class Pool(private val ptr: Ptr[Byte]) extends AnyVal

  class Result(private val ptr: Ptr[Byte]) extends AnyVal

  class Request(private val ptr: Ptr[Byte]) extends AnyVal {
    def method: Method = new Method(ptr + 152)
    def pool: Pool = new Pool(ptr + 920)
    def res = new Result(ptr + 488)
  }
  private type ProceedCb = CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit]
  private type StopCb = CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit]
  private type h2o_generator_t = CStruct2[ProceedCb,StopCb]
  class Generator private (private val ptr: Ptr[h2o_generator_t]) extends AnyVal {
    def proceed = ptr._1
    def stop = ptr._2

    def proceed_=(v: ProceedCb): Unit = ptr._1 = v
    def stop_=(v: StopCb): Unit = ptr._2 = v
  }
  object Generator {
    def apply(proceed: ProceedCb, stop: StopCb)(implicit z: Zone): Generator =
      new Generator(
        z.alloc(sizeof[h2o_generator_t]).asInstanceOf[Ptr[h2o_generator_t]]
      )
  }
}