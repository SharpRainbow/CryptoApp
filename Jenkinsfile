pipeline {
  agent {
    label "agent2"
  }
  stages {
    stage("Prepare") {
      steps {
        sh "apk update"
        sh "apk add maven"
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
