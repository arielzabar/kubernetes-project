def label = "docker-jenkins-${UUID.randomUUID().toString()}"
def home = "/home/jenkins"
def workspace = "${home}/workspace/build-docker-jenkins"
def workdir = "${workspace}/src/localhost/docker-jenkins/"

def ecrRepoName = "my-jenkins"
def tag = "$ecrRepoName:latest"

podTemplate(label: label,
        containers: [
                containerTemplate(name: 'jnlp', image: 'jenkinsci/jnlp-slave'),
        ]
) {
    node(label) {
        stage('Git Checkout') {
            git "https://github.com/arielzabar/kubernetes-project.git"
        }
        stage('Helm Upgrade') {
            container('jnlp') {
                echo "Installing helm"
                sh "curl -L https://get.helm.sh/helm-v3.2.4-linux-amd64.tar.gz -o /tmp/helm.tar.gz"
                sh "tar -zxvf /tmp/helm.tar.gz -C /tmp"
                echo "Helm upgrade"
                sh "/tmp/linux-amd64/helm upgrade consumer ${WORKSPACE}/helm-charts/consumer/"
                sh "/tmp/linux-amd64/helm upgrade producer ${WORKSPACE}/helm-charts/producer/"
            }
        }
    }
}
