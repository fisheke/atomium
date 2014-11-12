package controllers

import be.vlaanderen.awv.atom.format._
import be.vlaanderen.awv.atom.{Context, FeedService}
import play.api.libs.json.{JsValue, Json}

class MyFeedController(feedService: FeedService[String, Context]) extends FeedController[String](feedService) {

  implicit val c: Context = new Context {} //dummy context for MemoryFeedStore

  implicit val marshaller : Feed[String] => JsValue = { f => Json.toJson(f) }

  def getHeadOfFeed = {
    super.getHeadOfFeed
  }

  def getFeedPage(start: Int, pageSize: Int) = {
    super.getFeedPage(start, pageSize)
  }

}
