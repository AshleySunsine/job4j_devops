pipeline {
    agent { label 'agent-kube' }

    tools {
        git 'Default'
    }

    stages {
        stage('kubectl') {
            steps {
                script {
                    sh 'kubectl version'
                    sh 'kubectl apply -f secret.yaml'
                    sh 'kubectl apply -f configmap.yaml'
                    sh 'kubectl apply -f deployment.yaml'
                    sh 'kubectl apply -f service.yaml'
                }
            }
        }
    }
}
