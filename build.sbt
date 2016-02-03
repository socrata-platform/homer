import sbt._

def astyanaxExcludes(x: ModuleID) = x exclude ("commons-logging", "commons-logging") exclude ("org.mortbay.jetty", "servlet-api") exclude ("javax.servlet", "servlet-api")
val astyanaxVersion =  "1.56.48"
val astyanaxCassandra = astyanaxExcludes("com.netflix.astyanax" % "astyanax-cassandra" % astyanaxVersion)
val astyanaxThrift = astyanaxExcludes("com.netflix.astyanax" % "astyanax-thrift" % astyanaxVersion)

val rojomaJsonV3            = "com.rojoma"  %% "rojoma-json-v3"             % "[3.2.0,4.0.0)"

val geocoders               = "com.socrata" %% "geocoders"                  % "1.0.6"

val dataCoordinator         = "com.socrata" %% "secondarylib-feedback"      % "2.1.0"

val socrataCuratorUtils     = "com.socrata" %% "socrata-curator-utils"      % "1.0.3"

val socrataThirdPartyUtils  = "com.socrata" %% "socrata-thirdparty-utils"   % "4.0.12"

val socrataSoqlTypes        = "com.socrata" %% "soql-types"                 % "1.0.1" excludeAll(ExclusionRule(organization = "com.rojoma"))

val typesafeConfig          = "com.typesafe" % "config"                     % "1.0.2"

lazy val commonSettings = Seq(
  organization := "com.socrata",
  version := "0.0.0",
  scalaVersion := "2.10.4"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "geocoding-secondary",
    libraryDependencies ++= Seq(
      astyanaxCassandra,
      astyanaxThrift,
      rojomaJsonV3,
      geocoders,
      dataCoordinator,
      socrataCuratorUtils,
      socrataSoqlTypes,
      socrataThirdPartyUtils,
      typesafeConfig
    ),
    com.socrata.sbtplugins.StylePlugin.StyleKeys.styleCheck in Test := {},
    com.socrata.sbtplugins.StylePlugin.StyleKeys.styleCheck in Compile := {},
    com.socrata.sbtplugins.findbugs.JavaFindBugsPlugin.JavaFindBugsKeys.findbugsFailOnError in Test := false,
    com.socrata.sbtplugins.findbugs.JavaFindBugsPlugin.JavaFindBugsKeys.findbugsFailOnError in Compile := false
  )
