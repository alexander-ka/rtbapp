package com.kazachonak.rtbapp.rest

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.kazachonak.rtbapp.actor.BidHandler
import com.kazachonak.rtbapp.actor.BidHandler.RequestBids
import com.kazachonak.rtbapp.model._
import RtbAppJsonProtocol._

class RtbRest(bidHandler: ActorRef[BidHandler.Command])(implicit val system: ActorSystem[_]) {

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("rtbapp.routes.ask-timeout"))

  val route: Route =
    path("request-bids") {
      post {
        entity(as[BidRequest]) { bidRequest =>
          onSuccess(bidHandler.ask(RequestBids(bidRequest, _))) {
            case Some(bidResponse) => complete(bidResponse)
            case None => complete(StatusCodes.NoContent)
          }
        }
      }
    }

}
