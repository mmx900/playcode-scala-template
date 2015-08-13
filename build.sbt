name := """playcode-scala-template"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers ++= Seq(
	"Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

val angularVersion = "1.4.3"

libraryDependencies ++= Seq(
	cache,
	ws,
	"com.typesafe.play" %% "play-slick" % "1.0.1",
	"com.typesafe.play" %% "play-slick-evolutions" % "1.0.1",
	"com.h2database" % "h2" % "1.4.188",
	"org.pac4j" %% "play-pac4j-scala" % "1.5.0",
	"org.pac4j" % "pac4j-oauth" % "1.7.1",
	"org.webjars.bower" % "jquery" % "2.1.4",
	"org.webjars.bower" % "bootstrap" % "3.3.5",
	"org.webjars.bower" % "angular" % angularVersion,
	"org.webjars.bower" % "angular-route" % angularVersion,
	"org.webjars.bower" % "angular-resource" % angularVersion,
	"org.webjars.bower" % "angular-sanitize" % angularVersion,
	"org.webjars.bower" % "angular-bootstrap" % "0.13.3"
)

routesGenerator := InjectedRoutesGenerator