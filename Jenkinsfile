pipeline {
    agent { label 'Host-node' }
    environment {
      KUBECONFIG = "/home/ash/.kube/config"
    }

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
                    sh 'kubectl apply -f development.yaml'
                    sh 'kubectl apply -f service.yaml'
                }
            }
        }
    }

}
