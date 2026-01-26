pipeline {
    agent { label 'Host-node' }
    environment {
        KUBECONFIG = "/var/lib/jenkins/.kube/config"
    }

    tools {
        git 'Default'
    }

    stages {
        stage('kubectl') {
            steps {
                script {
                    sh 'whoami'
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
