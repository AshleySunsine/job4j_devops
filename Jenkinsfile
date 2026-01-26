pipeline {
    agent { label 'Host-node' }

    tools {
        git 'Default'
    }

    stages {
            stage('Deploy with Helm') {
                steps {
                    script {
                        sh '''
                            helm template my-release ./ --values ./values.yaml | kubectl apply -f -
                        '''
                    }
                }
            }
        }
}
