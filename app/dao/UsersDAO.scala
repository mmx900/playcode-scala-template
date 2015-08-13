package dao

import java.util.Date
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import models.{User, Page}

import scala.concurrent.Future

@Singleton
class UsersDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]  {
  import dbConfig.driver.api._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {

    implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("EMAIL", O.NotNull)
    def password = column[String]("PASSWORD")
    def nickname = column[String]("NICKNAME", O.NotNull)
    def registration = column[Date]("DATE", O.NotNull)

    def * = (id.?, email, password, nickname, registration) <>(User.tupled, User.unapply)
  }

  val users = TableQuery[Users]

  def list(page: Int = 0, pageSize: Int = 10, keyword: Option[String])(implicit s: Session): Future[Page[User]] = {
    val offset = pageSize * page
    val query = keyword match {
      case Some(k) if !k.trim.isEmpty =>
        users.filter(_.email like s"%$k%")
      case _ =>
        users
    }

    def pagesQuery = db.run {
      query.sortBy(_.id.desc).drop(offset).take(pageSize).result
    }
    for {
      totalRows <- count(keyword)
      pages <- pagesQuery
    } yield Page(pages, page, offset, totalRows)
  }

  def count(keyword: Option[String]): Future[Int] = {
    val query = keyword match {
      case Some(k) =>
        users.filter(_.email like s"%$k%").length.result
      case None =>
        users.length.result
    }

    db.run(query)
  }

  def findById(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def findByEmail(email: String): Future[Option[User]] = {
    db.run(users.filter(_.email === email).result.headOption)
  }

  def insert(user: User): Future[Long] = {
    db.run((users returning users.map(_.id)) += user)
  }
}