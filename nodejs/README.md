# Nodejs Sample Application for IBM Cloud App Configuration service
> **DISCLAIMER**: This is a guideline sample application and is used for demonstrative and illustrative purposes only. This is not a production ready code.

This sample contains an NodeJS project that you can use to learn more about the IBM Cloud App Configuration service.

## Contents
- [Prerequisite](#prerequisite)
- [Create an instance of IBM Cloud App Configuration service](#create-an-instance-of-app-configuration-service)
- [Setup the app](#setup-the-app)
   * [Prerequisites](#prerequisites)
   * [Next steps](#next-steps)
- [Run the app locally](#run-the-app-locally)
- [Test the app with feature toggle and segmentation](#test-the-app-with-feature-toggle-and-segmentation)
- [Run the app in an IKS (IBM Kubernetes Service)](#run-the-app-in-an-iks-ibm-kubernetes-service)
- [License](#license)

## Prerequisite

- You need an [IBM Cloud](http://cloud.ibm.com/) account. If you don't have an account, create one [here](https://cloud.ibm.com/registration/).

## Create an instance of App Configuration service
- Log in to your IBM Cloud account.
- In the [IBM Cloud catalog](https://cloud.ibm.com/catalog#services), search **App Configuration** and select [App Configuration](https://cloud.ibm.com/catalog/services/apprapp). The service configuration screen opens.
- **Select a region** - Currently, Dallas (us-south) and London (eu-gb) region is supported.
- Select a pricing plan, resource group and configure your resource with a service name, or use the preset name.
- Click **Create**. A new service instance is created and the App Configuration console displayed.

## Setup the app
### Prerequisites
- Node.js installed on your machine.
- MongoDB Community Edition installed on your machine. Official installation links for [Windows](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/), [Linux](https://docs.mongodb.com/manual/administration/install-on-linux/) & [macOS](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/). External installation links for [Windows](https://medium.com/@LondonAppBrewery/how-to-download-install-mongodb-on-windows-4ee4b3493514), [Linux](https://www.digitalocean.com/community/tutorials/how-to-install-mongodb-on-ubuntu-18-04) & [macOS](https://zellwk.com/blog/install-mongodb/).
- jq - command-line JSON processor. Install it from [here](https://stedolan.github.io/jq/download/).

### Next steps
- Download the source code
    ```
    git clone https://github.com/IBM/appconfiguration-samples.git
    cd appconfiguration-samples/nodejs
    ```
- Setup or configure your App Configuration service instance
    - Navigate to dashboard of your App Configuration instance.
    - Go to Service credentials section and generate a new set of credentials. Note down the `apikey` and `guid`. These credentials are required in the next steps.
    - From your terminal, inside the source code exceute the `demo.sh` script by running below command
        ```
        ./demo.sh
        ```
    - Provide all the inputs during script execution. A sample example is shown in below figure
      <img src="README_IMG1.png" width=75% height=50%/>
    - Script execution takes time. Script is executed successfully only when you see the log `---Demo script complete---` at the end in your terminal.
    - This script will create the collections, feature flags & segments in the instance which are required for the Bluecharge web app
- Edit the configuration values in file [`app.js`](app.js)
    1. Replace `region`, `guid` & `apikey` at [line 22](app.js#L22) with the values you obtained from the Service credentials section of the instance.
    2. For Mongo database connection. Follow the instruction given in the file at [line 50](app.js#L50). Make sure you comment either of url, options before running the app locally or on the IKS(IBM Kubernetes Service).
- Installing Dependencies
    - Run `npm install` from the root folder to install the app’s dependencies.

## Run the app locally
- Make sure your local mongo db is running or start the mongo server by running `mongod start` from the path where it is installed.
- Run `npm start` to start the app
- Access the running app in a browser at http://localhost:3000


## Test the app with feature toggle and segmentation
- Keep the app running. Signup and create various users with different email ids.
- Additionally, signup/create two users with email id alice@bluecharge.com & bob@bluecharge.com
- From the App Configuration service instance dashboard, navigate to Feature flags section.
- Turn ON the toggle for `Left Navigation` feature flag. Once turned ON, refresh your app running on localhost:3000. You would observe that navigation links from the top menu are now hiding inside the Hamburger menu. And when the toggle is turned OFF the links are positioned back in the top menu when the app is refreshed.
- Similarly, turn ON the toggle for 'Flight Booking' feature flag. Now Login with either of alice@bluecharge.com or bob@bluecharge.com email id. Once logged in, you would see a Flight Booking button on the home page banner. If you try logging in with email ids other than alice & bob you'll not see the Flight Booking button, because alice & bob are a part of `Production Testers` segment and the Flight Booking feature is enabled only for `Production Testers` segment.
- Similary, test the `Flight Discount` feature flag. Turn ON the feature flag and refresh the app. From the running app on the flightbooking page you should see a discount coupon of some value. The discount will be 25% for user logging in with email id ending with `ibm.com` and discount will be 5% for all other users, because users whose email is ending with `ibm.com` are part of `IBM employees segment`.


## Run the app in an IKS (IBM Kubernetes Service)

All of the below steps are carried out in IKS with namespace `appconfig`.  You can create a namespace using the command `kubectl create namespace appconfig` or you can update the yaml files to refer an approproiate namespace. 

### Setup Mongo in IKS 

Mongo is deployed as a Statefulset in IKS and admin users are used to connect to Mongo.  Secrets are created for admin user credentials.

1. Create secrets required for mongo deployment using `kubectl apply -f kube/secret.yaml`
2. Create the StatefulSet for mongo deployment using `kubectl apply -f kube/statefulsetmongo.yaml`
3. Create service to access mongo deployment using `kubectl apply -f kube/servicemongo.yaml`
   
Optionally, to login to Monogo, execute `kubectl exec -it mongodb-standalone-0 /bin/bash`.  Once logged in to the pod, issue `mongo mongodb://mongodb-standalone-0.database:27017/bluecharge` to get into Mongo shell.

### Setup the sample app in IKS

1. Build the docker for the app using the comment `docker build -t us.icr.io/<namespace>/apprapp-bluecharge:<tag> .`
2. Push the docker to icr.io using `docker push us.icr.io/<namespace>/apprapp-bluecharge:<tag>`
3. Update the image name in kube/deployment.yaml file in the `image` tag
4. Update the `imagePullSecrets` in kube/deployment.yaml file according to your IKS environment.  If you have used the registry in your the same account of where the cluster is present, then you can copy the `all-icr-io` secret from default to the appconfig namespace using the command `kubectl get secret all-icr-io  --namespace=default -o yaml | grep -v '^\s*namespace:\s' | kubectl apply --namespace=appconfig -f -`
5. To deploy the app, use `kubectl apply -f kube/deployment.yaml`
6. To access the app using from outside the IKS, create the service using `kubectl apply -f kube/servicenode.yaml`
7. To access the node Blue Charge app, use the url `http://<IKS Node public ip>:<nodeport of the service>/`

Optionally create an ingress rule to access the app using the cluster ingress domain end point.

# License
Copyright 2021 IBM Corp.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

See [here](https://cloud.ibm.com/docs/app-configuration) for detailed docs on App Configuration service.
