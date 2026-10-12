pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    parameters {
        choice(
            name: 'PROFILE',
            choices: ['web-selenoid-ci', 'web-local'],
            description: 'Профиль из config/run-profiles/ (см. docs/CI_PROFILES.md)'
        )
        choice(
            name: 'MODE',
            choices: ['BASE', 'FULL'],
            description: 'BASE: essential tests only | FULL: with diagnostics'
        )
        choice(
            name: 'THREAD_COUNT',
            choices: ['4', '1', '2', '3'],
            description: 'Перекрывает thread-count в профиле'
        )
        booleanParam(
            name: 'SKIP_CLEANUP',
            defaultValue: false,
            description: 'Skip cleanup for debugging'
        )
        string(
            name: 'TEST_TAG',
            defaultValue: '',
            description: 'JUnit tag to run (e.g. email, api). Empty = all tests.'
        )
    }

    environment {
        SELENOID_URL = "http://selenoid:4444/wd/hub"
        SELENOID_BASE = "http://selenoid:4444"
        JAVA_TOOL_OPTIONS = '-Dfile.encoding=UTF-8'
        MAVEN_OPTS = '-Xmx2g -Dfile.encoding=UTF-8'
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    echo "Build #${env.BUILD_NUMBER} - Mode: ${params.MODE}, Tag: ${params.TEST_TAG ?: 'ALL'}"
                    echo "Build #${env.BUILD_NUMBER} - Profile: ${params.PROFILE}, Mode: ${params.MODE}"
                    cleanWs()
                    def scmVars = checkout scm
                    if (scmVars.GIT_BRANCH) {
                        env.GIT_BRANCH = scmVars.GIT_BRANCH
                    }
                    if (scmVars.GIT_COMMIT) {
                        env.GIT_COMMIT = scmVars.GIT_COMMIT
                    }
                    loadCiConfig()
                }
            }
        }

        stage('Environment Check') {
            when {
                expression { params.MODE == 'FULL' }
            }
            steps {
                script {
                    checkEnvironment()
                }
            }
        }

        stage('Setup Selenoid') {
            when {
                expression { params.PROFILE == 'web-selenoid-ci' }
            }
            steps {
                script {
                    setupSelenoid()
                    if (params.MODE == 'FULL') {
                        validateSelenoid()
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh """
                        mvn ${env.MAVEN_MODULE_ARGS} clean install -DskipTests -B -q
                    """
                }
            }
        }

        stage('Configure Allure') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'allure-api-key', variable: 'ALLURE_TOKEN')]) {
                        configureAllure(env.ALLURE_TOKEN)
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'allure-api-key', variable: 'ALLURE_TOKEN'),
                        usernamePassword(
                            credentialsId: 'qa-imap-credentials',
                            usernameVariable: 'IMAP_USER',
                            passwordVariable: 'IMAP_PASSWORD'
                        )
                    ]) {
                        withEnv(["EMAIL_ADDRESS=${env.IMAP_USER}"]) {
                            runTests(env.ALLURE_TOKEN)
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (!params.SKIP_CLEANUP && params.PROFILE == 'web-selenoid-ci') {
                    cleanupSelenoid()
                }
                archiveArtifacts artifacts: "logs/**/*.log, ${env.ALLURE_RESULTS_DIR}/**, **/target/surefire-reports/**/*, **/browser_session_logs/**/*.log",
                        allowEmptyArchive: true
                junit testResults: "${env.TEST_MODULE}/target/surefire-reports/*.xml", allowEmptyResults: true
            }
        }
        failure {
            script {
                echo "Pipeline failed. Check archived artifacts for details."
            }
        }
    }
}

// ============= Helper Methods =============

