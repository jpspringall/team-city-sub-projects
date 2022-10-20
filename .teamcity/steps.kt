
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildSteps.*

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

    fun BuildType.printPullRequestNumber(
    ) {
        steps {
            script {
                name = "Print Pull Request Number"
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

    fun BuildType.runSonarScript(
    ) {
        //CHANGE THIS BEFORE USING FOR REALZ"
        val imageRepository = "emeraldsquad"
        //CHANGE THIS BEFORE USING FOR REALZ"
        steps {
            exec {
                name = "Run Sonar Script"
                path = "ci/run-sonar.sh"
                arguments =
                    """-s ""%env.sonar_server%"" -u ""%env.sonar_user%"" -p ""%env.sonar_password%"" -n ""%teamcity.pullRequest.number%"" -v ""%build.counter%"""""
                formatStderrAsError = true
                dockerImagePlatform = ExecBuildStep.ImagePlatform.Linux
                dockerPull = true
                dockerImage = "${imageRepository}/sonar-scanner-net" //CHECK IMAGE NAME FOR REALZ
            }
        }
    }

    fun BuildType.createParameters(
    ) {
        params {
            param("teamcity.pullRequest.number", "")
        }
    }
}
