pipeline {
  agent {
    label "agent2"
  }
  stages {
    stage("Prepare") {
      steps {
        sh "apt update"
        sh "apt install maven -y"
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
