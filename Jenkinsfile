pipeline {
    agent { label 'agent-jdk21' }

    tools {
        git 'Default'
    }

    stages {
        stage('Prepare Environment') {
            steps {
                sh 'chmod +x ./gradlew'
            }
        }
        stage('check') {
            steps {
                script {
                    sh './gradlew check -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
                }
            }
        }

        stage('Package') {
            steps {
                sh './gradlew build -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
            }
        }
        stage('JaCoCo Report') {
            steps {
                sh './gradlew jacocoTestReport -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
            }
        }
        stage('JaCoCo Verification') {
            steps {
                sh './gradlew jacocoTestCoverageVerification -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
            }
        }
        stage('Update DB') {
            steps {
                script {
                    sh './gradlew update -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
                }
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t job4j_devops .'
            }
        }
    }
}
