## [What Kubernetes is](https://kubernetes.io/docs/concepts/overview/)

Kubernetes is an open-source system for orchestration container deployments that help with:
- Automatic Deployment
- Scaling & Load Balancing
- Management of deploy containers
Kubernetes (K8s) standardize way of describing the "to-be-created" and "to-be-managed" resources of k8s cluster and is cloud provider-agnostic. Once K8s is configured, it will work with any cloud provider (cloud-provider-specific settings can be added).
## [What K8s is and is not](https://kubernetes.io/docs/concepts/overview/#what-kubernetes-is-not)

Kubernetes **is**:
- Open-source project that can be used with any provider
- Collections of concepts and tools
- Works with Docker
Kubernetes **is not**:
- Cloud provider
- Cloud service
- Just software
- Alternative to Docker
## [Kubernetes Architecture](https://kubernetes.io/docs/concepts/architecture/)

<img src="https://kubernetes.io/images/docs/kubernetes-cluster-architecture.svg" width="800"/>

- **Cluster**: A set of **Nodes** machines which are running the **Containerized** Application (**Worker Nodes**) or control other Nodes (**Control Plane**).
- **cloud-controller-manager**: component that embeds cloud-specific control logic and let you link your cluster into your cloud provider's API. if K8s is running local in our PC, cluster does not have a cloud controller manager.
- **Control plane**: **Master Node** that manages the **Worker Nodes** and the **Pods** in the Cluster, making decisions and responding to cluster events (for example, starting up new Pod when needed).
	- **kube-apiserver**: A component of the Kubernetes control plane that exposes the Kubernetes API and is the front end for the Kubernetes control plane. API for the **kubelets** to communicate.
	- **etcd**: Highly-available key value store used as Kubernetes' backing store for all cluster data.
	- **kube-scheduler**: Component that watches for newly created Pods with no assigned node, and selects a node for them to run on.
	- **Controller Manager** Component that runs controller processes (Node controller, Job controller, EndpointSlice controller, ServiceAccount controller).
- **Nodes**: Run the containers (Pods) of the application. **Nodes** are **physical or virtual machine** with a certain hardware capacity which host one or multiple **Pods** and **communicates** with the Cluster
	- **kubelet**: An agent that runs on each node in the cluster. It makes sure that containers are running in a Pod. Basically make the communication between **Control Plane** and the **Node**.
	- **kube-proxy**: is a network proxy that runs on each node in your cluster, managing **Node** and **Pod** network communication.
	- **Container runtime (CRI)**: t is responsible for managing the execution and lifecycle of containers within the Kubernetes environment.
	- **Pod**: Is the smallest unit in K8s that hold the actual running App containers + their required resources. A Pod can hold one or multiple containers.
- **Services**: A **logic set(groups) of Pods** with a unique, Pod and container **independent IP address**.
- **kubectl**: The Kubernetes command-line tool, allows you to run commands against Kubernetes clusters.
## [Installation Required Tools](https://kubernetes.io/docs/tasks/tools/)

To run Kubernetes in the local machine, will be need the **kubectl** which will the service that communicates to the Cluster and **minikube** tool that lets run Kubernetes locally.
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/)
- [minikube](https://minikube.sigs.k8s.io/docs/start/?arch=%2Flinux%2Fx86-64%2Fstable%2Fbinary+download)

### Commands (Imperative Approach)

```bash
minikube status # check status

minikube start # start minikube

minikube dashboard # open kubernetes dashboard

# send a create deployment to the cluster (by default, the cluster looks for image inside the cluster or in Docker Hub)
kubectl create deployment deploy-name --image=image-name

kubectl get deployments # list all deployments

kubectl get pods # list all pods

kubectl expose deployment first-app --port=8080 --type=LoadBalancer # create a service that expose a deployment

minikube service first-app

# Scale the first-app delpoyment to 2 replicas (2 pods running the container)
kubectl scale --current-replicas=1 --replicas=2 deployment/first-app

# Update a image in a existing deployment (Only update if the image tag is different from the previous)
kubectl set image deployment/first-app kube-first-app=gabezy/kube-first-app:2

kubectl rollout status deployment/first-app # show the rollout status for the given deployment

kubectl rollout undo deployment/first-app # rollback to the previous deployment

kubectl rollout history deployment/first-app # Get the rollout history for a given deployment

kubectl rollout history deployment/first-app --revision=3 # detail a specific revision history

kubectl rollout undo deployment/first-app --to-revision=1 # rollback to a specific revision

kubectl delete service first-app

kubectl delete deployment first-app
```

## Create Resources (Declarative Approach)

```yaml
# resource.yaml
apiVersion: v1
kind: Service
metadata:
	name: backend
spec:
	selector:
		app: node-deployment
	ports:
		- protocol: 'TCP'
		  port: 3000
		  targetPort: 8080
	type: LoadBalancer
--- # this 3 dashs is used in yaml to separate two different objects
apiVersion: apps/v1
kind: Deployment
metadata:
name: kube-first-app-deploymemt
spec:
	replicas: 1
	selector:
		# declaring which pods should manage by the deployment (has to matches the lables and values)
	    matchLabels:
	      app: node-deployment
		  tier: backend
		matchExpressions:
			- {key: app, operator: In, values: [deployment, prod]}
	# pod's metadata
	metadata:
		label:
			app: deployment
			tier: backend
	spec:
		# which containers will be initilized in the pod (List)
		containers:
			- name: app-node-container
			  image: gabezy/kube-first-app:latest
			  # Health check configs
			  livenessProbe:
				  httpGet:
					  path: /
					  port: 8080
			- name: nginx
			  image: nginx
```

To make Kubernetes to create the deployment and the service, use the following command:
```bash
kubeclt apply -f resource.yaml
```
To delete, use the following command:
```bash
kubeclt delete -f resource.yaml
```

documentation:
- [Deployment](https://kubernetes.io/docs/reference/kubernetes-api/workload-resources/deployment-v1/)
- [Service](https://kubernetes.io/docs/reference/kubernetes-api/service-resources/service-v1/)
## Kubernetes Objects
- Pods
- Deployments
- Services: Responsible to expose Pods to the Cluster or Externally. By default, **Pods** are internal only, meaning that can be access only inside the pod. In other hand, services can allow cluster and external access to Pods.
	- To create a service to expose some deployment, we can use `kubectl expose deployment name --port=8080 --type`, the types available are:
		- `ClusterIP`: Reachable only inside the Cluster.
		- `NodePort`: Should be expose with help of the node that run the deployment (externally accessible).
		- `LoadBalance`: Utilize a load balance that already exists and use the address of the load balance to distribute to the available instances.
- Volume