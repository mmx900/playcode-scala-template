package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json.{Json, JsValue, Writes}
import play.api.data.Form
import play.api.data.Forms.{email, nonEmptyText, tuple}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.User
import dao.UsersDAO
import views.html
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.scala.Security

class Application @Inject()(usersDao: UsersDAO) extends Controller with Security[CommonProfile] with Secured {

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

	def signUp = Action.async {
		implicit rs =>
			val (email, nickname, password) = signUpForm.bindFromRequest.get
			val u = new User(email, password, nickname)
			usersDao.insert(u).map { userId =>
				Ok.withSession(
					"id" -> userId.toString,
					"nickname" -> u.nickname
				)
			}
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

	def login = Action.async {
		implicit rs =>
			val (email, password) = loginForm.bindFromRequest.get
			usersDao.findByEmail(email) map {
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