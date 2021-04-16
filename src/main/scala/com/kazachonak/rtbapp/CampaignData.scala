package com.kazachonak.rtbapp

import com.kazachonak.rtbapp.model._


object CampaignData {

  def activeCampaigns = List(

    Campaign(
      id = 1,
      country = "LT",
      targeting = Targeting(
        targetedSiteIds = Set("0006a522ce0f4bbbbaa6b3c38cafaa0f")
      ),
      banners = List(
        Banner(
          id = 1,
          src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg",
          width = 300,
          height = 250
        )
      ),
      bid = 5.0
    ),

    Campaign(
      id = 2,
      country = "BY",
      targeting = Targeting(
        targetedSiteIds = Set("0006abc4bbbbaaa32423faa0f")
      ),
      banners = List(
        Banner(
          id = 1,
          src = "https://some.url.1",
          width = 400,
          height = 300
        )
      ),
      bid = 2.0
    ),

    Campaign(
      id = 3,
      country = "BY",
      targeting = Targeting(
        targetedSiteIds = Set("0007acbd890ffff")
      ),
      banners = List(
        Banner(
          id = 1,
          src = "https://some.url.2",
          width = 100,
          height = 100
        ),
        Banner(
          id = 1,
          src = "https://some.url.2",
          width = 200,
          height = 200
        )
      ),
      bid = 11.0
    )

  )

}
