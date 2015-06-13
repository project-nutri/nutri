name := "Nutri"

version := "1.0"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.apache.lucene" % "lucene-core" % "4.0.0",
    "org.apache.lucene" % "lucene-analyzers-common" % "4.0.0",
    "org.apache.lucene" % "lucene-queryparser" % "4.0.0",
    "org.seleniumhq.selenium" % "selenium-java" % "2.44.0",
    "org.jsoup" % "jsoup" % "1.7.2",
    "com.typesafe" % "config" % "1.2.1",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
    "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.5"
  )
}
