pipeline {
  agent {
    label "agent2"
  }
  stages {
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
