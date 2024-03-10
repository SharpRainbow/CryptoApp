pipeline {
  agent {
    docker {
      image "maven"
      }
    }
  stages {
    stage("Prepare") {
      steps {
        sh "java -version"
        sh "mvn -v"
      }
    }
    stage("Build") {
      steps {
        sh "chmod +x ./tools/linux-x64.warp-packer"
        sh "mvn clean package"
        sh "ls"
      }
    }
  }
  post {
        always {
            archiveArtifacts 'target/*.bin'
        }
    }
}
