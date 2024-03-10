pipeline {
  agent {
    label "agent2"
    args "-u root"
  }
  stages {
    stage("Prepare") {
      steps {
        sh "apk update"
        sh "apk install maven"
      }
    }
    stage("Build") {
      steps {
        sh "mvn clean package"
        sh "ls"
      }
    }
  }
  post {
        always {
            archiveArtifacts 'target/*.exe'
        }
    }
}
