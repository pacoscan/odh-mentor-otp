pipeline {
    agent any

    environment {
        DOCKER_PROJECT_NAME = "odh-mentor-otp-run"
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/odh-mentor-otp-run'
        DOCKER_TAG = "test-$BUILD_NUMBER"

        SERVER_PORT = "1014"
        JAVA_MX = "2G"
        BUILD_GRAPH = "False"
        DOWNLOAD_DATA = "False"
        BACKUP_GRAPH = "False"
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    rm -f .env
                    cp .env.example .env
                    echo 'COMPOSE_PROJECT_NAME=${DOCKER_PROJECT_NAME}' >> .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env

                    echo 'SERVER_PORT=${SERVER_PORT}' >> .env

                    echo 'JAVA_MX=${JAVA_MX}' >> .env
                    echo 'BUILD_GRAPH=${BUILD_GRAPH}' >> .env
                    echo 'DOWNLOAD_DATA=${DOWNLOAD_DATA}' >> .env
                    echo 'BACKUP_GRAPH=${BACKUP_GRAPH}' >> .env
                """
            }
        }
        stage('Build') {
            steps {
                sh '''
                    aws ecr get-login --region eu-west-1 --no-include-email | bash
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml build --pull
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml push
                '''
            }
        }
        stage('Deploy') {
            steps {
               sshagent(['jenkins-ssh-key']) {
                    sh """
                        (cd infrastructure/ansible && ansible-galaxy install -f -r requirements.yml)
                        (cd infrastructure/ansible && ansible-playbook --limit=test deploy.run.yml --extra-vars "release_name=${BUILD_NUMBER}")
                    """
                }
            }
        }
    }
}
