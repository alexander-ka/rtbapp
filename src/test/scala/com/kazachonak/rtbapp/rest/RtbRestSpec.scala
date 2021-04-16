package com.kazachonak.rtbapp.rest

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, MessageEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.kazachonak.rtbapp.actor.BidHandler
import com.kazachonak.rtbapp.model._
import com.kazachonak.rtbapp.rest.RtbAppJsonProtocol._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class RtbRestSpec extends AnyFlatSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  import akka.actor.typed.scaladsl.adapter._
  implicit val typedSystem = system.toTyped
  implicit val scheduler = system.scheduler

  val testKit = ActorTestKit()
  override def afterAll(): Unit = testKit.shutdownTestKit()

  val testCampaignsList = List(
    Campaign(
      id = 1,
      country = "BY",
      targeting = Targeting(
        targetedSiteIds = Set("000aaa001", "000aaa002")
      ),
      banners = List(
        Banner(
          id = 1,
          src = "https://some.url.1",
          width = 300,
          height = 250
        )
      ),
      bid = 2.0
    )
  )

  val userRegistry = testKit.spawn(BidHandler(testCampaignsList))
  val routes = new RtbRest(userRegistry).route


  "RtbRest" should "return the right JSON for bid responses" in {
    val bidRequest = BidRequest(
      id = "SGu1Jpq1IO",
      imp = Some(List(Impression(id="1", wmin=None, wmax=None, w=Some(300), hmin=None, hmax=None, h=Some(250), bidFloor=Some(1.5)))),
      site = Site("000aaa002", "fake.tld"),
      user = Some(User("USARIO1", Some(Geo(Some("BY"))))),
      device = Some(Device("440579f4b408831516ebd02f6e1c31b4", Some(Geo(Some("BY")))))
    )

    val bidRequestEntity = Marshal(bidRequest).to[MessageEntity].futureValue

    Post("/request-bids").withEntity(bidRequestEntity) ~> routes ~> check {
      status should ===(StatusCodes.OK)
      contentType should ===(ContentTypes.`application/json`)
      entityAs[String] should ===("""{"adid":"1","banner":{"height":250,"id":1,"src":"https://some.url.1","width":300},"bidRequestId":"SGu1Jpq1IO","id":"response1","price":1.5}""")
    }
  }

  it should "return HTTP 204: No content if it is not going to bid on the request" in {
    val bidRequest = BidRequest(
      id = "SGu1Jpq1IO",
      imp = Some(List(Impression(id="1", wmin=None, wmax=None, w=Some(300), hmin=None, hmax=None, h=Some(250), bidFloor=Some(3.0)))),
      site = Site("000aaa002", "fake.tld"),
      user = Some(User("USARIO1", Some(Geo(Some("BY"))))),
      device = Some(Device("440579f4b408831516ebd02f6e1c31b4", Some(Geo(Some("BY")))))
    )

    val bidRequestEntity = Marshal(bidRequest).to[MessageEntity].futureValue

    Post("/request-bids").withEntity(bidRequestEntity) ~> routes ~> check {
      status should ===(StatusCodes.NoContent)
      entityAs[String] should ===("")
    }
  }

}
