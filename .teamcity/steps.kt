
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.buildSteps.nuGetInstaller
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object CommonSteps {
    fun BuildType.buildAndTest(

    ) {
        steps {
            nuGetInstaller {
                toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
                projects = "TCSonarCube.sln"
            }
            dotnetBuild {
                name = "Build Solution"
                projects = "TCSonarCube.sln"
                sdk = "6"
                param(
                    "dotNetCoverage.dotCover.home.path",
                    "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%"
                )
            }
            dotnetTest {
                name = "Test Solution"
                projects = "TCSonarCube.sln"
                sdk = "6"
                param(
                    "dotNetCoverage.dotCover.home.path",
                    "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%"
                )
            }
        }
    }

    fun BuildType.configureForBuild(
    ) {
        val buildConfName = "%teamcity.buildConfName%"
        print("\nbuildConfName is $buildConfName.")
        if (buildConfName == "Master Build") {
            params {
                param("teamcity.pullRequest.number", "master")
            }
        }
    }

    fun BuildType.configureSonar(
    ) {
        steps {
            script {
                name = "Sonar Cube Docker Set Variables"
                scriptContent = """
                #!/bin/bash
                branch=%teamcity.pullRequest.number%
                echo "Extracting Key from: ${'$'}branch"
                id="${'$'}(cut -d'/' -f2 <<<"${'$'}branch")"
                echo "##teamcity[setParameter name='sonar.pullrequest.key' value='${'$'}id']"
                echo "##teamcity[setParameter name='sonar.pullrequest.branch' value='${'$'}branch']"
            """.trimIndent()
            }
        }
    }

}
