package main

import libuv._
import scalanative.unsafe.Zone
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  val f = Future {
    println("Hello from Future!")
  }

  Await.ready(f, 100.millis)

  implicit val z = Zone.open()
  def main(args: Array[String]): Unit = {
    val loop = Loop.default
    val tcp = TcpHandle()
    tcp.init()
    val addr = Ip4("0.0.0.0", 8000)
    tcp.bind(addr)
    val server = tcp.asStream
    val r = server.listen() { status => 
      println("New connection!")
      val client = TcpHandle()
      client.init()
      val res = server.accept(client.asStream)
      if(res == 0) {
        client.asStream.startRead { (buffer, n) =>
          val write = Write()
          val writeBuffer = Buffer()
          writeBuffer.base = buffer.base
          writeBuffer.len = n
          client.asStream.write(write, writeBuffer.asBuffers, 1)(status => println("end write"))
        }
      } else {
        client.asHandle.close(() => ())
      }
      tcp.asHandle.close(() => println("tcp closed"))
    }
  }
}