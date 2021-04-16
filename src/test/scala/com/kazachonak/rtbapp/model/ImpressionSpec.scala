package com.kazachonak.rtbapp.model

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers


class ImpressionSpec extends AnyFlatSpecLike with Matchers {

  "Impression.isEligibleBanner" should "check the exact size of banners rather than look to min and max constraints" in {
    val impression = Impression(id = "1", wmin=Some(100), wmax=Some(500), w=Some(300), hmin=Some(100), hmax=Some(500), h=Some(250), bidFloor=Some(3.0))
    impression.isEligibleBanner(Banner(1, "url", 300, 250)) shouldBe true
    impression.isEligibleBanner(Banner(1, "url", 400, 250)) shouldBe false
    impression.isEligibleBanner(Banner(1, "url", 300, 400)) shouldBe false
  }

  it should "check min size constraint" in {
    val impression = Impression(id = "2", wmin=Some(300), wmax=None, w=None, hmin=None, hmax=None, h=Some(250), bidFloor=Some(3.0))
    impression.isEligibleBanner(Banner(1, "url", 250, 250)) shouldBe false
    impression.isEligibleBanner(Banner(1, "url", 300, 250)) shouldBe true
    impression.isEligibleBanner(Banner(1, "url", 400, 250)) shouldBe true
  }

  it should "check both min and max size constraints" in {
    val impression = Impression(id="2", wmin=Some(300), wmax=Some(350), w=None, hmin=None, hmax=None, h=Some(250), bidFloor=Some(3.0))
    impression.isEligibleBanner(Banner(1, "url", 250, 250)) shouldBe false
    impression.isEligibleBanner(Banner(1, "url", 300, 250)) shouldBe true
    impression.isEligibleBanner(Banner(1, "url", 400, 250)) shouldBe false
  }

  it should "check both min and max size constraints for height" in {
    val impression = Impression(id="2", wmin=Some(250), wmax=Some(350), w=None, hmin=Some(250), hmax=Some(350), h=None, bidFloor=Some(3.0))
    impression.isEligibleBanner(Banner(1, "url", 300, 200)) shouldBe false
    impression.isEligibleBanner(Banner(1, "url", 300, 300)) shouldBe true
    impression.isEligibleBanner(Banner(1, "url", 300, 400)) shouldBe false
  }

}
