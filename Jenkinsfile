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
        sh "cd $JAVA_HOME"
        sh "apk fetch openjdk17-jmods"
        sh "tar zxvf openjdk17-jmods*"
        sh "mv ./usr/lib/jvm/java-17-openjdk/jmods/ ./"
        sh "rm openjdk"
        sh "rm -r usr"
      }
    }
    stage("Build") {
      steps {
        sh "cd -"
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
