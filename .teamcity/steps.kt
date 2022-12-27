
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildSteps.ExecBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object CommonSteps {
    fun BuildType.buildAndTest(

    ) {
        steps {
//            nuGetInstaller {
//                toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
//                projects = "TCSonarCube.sln"
//            }
            dotnetBuild {
                name = "Build Solution"
                workingDir = "project"
                projects = "TCSonarCube.sln"
                sdk = "6"
                param(
                    "dotNetCoverage.dotCover.home.path",
                    "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%"
                )
            }
//            dotnetTest {
//                name = "Test Solution"
//                workingDir = "project"
//                projects = "TCSonarCube.sln"
//                sdk = "6"
//                param(
//                    "dotNetCoverage.dotCover.home.path",
//                    "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%"
//                )
//            }
            script {
                enabled = false
                name = "Test Solution In A Container"
                workingDir = "project"
                scriptContent = "dotnet test TCSonarCube.sln -r /src/results --logger 'trx;logfilename=testresults.trx' --nologo"
                dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
                dockerImage = "mcr.microsoft.com/dotnet/sdk:6.0"
                dockerRunParameters = """
                    --env ASPNETCORE_ENVIRONMENT=Build
                    -v %system.teamcity.build.checkoutDir%/test-results:/src/results
                    -v /var/run/docker.sock:/var/run/docker.sock
                """.trimIndent()
            }

            script {
                enabled = true
                name = "Batect"
                workingDir = "./"
                scriptContent = """
                #!/bin/bash
                export TC_SONAR_QUBE_USE=1
                export TC_SONAR_QUBE_SERVER=%env.sonar_server%
                export TC_SONAR_QUBE_USER=%env.sonar_user%
                export TC_SONAR_QUBE_PASSWORD=%env.sonar_password%
                export TC_SONAR_QUBE_NUMBER=%teamcity.pullRequest.number%
                export TC_SONAR_QUBE_VERSION=%build.counter%
                ./batect run-test
            """.trimIndent()
                scriptContent = ""
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
        val imageRepository = "jpspringall"
        //CHANGE THIS BEFORE USING FOR REALZ"
        steps {
            exec {
                enabled = false
                name = "Run Sonar Script"
                path = "ci/run-sonar.sh"
                arguments =
                    """-s ""%env.sonar_server%"" -u ""%env.sonar_user%"" -p ""%env.sonar_password%"" -n ""%teamcity.pullRequest.number%"" -v ""%build.counter%"""""
                formatStderrAsError = true
                dockerImagePlatform = ExecBuildStep.ImagePlatform.Linux
                dockerPull = true
                dockerImage = "${imageRepository}/dotnet-sonar-scanner:5.8.0" //CHECK IMAGE NAME FOR REALZ
                dockerRunParameters = """
                    -v %system.teamcity.build.checkoutDir%/test-results:/test-results
                """.trimIndent()
            }
        }
    }

    fun BuildType.createParameters(
    ) {
        params {
            param("teamcity.pullRequest.number", "")
            param("teamcity.git.fetchAllHeads", "true")
        }
    }
}
