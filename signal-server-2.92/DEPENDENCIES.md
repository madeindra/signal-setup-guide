# Signal Server Dependencies
This guide is written in Ubuntu 18.04.

## Before you start
Update package in linux by running this command:

```
sudo apt update
```

## Git
To clone repository / project files from github, install Git

```
sudo apt install -y git
```

## Java JDK 11
To run java application, install JDK 11

```
sudo apt install -y default-jre
sudo apt install -y default-jdk
```

## Maven
To compile signal server, install Maven

```
sudo apt install maven
```

## Nginx
To forward signal port to http / https port, install Nginx

```
sudo apt install nginx
```

## Certbot
To generate let's encrypt free ssl certificate for https, install certbot

```
sudo add-apt-repository ppa:certbot/certbot
sudo apt update
sudo apt install python-certbot-nginx
```

## Coturn
To run a turn server for video/voice call, install coturn

```
sudo apt install coturn
```

## Docker
To run database, redis, and minio in docker, install docker

```
sudo apt install apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"
sudo apt update
sudo apt install docker-ce

sudo curl -L https://github.com/docker/compose/releases/download/1.28.5/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

To allow non sudo user to use docker, run these commands :

```
sudo usermod -aG docker ${USER}
su - ${USER}
id -nG
```

## Firewall
To enable firewall for Nginx & OpenSSH run these commands

```
sudo ufw enable
sudo ufw app list
sudo ufw allow 'Nginx Full'
sudo ufw allow 'OpenSSH'
```

**Why OpenSSH? Because if you forgot to allow it, you can't ssh to your server**

