1. Create a account at [Amazon Web Services](https://aws.amazon.com/)
2. Add a SSH key
	1. In **EC2** page, select "**Key Pairs**"
	2. In "Actions" select panel, choose "import key pair"
	3. open the terminal and use the following commands
		```bash
		ls -al ~/.ssh
		cat ~/.ssh/some_ssh_key.pub
		```
	4. Add the SSH key and select "Import key pair"
3. Create a new "Security Groups"
	1. Configure the **inbound rule** so that the client (e.g., browser, you, etc.) can communicate with the server. By default, add a rule for port **22** and set the type to **SSH** to connect to the server. Additionally, add a rule for your application's port and set the type to **HTTP**.
	2. Configure the **Outbound rules** that the server can communicate to some other IP or WWW.
4. Launch a EC2 instance with the SSH key and Security Groups configuration
5. Connect to the EC2 instance and install Docker
	```bash
	# Check for updates
	sudo yum update -y
	# Install Docker
	sudo yum -y install docker
	 
	# Start docker
	sudo service docker start
	 
	## Set admin permisson to docker user
	sudo usermod -a -G docker ec2-user
	
	
	sudo systemctl enable docker
	
	docker version
	```
6. Pull the application's image and Run the Docker container
## Things to Watch Out For

- Bind Mounts **shouldn't be** used in Production
- Containerized apps might need a **build step** (e.g React Apps)
- **Multi-Container projects** might need to be **split** across multiple hosts