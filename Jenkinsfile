pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'my-bank'
        HELM_RELEASE = 'my-bank'
        HELM_NAMESPACE = 'my-bank'
        HELM_CHART_PATH = 'helm/my-bank'
    }

    stages {
        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh """
                    docker build -t ${DOCKER_REGISTRY}/accounts-service:${BUILD_NUMBER} ./accounts-service
                    docker build -t ${DOCKER_REGISTRY}/cash-service:${BUILD_NUMBER} ./cash-service
                    docker build -t ${DOCKER_REGISTRY}/transfer-service:${BUILD_NUMBER} ./transfer-service
                    docker build -t ${DOCKER_REGISTRY}/notifications-service:${BUILD_NUMBER} ./notifications-service
                    docker build -t ${DOCKER_REGISTRY}/front-app:${BUILD_NUMBER} ./front-app
                """
            }
        }

        stage('Deploy') {
            steps {
                sh """
                    helm dependency update ${HELM_CHART_PATH}
                    helm upgrade --install ${HELM_RELEASE} ${HELM_CHART_PATH} \
                        --namespace ${HELM_NAMESPACE} --create-namespace \
                        --set accounts-service.image.tag=${BUILD_NUMBER} \
                        --set cash-service.image.tag=${BUILD_NUMBER} \
                        --set transfer-service.image.tag=${BUILD_NUMBER} \
                        --set notifications-service.image.tag=${BUILD_NUMBER} \
                        --set front-app.image.tag=${BUILD_NUMBER}
                """
            }
        }

        stage('Verify') {
            steps {
                sh "helm test ${HELM_RELEASE} -n ${HELM_NAMESPACE}"
            }
        }
    }
}
