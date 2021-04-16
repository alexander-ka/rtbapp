package com.kazachonak.rtbapp.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.kazachonak.rtbapp.model._
import spray.json.DefaultJsonProtocol


object RtbAppJsonProtocol  extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val targetingFormat = jsonFormat1(Targeting)
  implicit val bannerFormat = jsonFormat4(Banner)
  implicit val campaignFormat = jsonFormat5(Campaign)

  implicit val geoFormat = jsonFormat1(Geo)
  implicit val deviceFormat = jsonFormat2(Device)
  implicit val impressionFormat = jsonFormat8(Impression)
  implicit val siteFormat = jsonFormat2(Site)
  implicit val userFormat = jsonFormat2(User)
  implicit val bidRequestFormat = jsonFormat5(BidRequest)

  implicit val bidResponseFormat = jsonFormat5(BidResponse)
}
