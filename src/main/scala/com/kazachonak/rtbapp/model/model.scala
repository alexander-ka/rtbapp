package com.kazachonak.rtbapp.model


case class Campaign(id: Int, country: String, targeting: Targeting, banners: List[Banner], bid: Double)
case class Targeting(targetedSiteIds: Set[String])
case class Banner(id: Int, src: String, width: Int, height: Int)

case class BidRequest(id: String, imp: Option[List[Impression]], site: Site, user: Option[User], device: Option[Device])

case class Impression(id: String,
                      wmin: Option[Int], wmax: Option[Int], w: Option[Int],
                      hmin: Option[Int], hmax: Option[Int], h: Option[Int],
                      bidFloor: Option[Double]) {

  def bidFloorDouble: Double = bidFloor.getOrElse(0.0)

  def isEligibleBanner(banner: Banner): Boolean =
    isValidDimension(banner.height, h, hmin, hmax) && isValidDimension(banner.width, w, wmin, wmax)

  private def isValidDimension(value: Int, exact: Option[Int], min: Option[Int], max: Option[Int]): Boolean =
    (exact, min, max) match {
      case (Some(exact), _, _) => value == exact
      case (_, Some(min), Some(max)) => value >= min && value <= max
      case (_, Some(min), None) => value >= min
      case (_, None, Some(max)) => value <= max
      case _ => false
    }
}

/*
 * According to the Eskimi task doc Bid request protocol Site.id is a number (Int). But the examples of Bid request
 * and Campaigns use strings like "0006a522ce0f4bbbbaa6b3c38cafaa0f" for site id. I assume that there is an error and
 * site id should be String, not Int.
 */
case class Site(id: String, domain: String)

case class User(id: String, geo: Option[Geo])
case class Device(id: String, geo: Option[Geo])
case class Geo(country: Option[String])

/*
 * According to OpenRTB-API-Specification-Version-2-3, BidResponse.id is an "ID of the bid request to which this is
 * a response". But in the example bid response provided in the Eskimi task doc BidResponse.id equals to some value
 * "response1". So I'm generating ids like "response1", "response2", "response3", etc.
 */
case class BidResponse(id: String, bidRequestId: String, price: Double, adid: Option[String], banner: Option[Banner])
