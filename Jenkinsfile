pipeline {
    agent any

    stages {
        stage('Lint') {
            steps {
                sh './gradlew ktlintFormat'
            }
        }
        stage('Build'){
            steps {
                sh './gradlew shadowJar'
            }
        }
    }
}