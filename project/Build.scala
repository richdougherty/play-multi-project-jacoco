import sbt._
import play.Project._
import Keys._
import de.johoop.jacoco4sbt.JacocoPlugin._


object ApplicationBuild extends Build {

  val appVersion = "1.0-SNAPSHOT"

    
  // So we can get code coverage
  lazy val jacocoSettings = Seq(
      parallelExecution in jacoco.Config := false,
      jacoco.excludes in jacoco.Config := Seq("Routes*","*Reverse*","*anonfun*")//,
      
      // Workaround 
//      Keys.fork in jacoco.Config := true,

      // Workaround to run jacoco in current working directory
//      testGrouping in jacoco.Config := {
//        val original: Seq[Tests.Group] = (testGrouping in jacoco.Config).value
//
//        original.map { group =>
//          val forkOptions = ForkOptions(
//            bootJars = Nil,
//            javaHome = javaHome.value,
//            connectInput = connectInput.value,
//            outputStrategy = outputStrategy.value,
//            runJVMOptions = javaOptions.value,
//            workingDirectory = Some(baseDirectory.value), // Run jacoco in current subproject directory
//            envVars = envVars.value
//          )
//
//          group.copy(runPolicy = Tests.SubProcess(forkOptions))
//        }
//      }
    )
    
  val PlayBaseDirProp = Tags.Tag("PlayBaseDirProp")
  concurrentRestrictions in Global += Tags.limit(PlayBaseDirProp, 1)

  def withSystemProperty[T](name: String, value: String)(f: => T) = {
    System.setProperty(name, value)
    try {
      f
    } finally {
      System.clearProperty(name)
    }
  }

  def withPlayBaseDir[T](taskKey: TaskKey[T]) = {
    def taggedPropTask = Def.task {
      withSystemProperty("play.base.dir", baseDirectory.value.toString) {
        taskKey.value
      }
    } tag(PlayBaseDirProp)
    taskKey := taggedPropTask.value
  }

  lazy val s = playJavaSettings ++ Seq(jacoco.settings:_*) ++ jacocoSettings
  
  val commonDependencies = Seq(
      javaCore
      )

  val common = play.Project(
      "common", appVersion, commonDependencies, settings = s, path = file("modules/common")    
  ).settings(
      javaOptions in Test += s"-Dplay.base.dir=${baseDirectory.value}",
      withPlayBaseDir(test in jacoco.Config)
  )
    
  val appDependencies = Seq(
      javaJdbc,
      javaEbean,
      cache
   )
   
   val main = play.Project("play-multi-project-jacoco", appVersion, appDependencies, settings = s).dependsOn(
      common
   ).aggregate(
      common    
   )
}
   