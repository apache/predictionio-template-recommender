name := "template-scala-parallel-recommendation"

libraryDependencies ++= Seq(
  "org.apache.predictionio" %% "apache-predictionio-core" % "0.11.0-incubating" % "provided",
  "org.apache.spark"        %% "spark-core"               % "1.3.0" % "provided",
  "org.apache.spark"        %% "spark-mllib"              % "1.3.0" % "provided")
