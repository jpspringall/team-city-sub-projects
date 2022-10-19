
import CommonSteps.buildAndTest
import CommonSteps.configureSonar
import CommonSteps.createParameters
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.project
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.version

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
params {
    param("teamcity.pullRequest.number", "")
}
    vcsRoot(HttpsGithubComJpspringallTeamCitySonarCubeRefsHeadsMaster1)
    buildType(Build)
    buildType(PullRequestBuild)
}

object Build : BuildType({
    name = "Master Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    createParameters()

    configureSonar()

    buildAndTest()

    triggers {
        vcs {
        }
    }

    features {
    }
})

object PullRequestBuild : BuildType({
    name = "Pull Request Build"

    vcs {
        root(HttpsGithubComJpspringallTeamCitySonarCubeRefsHeadsMaster1)
    }

    createParameters()

    configureSonar()

    buildAndTest()

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
        pullRequests {
            vcsRootExtId = "${HttpsGithubComJpspringallTeamCitySonarCubeRefsHeadsMaster1.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:22719b77-2b1e-4b10-be8b-6cab49c7c069"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
})

object HttpsGithubComJpspringallTeamCitySonarCubeRefsHeadsMaster1 : GitVcsRoot({
    name = "Pull Request Build"
    url = "https://github.com/jpspringall/team-city-sonar-cube"
    branch = "refs/heads/master"
    branchSpec = "refs/pull/*/head"
    authMethod = password {
        userName = "jpspringall"
        password = "credentialsJSON:e224d815-b2d6-4dc7-9e5c-11f7d85dbd51"
    }
    param("oauthProviderId", "PROJECT_EXT_2")
})
