# Signal Server
Written using Signal Server v5.48 in Ubuntu 18.04 x64

## Requirement
- Git
- JDK 11
- Mavn

```
sudo apt update
sudo apt install git default-jre maven -y
```

## Steps
1. Clone 
```
git clone https://github.com/signalapp/Signal-Server
cd Signal-Server
```

2. Install
```
mvn install
```

3. Compile
```
mvn -e -B package
```

4. Create config.yml, [see example](./config.yml)
