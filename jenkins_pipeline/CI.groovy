def label = "docker-jenkins-${UUID.randomUUID().toString()}"
def home = "/home/jenkins"
def workspace = "${home}/workspace/build-docker-jenkins"
def workdir = "${workspace}/src/localhost/docker-jenkins/"

def ecrRepoName = "my-jenkins"
def tag = "$ecrRepoName:latest"

podTemplate(label: label,
        containers: [
                containerTemplate(name: 'jnlp', image: 'jenkins/jnlp-slave:alpine'),
                containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
        ],
        volumes: [
                hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock'),
        ],
) {
    node(label) {
        stage('Git Checkout') {
            git "https://github.com/arielzabar/kubernetes-project.git"
        }
        stage('Docker Build') {
            container('docker') {
                echo "Building docker image..."
                sh "docker build ${WORKSPACE}/code/producer/ -t arielzabar/arielleproducer:1.1"
                sh "docker build ${WORKSPACE}/code/consumer/ -t arielzabar/arielleconsumer:1.1"
            }
        }
        stage('Docker push') {
            container('docker') {
                echo "Pushing docker image..."
                sh "docker login --username yakirshr --password 'XXX'"
                sh "docker push arielzabar/arielleproducer:1.1"
                sh "docker push arielzabar/arielleconsumer:1.1"
            }
        }
    }
}
