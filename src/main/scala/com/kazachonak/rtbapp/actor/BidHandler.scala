package com.kazachonak.rtbapp.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.kazachonak.rtbapp.model._

import scala.util.Random


object BidHandler {

  sealed trait Command
  final case class RequestBids(bidRequest: BidRequest, replyTo: ActorRef[Option[BidResponse]]) extends Command

  def apply(campaignsList: List[Campaign]): Behavior[Command] = bidHandler(1, toMap(campaignsList))

  private case class CampaignKey(country: String, siteId: String)

  private def bidHandler(nextResponseId: Long, campaignsMap: Map[CampaignKey, List[Campaign]]): Behavior[Command] =
    Behaviors.receiveMessage{
      case RequestBids(bidRequest, replyTo) =>
        val bidResponseOpt = requestBids(bidRequest, nextResponseId, campaignsMap)
        replyTo ! bidResponseOpt
        bidHandler(nextResponseId + 1, campaignsMap)
    }

  private case class EligibleBanner(impression: Impression, banner: Banner, campaign: Campaign)

  private def requestBids(bidRequest: BidRequest, responseId: Long, campaignsMap: Map[CampaignKey, List[Campaign]]) = {
    val siteId = bidRequest.site.id
    val deviceCountry = bidRequest.device.flatMap(_.geo.flatMap(_.country))
    val userCountry = bidRequest.user.flatMap(_.geo.flatMap(_.country))
    val countryOpt = deviceCountry orElse userCountry

    countryOpt.flatMap{ country =>
      val campaigns = campaignsMap.getOrElse(CampaignKey(country, siteId), Nil)

      val eligibleBanners = for {
        impression <- bidRequest.imp.getOrElse(Nil)
        c <- campaigns if c.bid >= impression.bidFloorDouble
        b <- c.banners if impression.isEligibleBanner(b)
      } yield EligibleBanner(impression, b, c)

      chooseRandom(eligibleBanners) match {
        case Some(eb) => Some(BidResponse("response" + responseId, bidRequest.id, eb.impression.bidFloorDouble, Some(eb.campaign.id.toString), Some(eb.banner)))
        case None => None
      }
    }
  }

  private def chooseRandom(banners: List[EligibleBanner]): Option[EligibleBanner] = {
    if (banners.isEmpty)
      None
    else
      Some(banners(Random.nextInt(banners.size)))
  }

  /*
   * We build an efficient data structure for finding campaigns assuming that there are a lot of campaigns and a lot of
   * sites
   */
  private def toMap(campaignsList: List[Campaign]): Map[CampaignKey, List[Campaign]] = {
    val keyValuePairs = for {
      campaign <- campaignsList
      siteId <- campaign.targeting.targetedSiteIds
    } yield CampaignKey(campaign.country, siteId) -> campaign

    keyValuePairs.groupBy(_._1).view.mapValues(a => a.map(_._2)).toMap
  }

}
