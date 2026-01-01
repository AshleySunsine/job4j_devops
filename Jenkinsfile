pipeline {
    agent { label 'Host-node' }

    tools {
        git 'Default'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo 'Начинаем checkout ветки Pipeline-for-K8s'
                    git branch: 'Pipeline-for-K8s', url: 'https://github.com/AshleySunsine/job4j_devops.git'
                    echo 'Checkout завершен'
                }
            }
        }

        stage('kubectl') {
            steps {
                script {
                    echo 'Проверка версии kubectl'
                    sh 'kubectl version'

                    echo 'Применение secret.yaml'
                    sh 'kubectl apply -f secret.yaml'

                    echo 'Применение configmap.yaml'
                    sh 'kubectl apply -f configmap.yaml'

                    echo 'Применение development.yaml'
                    sh 'kubectl apply -f development.yaml'

                    echo 'Применение service.yaml'
                    sh 'kubectl apply -f service.yaml'
                }
            }
        }
    }
}