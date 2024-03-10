pipeline {
  agent {
    docker {
      image "maven"
      }
    }
  stages {
    stage("Prepare") {
      steps {
        sh "mvn -v"
        sh "chmod +x ./tools/linux-x64.warp-packer"
      }
    }
    stage("Build") {
      steps {
        sh "mvn clean package"
      }
    }
  }
  post {
        always {
            archiveArtifacts 'target/*.bin'
        }
    }
}
