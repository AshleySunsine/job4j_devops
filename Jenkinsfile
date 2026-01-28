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

        stage("Parallel stages: check, Package, JaCoCo Report, JaCoCo Verification, Update DB") {
            parallel {
                stage('check') {
                    steps {
                        script {
                            sh '''
                                        echo "Путь к файлу: /var/agent-jdk21/env/.env.develop"
                                        echo "Содержимое:"
                                        cat /var/agent-jdk21/env/.env.develop
                                        echo "=== Конец файла ==="
                                    '''
                            sh './gradlew check -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
                        }
                    }
                }

                stage('Package') {
                    steps {
                        sh '''
                                    echo "Путь к файлу: /var/agent-jdk21/env/.env.develop"
                                    echo "Содержимое:"
                                    cat /var/agent-jdk21/env/.env.develop
                                    echo "=== Конец файла ==="
                                '''
                        sh './gradlew build -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
                    }
                }
                stage('JaCoCo Report') {
                    steps {
                        sh '''
                                    echo "Путь к файлу: /var/agent-jdk21/env/.env.develop"
                                    echo "Содержимое:"
                                    cat /var/agent-jdk21/env/.env.develop
                                    echo "=== Конец файла ==="
                                '''
                        sh './gradlew jacocoTestReport -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
                    }
                }
                stage('JaCoCo Verification') {
                    steps {
                        sh '''
                                        echo "Путь к файлу: /var/agent-jdk21/env/.env.develop"
                                        echo "Содержимое:"
                                        cat /var/agent-jdk21/env/.env.develop
                                        echo "=== Конец файла ==="
                                    '''
                        sh './gradlew jacocoTestCoverageVerification -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
                    }
                }
                stage('Update DB') {
                    steps {
                        script {
                            sh '''
                                            echo "Путь к файлу: /var/agent-jdk21/env/.env.develop"
                                            echo "Содержимое:"
                                            cat /var/agent-jdk21/env/.env.develop
                                            echo "=== Конец файла ==="
                                        '''
                            sh './gradlew update -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
                        }
                    }
                }
                stage('publish') {
                    steps {
                        script {
                            sh './gradlew publish -P"dotenv.filename"="/var/agent-jdk21/env/.env.develop"'
                        }
                    }
                }
            }
        }

        stage('Check Git Tag') {
            steps {
                script {
                    def ip = "192.168.0.189"
                    def port = "8087"
                    def path = "/repository/my-docker-repo/"
                    def appName = "job4j_devops"
                    def appVersion = "v5.0.1"
                    def appFullName = "${appName}:${appVersion}"
                    def appFullAddress = "${ip}:${port}${path}${appFullName}"
                    def gitTag = sh(script: 'git describe --tags --exact-match', returnStdout: true).trim()

                    sh """
                    timeout 5 bash -c "</dev/tcp/${ip}/${port}" && echo "connection success" || echo "connection failed"
                    """
                    if (gitTag) {
                         echo "Tag found: ${gitTag}. Proceeding with Docker build."
                         echo "Docker tag command: docker tag ${appFullName} ${appFullAddress}"
                         echo "Docker push command: docker push ${appFullAddress}"

                         sh "docker build -t ${appFullName} ."
                         withCredentials([usernamePassword(
                            credentialsId: 'docker-credentials-id',
                            usernameVariable: 'DOCKER_USERNAME',
                            passwordVariable: 'DOCKER_PASSWORD'
                            )]) {
                                sh """
                                    echo "$DOCKER_PASSWORD" | docker login ${ip}:${port} -u "$DOCKER_USERNAME" --password-stdin
                                """
                            }
                         sh "docker tag ${appFullName} ${appFullAddress}"
                         sh "docker push ${appFullAddress}"
                    } else {
                        echo "No Git tag found. Skipping Docker build."
                    }
                }
            }
        }
    }
}

