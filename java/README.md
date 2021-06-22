### Requirements
- JDK11+
- maven

OR

- bash
- docker

### Usage

```
# with openjdk & maven
KEY_ID=... KEY_SECRET=... mvn compile exec:java -Dexec.args="test.json"

# with docker and bash
KEY_ID=... KEY_SECRET=... run.sh mvn compile exec:java -Dexec.args="test.json"
```
