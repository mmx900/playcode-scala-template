package models

import java.util.Date
import play.api.db.slick.Config.driver.simple._

case class User(id: Option[Long], email: String, password: String, nickname: String, registration: Date) {
	def this(email: String, password: String, nickname: String) = this(None, email, password, nickname, new Date)
}

class Users(tag: Tag) extends Table[User](tag, "USERS") {

	implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

	def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
	def email = column[String]("EMAIL", O.NotNull)
	def password = column[String]("PASSWORD")
	def nickname = column[String]("NICKNAME", O.NotNull)
	def registration = column[Date]("DATE", O.NotNull)

	def * = (id.?, email, password, nickname, registration) <>(User.tupled, User.unapply)
}

object Users {
	val users = TableQuery[Users]

	def list(page: Int = 0, pageSize: Int = 10, keyword: Option[String])(implicit s: Session): Page[User] = {

		val offset = pageSize * page
		val totalRows = count(keyword)
		val query = keyword match {
			case Some(k) if !k.trim.isEmpty =>
				users.filter(_.email like s"%$k%")
			case _ =>
				users
		}
		val result = query.sortBy(_.id.desc).drop(offset).take(pageSize).list

		Page(result, page, offset, totalRows)
	}

	def count(keyword: Option[String])(implicit s: Session): Int = {
		keyword match {
			case Some(k) =>
				Query(users.filter(_.email like s"%$k%").length).first
			case None =>
				Query(users.length).first
		}

	}

	def findById(id: Long)(implicit s: Session): Option[User] = {
		users.filter(_.id === id).firstOption
	}

	def findByEmail(email: String)(implicit s: Session): Option[User] = {
		users.filter(_.email === email).firstOption
	}

	def insert(user: User)(implicit s: Session): Long = {
		(users returning users.map(_.id)) += user
	}
}