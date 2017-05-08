# Extension for security Key Store on Identity Server


An example project containing components and features for an Identity Server extension for [WSO2 Identity Server](https://github.com/wso2/product-is) product.
 

## Building from the source

1. Install Java7 or above
1. Install Apache Maven 3.x.x(https://maven.apache.org/download.cgi#)
1. Get a clone or download the source from this repository (https://github.com/ruwanta/wso2is-examples.git)
1. cd ``is530/example-keystore-extension``
1. Run the Maven command ``mvn clean install``.
1. Copy the generated ``org.wso2.carbon.identity.sample.extension.keystore-x.y.z.jar`` within ``target`` directory to the Identity Server ``\repository\components\dropins``


## Limitations
This key store extension is for only for verification that the extension works. You need to create your own extension according to your requirements to retrieve the security private-keys and certificates.