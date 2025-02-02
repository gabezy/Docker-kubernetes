
## State (Persisting data in k8s)

State is data created and used by your application which must no be lost. This means that this kind of data need to survive to to a container restart in the Pod. 
- User-generated data, user accounts,...
	- Often stored in a database, but could also be files (uploads).
- Intermediate results derived by the app
	- Often store in memory temporary in database tables or files.

To handle this problem, we can use **volumes** to store the data. It's import to highlight that volumes, by default, are **Pod-specific**, meaning that if a Pod has been deleted, the volumes will be deleted too.

### Kubernetes Volumes vs Docker Volumes

![[k8s_volumes.png]]

## [Storage](https://kubernetes.io/docs/concepts/storage/)
### [Volumes](https://kubernetes.io/docs/concepts/storage/volumes/)

At its core, a volume is a directory, possibly with some data in it, which is accessible to the containers in a pod. How that directory comes to be, the medium that backs it, and the contents of it are determined by the particular [volume type](https://kubernetes.io/docs/concepts/storage/volumes/#volume-types) used.
#### Define a volume

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
	name: story-deployment
spec:
	replicas: 1
		selector:
			matchLabels:
				app: story-node
	template:
		metadata:
			labels:
				app: story-node
		spec:
			containers:
			- name: story
			  image: gabezy/kub-data-demo:latest
			  imagePullPolicy: Always
			# define where which volume should be mount
			volumeMounts:
				- mountPath: /app/story # path define in the application and the Dockerfile (workdir)
				  name: story-vol # point to the respective volume that will be used to mount
			volumes:
				- name: story-vol
				  # creates a mew empty directory (folder) and keep this directory alive as long as the Pod is alive
				  emptyDir: {} # use the emptyDir type with the default configs
```

- emptyDir: Create a empty directory per Pod.
- hostPath: Mounts a file or directory from the host node's filesystem into your Pod (Not recommended).
	```yaml
	volumes:
        - name: story-vol
          hostPath:
              path: /data
              type: DirectoryOrCreate
	```
- CSI (Container Storage Interface): Defines a standard interface for container orchestration systems (like Kubernetes) to expose arbitrary storage systems to their container workloads and can be used in any Cloud provider, since already has a build CSI (Not a Kubernetes builtin plugin).

### [Persistent Volumes](https://kubernetes.io/docs/concepts/storage/persistent-volumes/)

A **PersistentVolume**(PV) is a piece of storage in the cluster that has been provisioned by an administrator or dynamically provisioned using Storage Classes. The main characteristics of Persistent Volumes are:
- It is a resource in the Cluster (Just like a Node).
- Has independent lifecycle from any Pod that uses this PV (Pod-and-Node independent).
- Can use various type of implementation (NSF, iSCI, SCI).

A **PersistentVolumeClaim (PVC)** is a request for storage (to PV) by a user (Node). It is similar to a Pod, where Pods consume node resources and PVCs consume PV resources. PVC can request specific size and access modes (e.g., ReadWriteOnce, ReadOnlyMany, ReadWriteMany, or ReadWriteOncePod). 

![[pv_and_pvc.png]]

#### Define PersistentVolume and PersistentVolumeClaim

To define a PV, in the resource `.yaml` file, use the `kind: PersistentVolume` and to PVC use the `kind: PersistentVolumeClaim`. 

*Notes*: The **hostPath** type uses a file or directory on the Node to emulate network-attached storage and are only used in local environment, so don´t is used in Production environments.

```yaml
# Establishing the PersistentVolume and its configuration
apiVersion: v1
kind: PersistentVolume
metadata:
  name: host-pv
spec:
  capacity:
    storage: 250Mi # Gi (Gigabyte)
  #https://www.computerweekly.com/feature/Storage-pros-and-cons-Block-vs-file-vs-object-storage
  volumeMode: Filesystem # or Block
  storageClassName: standard
  accessModes:
    - ReadWriteOnce # The volume can be mounted as a read-write volume by a single node (multiple pods on the same node)
    #- ReadOnlyMany #  The volume can be mounted as read-only by many nodes.
    #- ReadWriteMany # The volume can be mounted as read-write by many nodes.
    #- ReadWriteOncePod # Assure only on pod across the whole cluster can read and write to the PV
  hostPath:
    path: /data
    type: DirectoryOrCreate
---
# Establishing the claim and its configuration
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: host-pvc
spec:
  volumeName: host-pv
  accessModes:
    - ReadWriteOnce
  storageClassName: standard
  #Which resources this PVC will claim from the PV
  resources:
    requests:
        storage: 250Mi
######################################################################################################################################################################
## in the deployment or pod declaration file
apiVersion: apps/v1
kind: Deployment
metadata:
  name: story-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: story-node
  template:
    metadata:
      labels:
        app: story-node
    spec:
      containers:
        - name: story
          image: gabezy/kub-data-demo:latest
          imagePullPolicy: Always
          # define where which volume should be mount
          volumeMounts:
            - mountPath: /app/story # path define in the application and the Dockerfile (workdir)
              name: story-vol # point to the respective volume that will be used to mount
      volumes:
        - name: story-vol
          persistentVolumeClaim: # point to a persistentVolumeClaim
            claimName: host-pvc #  specific which PVC use

```

#### Volumes vs Persistent Volumes
![[volumes_vs_pv.png]]

### Environment Variable

The environment variables are declare in the `pod` or `deployment` `.yaml` file, in the `containers:` object.

```yaml
spec:
      containers:
        - name: story
          image: gabezy/kub-data-demo:latest
          imagePullPolicy: Always
          env:
            - name: STORY_FOLDER
              value: story
```

### ConfigMap

The ConfigMap resource is a Kubernetes Resource that can story multiples key-values pairs and be used in multiples Pods with the `valueFrom:`.

- Create a ConfigMap Resource
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: data-story-env
data:
  folder: 'story'
  # key: value...
```

- Use a value from ConfigMap
```yaml
spec:
      containers:
        - name: story
          image: gabezy/kub-data-demo:latest
          imagePullPolicy: Always
          env:
            - name: STORY_FOLDER
              valueFrom:
                configMapKeyRef: # Point to the ConfigMap
                  name: data-store-env # ConfigMap's name
                  key: folder # ConfigMap's  data key
```
