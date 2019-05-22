    *Swagger Development**
=========

## <a name="check"></a>Check your swagger-codegen version

Destinations API is using swagger-codegen version 2.2.2. You can check what version you're using by running `brew info swagger-codegen`.
```
$ brew info swagger-codegen
swagger-codegen: stable 2.2.3 (bottled), HEAD
Generation of client and server from Swagger definition
https://swagger.io/swagger-codegen/
/usr/local/Cellar/swagger-codegen/2.2.2 (6 files, 14.7MB) *
  Poured from bottle on 2017-03-17 at 14:22:52
/usr/local/Cellar/swagger-codegen/2.2.3 (6 files, 13.2MB)
  Poured from bottle on 2017-08-04 at 16:55:02
```

You can see that your system is using 2.2.2 because the `*` is next to that version in the output. If you're already using 2.2.2 feel free to jump down to [generate](#generate).


## <a name="switch"></a>Switching to version 2.2.2
You can switch to version 2.2.2 with:
```
$ brew switch swagger-codegen 2.2.2
Cleaning /usr/local/Cellar/swagger-codegen/2.2.2
Cleaning /usr/local/Cellar/swagger-codegen/2.2.3
1 links created for /usr/local/Cellar/swagger-codegen/2.2.2
```
You can now [check](#check) to make sure your `brew info` command says 2.2.2.

If you get the error:
```
$ brew switch swagger-codegen 2.2.2
Error: swagger-codegen does not have a version "2.2.2" in the Cellar.
```
then you'll have to [install](#install) version 2.2.2.

## <a name="install"></a>Install version 2.2.2
Please run these commands one by one.
```
mkdir -p /usr/local/Cellar/swagger-codegen/2.2.2/{bin,libexec}

curl http://repo1.maven.org/maven2/io/swagger/swagger-codegen-cli/2.2.2/swagger-codegen-cli-2.2.2.jar -o /usr/local/Cellar/swagger-codegen/2.2.2/libexec/swagger-codegen-cli.jar

echo '#!/bin/bash\nexec java  -jar /usr/local/Cellar/swagger-codegen/2.2.2/libexec/swagger-codegen-cli.jar "$@"' > /usr/local/Cellar/swagger-codegen/2.2.2/bin/swagger-codegen

chmod 555 /usr/local/Cellar/swagger-codegen/2.2.2/bin/swagger-codegen
```

After this you should have a directory structure of 
```
/usr/local/Cellar/swagger-codegen/2.2.2
├── bin
│   └── swagger-codegen
└── libexec
    └── swagger-codegen-cli.jar
```
You can now [switch](#switch) to version 2.2.2.

NOTE: I don't feel good about the way I did this, but I couldn't figure out how to get brew to install a specific version and this seemed like the fastest way (faster than building from source). Annotations:
1. The `mkdir` recursively creates the necessary directories.
2. Then we `curl` the jar from the maven repository.
3. There is a bash script that comes with the brew installation. It just `exec`s the swagger jar with the arguments you give so the java part of `swagger-codegen-cli.jar` is hidden. It looks like the below when not in the ugly string form above:
```bash
#!/bin/bash
exec java  -jar /usr/local/Cellar/swagger-codegen/2.2.2/libexec/swagger-codegen-cli.jar "$@"
```
4. Finally we `chmod` the bin executable to match the default brew install permissions.

## <a name="generate"></a>Generate swagger clients
Run the swagger-codegen command to generate a new swagger client in the root of the repository.

```
$ rm -rf build
$ swagger-codegen generate -i audience_chain_api.yml -l html2 -o build -c swagger_config.json
```
