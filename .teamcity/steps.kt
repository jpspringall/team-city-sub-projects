
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

    fun BuildType.configureSonar(
    ) {
        steps {
            script {
                name = "Sonar Set Variables"
                conditions {
                    equals("system.teamcity.buildConfName", "Pull Request Build")
                }
                scriptContent = """
                #!/bin/bash
                id=%teamcity.pullRequest.number%
                echo "Id is: ${'$'}id"
                branch="pull/${'$'}id"
                echo "Branch is: ${'$'}branch"
            """.trimIndent()
            }
        }
    }
}
