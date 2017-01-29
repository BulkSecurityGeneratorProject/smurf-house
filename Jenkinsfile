node {
    // uncomment these 2 lines and edit the name 'node-4.4.7' according to what you choose in configuration
    def nodeHome = tool name: 'node-4.4.7', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
    env.PATH = "${nodeHome}/bin:${env.PATH}"

    stage('check tools') {
        echo "Running ${env.BUILD_TAG} ${env.BUILD_ID} - ${env.BUILD_DISPLAY_NAME} on ${env.BUILD_URL}  ...  ${env.JENKINS_URL}. JobName: ${env.JOB_NAME} - ${env.JOB_URL}"

        sh 'git rev-parse HEAD > GIT_COMMIT'
        def commitNumber = readFile('GIT_COMMIT').trim()
        echo "commitNumber: ${commitNumber}"

        sh "node -v"
        sh "npm -v"
        sh "bower -v"
        sh "gulp -v"
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


    stage('creating docker') {
        sh "./mvnw -Pprod docker:build"
        sh "docker push fmunozse/smurfhouse"
    }


}
