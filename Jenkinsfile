pipeline {
    agent { label 'Host-node' }

    tools {
        git 'Default'
    }

    stages {
        stage('kubectl') {
            steps {
                script {
                    // Вывод текущего пользователя
                    sh 'whoami'

                    // Вывод переменных окружения, связанных с kubeconfig
                    sh 'echo "KUBECONFIG=$KUBECONFIG"'

                    // Вывод текущего контекста kubectl
                    sh 'kubectl config current-context'

                    // Вывод всех доступных контекстов
                    sh 'kubectl config get-contexts'

                    // Вывод информации о текущем кластере
                    sh 'kubectl config view --minify'

                    // Версия клиента kubectl
                    sh 'kubectl version --client'

                    // Попытка выполнить apply
                    sh 'kubectl apply -f secret.yaml'
                    sh 'kubectl apply -f configmap.yaml'
                    sh 'kubectl apply -f development.yaml'
                    sh 'kubectl apply -f service.yaml'
                }
            }
        }
    }
}