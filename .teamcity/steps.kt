import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.buildSteps.nuGetInstaller

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

    fun BuildType.sonarTest(

    ) {
        params {
            param("teamcity.pullRequest.number", "master")
        }
    }
}
