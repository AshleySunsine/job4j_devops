pipeline {
    agent { label 'Host-node' }

    tools {
        git 'Default'
    }

    stages {
        stage('kubectl') {
            steps {
                script {
                    sh 'whoami'
                    sh 'kubectl version'
                    sh 'kubectl apply -f ./templates/secret.yaml'
                    sh 'kubectl apply -f ./templates/configmap.yaml'
                    sh 'kubectl apply -f ./templates/development.yaml'
                    sh 'kubectl apply -f ./templates/service.yaml'
                }
            }
        }
    }
}
