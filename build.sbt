name := """playcode-scala-template"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers ++= Seq(
	"Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
	jdbc,
	anorm,
	cache,
	ws,
	"com.typesafe.play" %% "play-slick" % "0.8.0",
	"org.pac4j" % "play-pac4j_scala" % "1.3.0-SNAPSHOT",
	"org.pac4j" % "pac4j-oauth" % "1.5.1",
	"org.webjars" % "jquery" % "2.1.1",
	"org.webjars" % "bootstrap" % "3.2.0",
	"org.webjars" % "angularjs" % "1.3.0-beta.18",
	"org.webjars" % "angular-ui-bootstrap" % "0.11.0-2"
)
