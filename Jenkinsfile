pipeline {
    agent { label 'Host-node' }

    tools {
        git 'Default'
    }

    stages {
    stage('Checkout') {
                steps {
                    git branch: 'Pipeline-for-K8s', url: 'https://github.com/AshleySunsine/job4j_devops.git'
                }
            }

        stage('kubectl') {
            steps {
                script {
                    sh 'kubectl version'
                    sh 'kubectl apply -f secret.yaml'
                    sh 'kubectl apply -f configmap.yaml'
                    sh 'kubectl apply -f development.yaml'
                    sh 'kubectl apply -f service.yaml'
                }
            }
        }
    }

}
