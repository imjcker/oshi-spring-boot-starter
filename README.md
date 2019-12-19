# sys-spring-boot-starter
[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)][license]
[![Version](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven.svg?label=Version)](https://imjcker.github.io)

live server system information

![index](docs/qrcode.jpg)
![index](docs/sys.gif)

## docker
run within docker by execute script below.
explanation: run service container as **monitor** and publish port on host server port **8081** or any other available ports you replace with. 
```shell script
sudo docker run -d --name monitor -p 8081:8080 imjcker/sys-spring-boot-starter:latest
```



[license]: https://www.apache.org/licenses/LICENSE-2.0
