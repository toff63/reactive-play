package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.oauth.OAuthCalculator
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.iteratee._
import java.io.OutputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.File
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.FileInputStream
import play.api.data._
import play.api.data.Forms._
import scala.util.Random
import views.html.helper.repeat
import play.api.libs.concurrent.Promise
import scala.io.BufferedSource
import scala.concurrent.Await

object Application extends Controller {
  val pageName:String = "Chat Application"
  val defaultName:String = "unknown" + Random.nextInt(1000)  
  val userForm = Form("user" -> nonEmptyText(0,30))
  
  def index = Action {
    Ok(views.html.index(pageName,userForm))
  }
  
  def chatRoom = Action { implicit request =>
    userForm.bindFromRequest.fold( 
        errors => Ok(views.html.chat(pageName, defaultName)),
        user => Ok(views.html.chat(pageName, user)))
  }

}

object Chat extends Controller {
  val (out,channel) = Concurrent.broadcast[String]
  
  def chatJs(user:String)  = Action{ implicit request =>
    Ok(views.js.chat(user))
  }
  
  def chat(userName:String) = WebSocket.using[String] { request => 
    val in = Iteratee.foreach[String](
        msg => channel push(userName + ": "+ msg)
    ).map(_ => println ("Disconnected"))
    (in, out)
  }
}
  
object Video extends Controller {
  
  val (outVideo,channelVideo) = Concurrent.broadcast[String]
  val source:BufferedSource = scala.io.Source.fromFile("C:\\Users\\Avell G1511\\Documents\\dev\\reactive-play\\public\\pulpFiction.srt")
  val lines:Iterator[String] = source.getLines  

  var textStream:Enumerator[String] = Enumerator.unfold("") { line =>
      Thread.sleep(1000)
      if (lines.hasNext)  Some(lines.next, line) else None
  }.onDoneEnumerating(source.close)

  def videoJs  = Action(implicit request => Ok(views.js.video()))

  def video = WebSocket.using[String] { request => 
    val in = Iteratee.foreach[String](
        msg => channelVideo push (msg)
    ).map(_ => println("Disconnected"))
    textStream |>> in
    (in, outVideo)
  }
}

