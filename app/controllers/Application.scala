package controllers

import play.api.mvc.{Action, Request}
import play.api.libs.json.{Json, JsValue, Writes}
import play.api.db.slick.{DBAction, DBSessionRequest, dbSessionRequestAsSession}
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.{email, nonEmptyText, tuple}
import models.User
import views.html
import org.pac4j.play.scala.ScalaController

object Application extends ScalaController with Secured {

	implicit val userReads = Json.reads[User]

	implicit object UserWrites extends Writes[User] {
		def writes(a: User): JsValue = {
			Json.obj(
				"id" -> a.id,
				"email" -> a.email,
				"nickname" -> a.nickname
			)
		}
	}

  def index = Action {
    Ok(views.html.index())
  }

	def signUpRequest = Action {
		Ok(html.signup())
	}

	val signUpForm = Form(
		tuple(
			"email" -> email,
			"nickname" -> nonEmptyText,
			"password" -> nonEmptyText
		)
	)

	def signUp = DBAction {
		implicit rs =>
			val (email, nickname, password) = signUpForm.bindFromRequest.get
			val u = new User(email, password, nickname)
			val userId = models.Users.insert(u)

			Ok.withSession(
				"id" -> userId.toString,
				"nickname" -> u.nickname
			)
	}

	def getRedirectPath(request: Request[_]) = {
		request.getQueryString("redirectPath").getOrElse("/")
	}

	def loginRequest = Action {
		request =>
			val redirectPath = getRedirectPath(request)
			val newSession = getOrCreateSessionId(request)

			Ok(html.login(redirectPath)).withSession(newSession)
	}

	val loginForm = Form(
		tuple("email" -> email, "password" -> nonEmptyText)
	)

	def login = DBAction {
		implicit rs =>
			val (email, password) = loginForm.bindFromRequest.get
			models.Users.findByEmail(email) match {
				case Some(u) if u.password == password =>
					Ok.withSession(
						"id" -> u.id.get.toString,
						"nickname" -> u.nickname
					)
				case _ =>
					NotFound
			}
	}

	def loggedin = Action {
		implicit request =>
			getUserFromRequest match {
				case Some(u) =>
					Ok(Json.toJson(u))
				case _ =>
					Ok("0")
			}
	}

	def logout = Action {
		Ok.withNewSession
	}
}