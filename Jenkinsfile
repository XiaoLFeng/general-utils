node {
  stage('SCM') {
    checkout scm
  }
  stage('SonarQube Analysis') {
    def mvn = tool 'Default Maven';
    withSonarQubeEnv() {
      sh "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=XiaoLFeng_general-utils_89081129-e116-4061-9f5e-1beec82cdf18 -Dsonar.projectName='general-utils'"
    }
  }
}
