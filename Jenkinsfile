pipeline {
  agent {
    label "jenkins_agent"
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
            archiveArtifacts: 'target/*.exe'
        }
    }
}
