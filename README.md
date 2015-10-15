Admob mediation with InMobi
=================================
Banner & Interstitial mediation sample apps for InMobi SDK 5.0.0

Installation guide
==================================
##### Admob dashboard

1. Create a custom mediation group in admob site
2. Place your InMobi Account ID and Placement ID on ``JSON`` style
 1. e.g. `{"accountId": "a1c4a7a124a04446b4b9588128284eee" , "placementId": 1433947912309}`
3. Class name should be one of below
  - Interstitial: `com.admob.custom.InMobiInterstitial.InMobiInterstitialCustomEvent`
  - Banner: `com.admob.custom.InMobiBanner.InMobiBannerCustomEvent`

##### Android setting 
1. Place your mediation ID (e.g. `ca-app-pub-2653148267411761/1329412333`) in  `string.xml`
 
### That's it!

License
=======
[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
