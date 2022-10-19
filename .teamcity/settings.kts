import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.ExecBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.buildSteps.nuGetInstaller
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {

    vcsRoot(HttpsGithubComJpspringallTeamCitySonarCubeRefsHeadsMaster1)

    buildType(Build)
    buildType(PullRequestBuild)

    params {
        param("my.test.parameters", "")
        param("my.test.parameter", "")
    }
}

object Build : BuildType({
    name = "Master Build"

    params {
        param("env.SONAR_TOKEN", "sqp_8ec5dfa55e18c5b3f2a91c35bced2581afe64fbe")
        param("sonar.project.name", "SonarCubeTest")
        param("sonar.project.key", "SonarCubeTest")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        nuGetInstaller {
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            projects = "TCSonarCube.sln"
        }
        dotnetBuild {
            name = "Build Solution"
            projects = "TCSonarCube.sln"
            sdk = "6"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetTest {
            name = "Test Solution"
            projects = "TCSonarCube.sln"
            sdk = "6"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        script {
            name = "Sonar Cube Docker Set Variables"
            scriptContent = """
                #!/bin/bash
                branch=%teamcity.build.branch%
                echo "Extracting Key from: ${'$'}branch"
                id="${'$'}(cut -d'/' -f2 <<<"${'$'}branch")"
                echo "##teamcity[setParameter name='sonar.pullrequest.key' value='${'$'}id']"
                echo "##teamcity[setParameter name='sonar.pullrequest.branch' value='${'$'}branch']"
            """.trimIndent()
        }
        exec {
            name = "Run Script"
            path = "ci/run-sonar.sh"
            arguments = """"%sonar.pullrequest.key%" "%sonar.pullrequest.branch%""""
            formatStderrAsError = true
            dockerImagePlatform = ExecBuildStep.ImagePlatform.Linux
            dockerImage = "emeraldsquad/sonar-scanner-net"
        }
        script {
            name = "Sonar Cube Run Scan"
            scriptContent = """
                #!/bin/bash
                dotnet-sonarscanner begin \
                    /k:%sonar.project.key% \
                    /n:"%sonar.project.name%" \
                    /v:"%build.vcs.number%" \
                    /d:sonar.login="%env.SONAR_TOKEN%" \
                    /d:sonar.host.url="%env.SONAR_HOST_URL%" \
                    /d:sonar.pullrequest.key="%sonar.pullrequest.key%" \
                    /d:sonar.pullrequest.branch="%sonar.pullrequest.branch%" \
                    /d:sonar.pullrequest.base="master" \
                    /d:sonar.cs.opencover.reportsPaths="**/coverage.opencover.xml"
                dotnet test -v n TCSonarCube.sln --filter 'FullyQualifiedName~Test.Unit' -p:CollectCoverage=true -p:CoverletOutputFormat=opencover%2cteamcity --results-directory "testresults"
                dotnet-sonarscanner end /d:sonar.login="%env.SONAR_TOKEN%"
            """.trimIndent()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerImage = "emeraldsquad/sonar-scanner-net"
        }
        step {
            name = "Begin analysis"
            type = "sonar-qube-msbuild"
            enabled = false
            param("sonarProjectName", "SonarCubeTest")
            param("sonarProjectKey", "SonarCubeTest")
            param("sonarServer", "f7994fe6-39ec-4a04-8c8a-261e630fe753")
        }
        step {
            name = "Finish analysis"
            type = "sonar-qube-msbuild-finish"
            enabled = false
        }
        step {
            name = "SonarQubeRunner"
            type = "sonar-plugin"
            enabled = false
            param("sonarProjectName", "SonarCubeTest")
            param("sonarProjectKey", "SonarCubeTest")
            param("sonarServer", "f7994fe6-39ec-4a04-8c8a-261e630fe753")
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:22719b77-2b1e-4b10-be8b-6cab49c7c069"
                }
            }
        }
        pullRequests {
            enabled = false
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            provider = github {
                authType = vcsRoot()
                filterSourceBranch = "+:refs/pull/*/head"
                filterTargetBranch = "+:refs/heads/master"
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
})

object PullRequestBuild : BuildType({
    name = "Pull Request Build"

    vcs {
        root(HttpsGithubComJpspringallTeamCitySonarCubeRefsHeadsMaster1)
    }

    steps {
        nuGetInstaller {
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            projects = "TCSonarCube.sln"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${HttpsGithubComJpspringallTeamCitySonarCubeRefsHeadsMaster1.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:22719b77-2b1e-4b10-be8b-6cab49c7c069"
                }
            }
        }
    }
})

object HttpsGithubComJpspringallTeamCitySonarCubeRefsHeadsMaster1 : GitVcsRoot({
    name = "https://github.com/jpspringall/team-city-sonar-cube#refs/heads/master (1)"
    url = "https://github.com/jpspringall/team-city-sonar-cube"
    branch = "refs/heads/master"
    branchSpec = "+:refs/pull/*/merge"
    authMethod = password {
        userName = "jpspringall"
        password = "credentialsJSON:e224d815-b2d6-4dc7-9e5c-11f7d85dbd51"
    }
    param("oauthProviderId", "PROJECT_EXT_2")
})
