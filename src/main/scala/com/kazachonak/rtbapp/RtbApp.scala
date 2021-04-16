package com.kazachonak.rtbapp

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.kazachonak.rtbapp.actor.BidHandler
import com.kazachonak.rtbapp.rest.RtbRest
import scala.io.StdIn
import scala.util.{Failure, Success}


object RtbApp {

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing]{ context =>
      val bidHandlerActor = context.spawn(BidHandler(CampaignData.activeCampaigns), "BidHandlerActor")
      context.watch(bidHandlerActor)
      val rtbRest = new RtbRest(bidHandlerActor)(context.system)

      startHttpServer(rtbRest.route)(context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "RtbAppHttpServer")
  }

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val host = system.settings.config.getString("rtbapp.server.host")
    val port = system.settings.config.getInt("rtbapp.server.port")

    val futureBinding = Http().newServerAt(host, port).bind(routes)
    futureBinding.onComplete{
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
        StdIn.readLine()
        binding.unbind()
          .onComplete(_ => system.terminate())
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}
