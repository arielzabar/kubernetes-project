helm repo add bitnami https://charts.bitnami.com/bitnami
helm install rabbitmq bitnami/rabbitmq
port forwarding:  kubectl port-forward --namespace default svc/rabbitmq 15672:15672 & 
connect:          http://127.0.0.1:15672/#/

helm install jenkins stable/jenkins

echo "Password      : $(kubectl get secret --namespace default rabbitmq -o jsonpath="{.data.rabbitmq-password}" | base64 --decode)"

1. Get your 'admin' user password by running:
  printf $(kubectl get secret --namespace default jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode);echo
  
2. Get the Jenkins URL to visit by running these commands in the same shell:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/component=jenkins-master" -l "app.kubernetes.io/instance=jenkins" -o jsonpath="{.items[0].metadata.name}")
  
  echo http://127.0.0.1:8080
  
  kubectl --namespace default port-forward $POD_NAME 8080:8080
