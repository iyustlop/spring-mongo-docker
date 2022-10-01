pipeline {
    agent any
    tools {
        maven 'maven_3_8_6'
    }
    stages {
        stage("Build Maven"){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/iyustlop/spring-mongo-docker.git']]])
                sh 'mvn clean install -DskipTests'
            }
        }
        stage("Build Docker image"){
            steps{
                script{
                    sh 'docker build -t iyustlop/spring-mongo-demo .'
                }
            }
        }
        stage("Push image to Docker Hub"){
            steps{
                script{
                    withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerHubPwd')]) {
                        sh 'docker login -u iyustlop -p ${dockerHubPwd}'
                    }
                    
                    sh 'docker push iyustlop/spring-mongo-demo'
                }
            }
        }
    }
}
