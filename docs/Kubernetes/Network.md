In the Kubernetes, the **Service** Resources is responsible for the network configuration. Services exposes Pods to the Cluster or Externally. By default, **Pods** are internal only, meaning that can be access only inside the pod. In other hand, services can allow cluster and external access to Pods.
 - To create a service to expose some deployment, we can use `kubectl expose deployment name --port=8080 --type`, the types available are:
	 - `ClusterIP`: Reachable only inside the Cluster.
	- `NodePort`: Should be expose with help of the node that run the deployment (externally accessible).
	- `LoadBalance`: Utilize a load balance that already exists and use the address of the load balance to distribute to the available instances.

- Create **Service** Resources

```yaml
apiVersion: v1
kind: Service
metadata:
	name: backend
spec:
	# Which Pods the service will be select
	selector:
		app: node-deployment
	ports:
		- protocol: 'TCP'
		  port: 3000
		  # The port from the container in the pod
		  targetPort: 8080
	type: LoadBalancer #NodePort or ClusterIP
```

### Pod-internal communication 

When we have two or more containers in the same pod, each container communicate with one another using `localhost`. Within a Pod, containers share an IP address and port space, and can find each other via `localhost`.

### Pod-to-Pod communication 

When containers need to communicate to each other in different Pods, we have to ways to establish communication:
1. Through the `CLUSTER-IP` address in the service, if the **Pod** and  **Service** is internal (`ClusterIP`).
2. Use the automatically generate service's environments variables that Kubernetes generates. The environment variable is the `<SERVICE_NAME>_SERVICE_HOST` that appoints to the container that uses the service. E.g : `AUTH_SERVICE_SERVICE_HOST`
#### [CoreDNS](https://kubernetes.io/docs/tasks/administer-cluster/coredns/)

With CoreDNS, Kubernetes can locate and communicate with others Pod by the service name and the namespace that the pod belongs, like `<service>.<namespace>`. E.g: `auth-servive.default`

```yaml
    spec:
      containers:
        - name: user-service
          image: gabezy/kub-demo-users:latest
          imagePullPolicy: Always
          env:
            - name: AUTH_ADDRESS
              value: 'auth-service.default'
```