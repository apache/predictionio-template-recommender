name := "template-scala-parallel-recommendation"

scalaVersion := "2.11.8"
libraryDependencies ++= Seq(
  "org.apache.predictionio" %% "apache-predictionio-core" % "0.13.0" % "provided",
  "org.apache.spark"        %% "spark-mllib"              % "2.1.1" % "provided")
