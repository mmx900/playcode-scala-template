package models

import java.util.Date

case class User(id: Option[Long], email: String, password: String, nickname: String, registration: Date) {
	def this(email: String, password: String, nickname: String) = this(None, email, password, nickname, new Date)
}