def loadCiConfig() {
    def rp = load 'jenkins/runProfile.groovy'
    def allureCi = rp.loadAllureCi()

    def ctx = rp.fromProfile(params.PROFILE, [
            'thread-count'  : params.THREAD_COUNT,
            'selenide.remote': env.SELENOID_URL,
            'selenoidHubUrl': env.SELENOID_URL,
    ])

    env.TEST_MODULE = ctx.module
    env.MAVEN_MODULE_ARGS = ctx.mavenModuleArgs
    env.MAVEN_PROFILE_FLAGS = ctx.mavenFlags
    env.RUN_CHANNEL = ctx.props.get('run.channel', 'web')
    env.ALLURE_RESULTS_DIR = "${ctx.module}/target/allure-results"
    env.ALLURE_ENDPOINT = allureCi['allure.endpoint']
    env.ALLURE_PROJECT_ID = rp.allureProjectId(allureCi, 'web')

    def branch = env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'main'
    env.ALLURE_LAUNCH_NAME = rp.resolveLaunchName(allureCi['allure.launch.web'], [
            'BRANCH_NAME': branch.replaceFirst(/^origin\//, ''),
    ])

    echo "Run profile: ${ctx.id} — ${ctx.description}"
    echo "Module: ${ctx.module}, run.channel=${env.RUN_CHANNEL}"
    echo "Maven flags: ${ctx.mavenFlags}"
    echo "Allure launch: ${env.ALLURE_LAUNCH_NAME}"
}

def checkEnvironment() {
    sh '''
        mkdir -p logs
        {
            echo "=== Environment Check ==="
            java -version
            mvn -version
            allurectl --version
            docker --version
        } > logs/environment.log 2>&1
    '''
}

def setupSelenoid() {
    sh '''
        mkdir -p logs

        docker images | grep -q "selenoid/chrome.*128.0" || \
            docker pull selenoid/chrome:128.0

        docker images | grep -q "selenoid/video-recorder" || \
            docker pull selenoid/video-recorder:latest-release

        docker ps -aq --filter ancestor=selenoid/chrome:128.0 | xargs -r docker rm -f || true
        docker ps -aq --filter ancestor=selenoid/video-recorder:latest-release | xargs -r docker rm -f || true

        echo "Selenoid setup complete" > logs/selenoid-setup.log
    '''
}

def validateSelenoid() {
    sh '''
        mkdir -p logs

        if ! curl -sf ${SELENOID_BASE}/status > /dev/null; then
            echo "ERROR: Selenoid not reachable" > logs/selenoid-validation.log
            exit 1
        fi

        STATUS=$(curl -sf ${SELENOID_BASE}/status)
        TOTAL=$(echo $STATUS | jq -r '.total // 0')
        USED=$(echo $STATUS | jq -r '.used // 0')
        FREE=$((TOTAL - USED))

        if [ $FREE -le 0 ]; then
            echo "ERROR: No free Selenoid slots (Total: $TOTAL, Used: $USED)" > logs/selenoid-validation.log
            exit 1
        fi

        echo "Selenoid OK - Free slots: $FREE" > logs/selenoid-validation.log
    '''
}

def configureAllure(token) {
    sh """
        mkdir -p /var/jenkins_home/.allure logs

        cat > /var/jenkins_home/.allure/config.yaml <<EOF
        project_id: ${ALLURE_PROJECT_ID}
        endpoint: ${ALLURE_ENDPOINT}
        token: ${token}
        EOF

        if ! curl -sf ${ALLURE_ENDPOINT} > /dev/null; then
            echo "WARNING: Cannot reach Allure TestOps" > logs/allure-config.log
        else
            echo "Allure configured successfully" > logs/allure-config.log
        fi
    """
}

def runTests(token) {
    // Только целевой модуль. Не использовать -am test: иначе Maven гоняет surefire
    // и в desktop-tests (Playwright/Electron) из-за зависимости web-tests → desktop-tests.
    // -am + install — в Build, иначе test резолвит qa-core из stale .m2 SNAPSHOT.
    def mvnCmd = "-pl ${env.TEST_MODULE} test ${env.MAVEN_PROFILE_FLAGS} " +
            "-Dallure.results.directory=${env.WORKSPACE}/${env.ALLURE_RESULTS_DIR} " +
            '-Dsurefire.rerunFailingTestsCount=0 ' +
            '-Dimap.user=' + env.IMAP_USER + ' ' +
            '-Dimap.password=' + env.IMAP_PASSWORD + ' ' +
            '-Demail.address=' + env.EMAIL_ADDRESS + ' ' +
            '-DexcludedGroups=encryption-suite ' +
            '-B'

    if (params.TEST_TAG?.trim()) {
        mvnCmd += ' -Dgroups=' + params.TEST_TAG.trim()
        mvnCmd += ' -DfailIfNoTests=false'
    }

    sh """
        mkdir -p logs
        mkdir -p ${ALLURE_RESULTS_DIR}
        export ALLURE_TOKEN=${token}
        export IMAP_USER=${env.IMAP_USER}
        export IMAP_PASSWORD=${env.IMAP_PASSWORD}
        export EMAIL_ADDRESS=${env.EMAIL_ADDRESS}
        export ALLURE_LAUNCH_NAME="${env.ALLURE_LAUNCH_NAME}"
        allurectl watch \
            --endpoint=${env.ALLURE_ENDPOINT} \
            --results=${ALLURE_RESULTS_DIR} \
            -- mvn ${mvnCmd} \
            2>&1 | tee logs/test-run.log || true
    """
}

def cleanupSelenoid() {
    sh '''
        mkdir -p logs

        curl -X DELETE ${SELENOID_URL}/session/* 2>&1 || true

        docker ps -aq --filter ancestor=selenoid/chrome:128.0 | xargs -r docker rm -f || true
        docker ps -aq --filter ancestor=selenoid/video-recorder:latest-release | xargs -r docker rm -f || true

        docker system prune -f > logs/cleanup.log 2>&1 || true
    '''
}
