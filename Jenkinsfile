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
            }
        }
        stage('Docker Build') {
            steps {
                sh '''
                                echo "Путь к файлу: /var/agent-jdk21/env/.env.develop"
                                echo "Содержимое:"
                                cat /var/agent-jdk21/env/.env.develop
                                echo "=== Конец файла ==="
                            '''
                sh 'echo "FINISH!!!!"'
                //sh 'docker build -t job4j_devops .'
            }
        }
    }
}
