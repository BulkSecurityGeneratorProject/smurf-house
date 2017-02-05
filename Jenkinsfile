node {
    // uncomment these 2 lines and edit the name 'node-4.4.7' according to what you choose in configuration
    def nodeHome = tool name: 'node-4.4.7', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
    env.PATH = "${nodeHome}/bin:${env.PATH}"

    stage('check tools') {
        echo "Running ${env.BUILD_TAG} ${env.BUILD_ID} - ${env.BUILD_DISPLAY_NAME} on ${env.BUILD_URL}  ...  ${env.JENKINS_URL}. JobName: ${env.JOB_NAME} - ${env.JOB_URL}"

        echo "\u2600 BUILD_URL=${env.BUILD_URL}"
        def workspace = pwd()
        echo "\u2600 workspace=${workspace}"

        sh 'git rev-parse HEAD > GIT_COMMIT'
        def commitNumber = readFile('GIT_COMMIT').trim()
        echo "commitNumber: ${commitNumber}"

        sh "node -v"
        sh "npm -v"
        sh "bower -v"
        sh "gulp -v"


        def pomv = version();

        echo "pomv: ${pomv}"

        echo "stop ${aaaaa}"

    }

    stage('checkout') {
        checkout scm
    }

    stage('npm install') {
        sh "npm install"
    }

    stage('clean') {
        sh "./mvnw clean"
    }


    stage('backend tests') {
        try {
            sh "./mvnw test"
        } catch(err) {
            throw err
        } finally {
            step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
        }
    }


    stage('frontend tests') {
        try {
            sh "gulp test"
        } catch(err) {
            throw err
        } finally {
            step([$class: 'JUnitResultArchiver', testResults: '**/target/test-results/karma/TESTS-*.xml'])
        }
    }

    stage('packaging') {
        sh "./mvnw package -Pprod -DskipTests"
    }

    stage ('docker?') {
        timeout(time: 1, unit: 'HOURS') {
          input 'Generate docker version and push?'
        }
    }

    stage('creating docker') {
        sh "./mvnw -Pprod docker:build"

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'DOCKER_USERPWD', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            sh "docker login --username $USERNAME --password $PASSWORD"
        }

        echo "pushing to ${DOCKER_IMAGE}"

        sh "docker push ${DOCKER_IMAGE}"

    }


}


def version() {
    String path = pwd();
    def matcher = readFile("${path}/pom.xml") =~ '<version>(.+)</version>'
    return matcher ? matcher[0][1] : null
}
