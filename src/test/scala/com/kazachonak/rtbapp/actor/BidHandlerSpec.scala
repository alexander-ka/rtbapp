package com.kazachonak.rtbapp.actor

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.kazachonak.rtbapp.model._
import org.scalatest.flatspec.AnyFlatSpecLike


class BidHandlerSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike {

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
    ),
    Campaign(
      id = 2,
      country = "BY",
      targeting = Targeting(
        targetedSiteIds = Set("000aaa002", "000aaa003")
      ),
      banners = List(
        Banner(
          id = 1,
          src = "https://some.url.2",
          width = 300,
          height = 250
        )
      ),
      bid = 3.0
    ),
    Campaign(
      id = 3,
      country = "LT",
      targeting = Targeting(
        targetedSiteIds = Set("000aaa001", "000aaa004")
      ),
      banners = List(
        Banner(
          id = 1,
          src = "https://some.url.3",
          width = 300,
          height = 250
        )
      ),
      bid = 2.0
    )
  )

  val bidHandler = testKit.spawn(BidHandler(testCampaignsList), "bidHandler")
  val probe = testKit.createTestProbe[Option[BidResponse]]()


  "BidHandler" should "find campaigns with bids higher or equal to bidFloor in the corresponding Impression of BidRequest" in {
    bidHandler ! BidHandler.RequestBids(BidRequest(
      id = "SGu1Jpq1IO",
      imp = Some(List(Impression(id="1", wmin=None, wmax=None, w=Some(300), hmin=None, hmax=None, h=Some(250), bidFloor=Some(3.0)))),
      site = Site("000aaa002", "fake.tld"),
      user = Some(User("USARIO1", Some(Geo(Some("BY"))))),
      device = Some(Device("440579f4b408831516ebd02f6e1c31b4", Some(Geo(Some("BY")))))
    ), probe.ref)
    probe.expectMessage(Some(BidResponse(
      id = "response1",
      bidRequestId = "SGu1Jpq1IO",
      price = 3.0,
      adid = Some("2"),
      banner = Some(Banner(1, "https://some.url.2", 300, 250))
    )))
  }

  it should "return no campaigns if there are no banners matching the size of the Impression with the suitable bidFloor" in {
    bidHandler ! BidHandler.RequestBids(BidRequest(
      id = "222SGu1Jpq1IO",
      imp = Some(List(
        Impression(id="1", wmin=None, wmax=None, w=Some(300), hmin=None, hmax=None, h=Some(250), bidFloor=Some(5.0)),
        Impression(id="2", wmin=None, wmax=None, w=Some(400), hmin=None, hmax=None, h=Some(250), bidFloor=Some(1.0))
      )),
      site = Site("000aaa002", "fake.tld"),
      user = Some(User("USARIO2", Some(Geo(Some("BY"))))),
      device = Some(Device("440579f4b408831516ebd02f6e1c31b4", Some(Geo(Some("BY")))))
    ), probe.ref)
    probe.expectMessage(None)
  }

  it should "use country from device.geo rather than user.geo" in {
    bidHandler ! BidHandler.RequestBids(BidRequest(
      id = "333SGu1Jpq1IO",
      imp = Some(List(Impression(id="1", wmin=None, wmax=None, w=Some(300), hmin=None, hmax=None, h=Some(250), bidFloor=Some(2.0)))),
      site = Site("000aaa001", "fake.tld"),
      user = Some(User("USARIO3", Some(Geo(Some("BY"))))),
      device = Some(Device("333333331516ebd02f6e1c31b4", Some(Geo(Some("LT")))))
    ), probe.ref)
    probe.expectMessage(Some(BidResponse(
      id = "response3",
      bidRequestId = "333SGu1Jpq1IO",
      price = 2.0,
      adid = Some("3"),
      banner = Some(Banner(1, "https://some.url.3", 300, 250))
    )))
  }

}
