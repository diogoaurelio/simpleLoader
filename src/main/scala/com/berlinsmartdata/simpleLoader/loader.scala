package com.berlinsmartdata.simpleLoader

import com.berlinsmartdata.simpleLoader.utils.JobConfiguration
import com.berlinsmartdata.simpleLoader.utils.wsRequest
import play.api.libs.json._

object loader extends App {

  val conf = JobConfiguration.getConfiguration()
  val host = try conf.getString("api.default.host") catch {case e: Exception => "ENTER-YOUR-API-HOST-HERE" }
  val x_api_key = try conf.getString("api.default.x_api_key") catch { case e: Exception => ""}
  val content_type = try conf.getString("api.default.content_type") catch {case e: Exception => "application/json" }

  val headersMap = Map(
    "Content-Type" -> content_type,
    "x-api-key" -> x_api_key
  )
  val endPoint = s"$host/prod/recomendation_sys_prod"
  val numRequests=1
  val requestTimeOut = 2000
  val postData = Json.obj(
    "key1" -> "value1",
    "key2" -> "value2"
  )

  wsRequest.controller(wUrl=endPoint, numRequests=numRequests,
    headers=Option(headersMap), postData=Option(postData))


}
