helm repo add bitnami https://charts.bitnami.com/bitnami
helm install rabbitmq bitnami/rabbitmq
port forwarding:  kubectl port-forward --namespace default svc/rabbitmq 15672:15672 & 
connect:          http://127.0.0.1:15672/#/
