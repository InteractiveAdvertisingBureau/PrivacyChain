# AudienceChain

# Start the fabric env at local
Prerequisites(http://hyperledger-fabric.readthedocs.io/en/latest/prereqs.html)
1. Install docker (version 17.06.2-ce or greater is required)
2. Install docker-compose
    (ubuntu16.04 install docker-compose https://www.cnblogs.com/tianhei/p/7802064.html,no need sudo run docker https://www.jianshu.com/p/95e397570896)
3. install Go programming language 1.9.x
4. Install Node.js. Node.js version 9.x is not supported at this time, install version 8.9.x or greater.
5. install Python 2.7 version.


## Terminal 1: Start fabric env
```
cd docker

sh downloadFabricCommandPullFabricImages.sh (this command only run one time)

sh byfn.sh generate -c foo

sh byfn.sh up -c foo
```

####  follow command will stop and remove the docker container
``` 
sh byfn.sh down -c foo 
```

####  follow command will reset all docker images for demo
``` 
cd <project root folder>

npm run reset-demo
```

## Terminal 2: Start the rest api at local
``` 
mvn spring-boot:run 
```

# Deploy monitors

## Api Monitor

1. Modify __monitor.yaml__ and config what you want:

```
smtp:
  host: localhost
  port: 25
mail:
  from: 'BlockChain Monitor <do-not-reply@liveramp.com>'
  to: 
    - <email1>
    - <email2>
  cc: <just like to, cc could be configured as string as well as array of string>
  subject:
    good: '[Monitor] Good - BlockChain Status'
    doubtable: '[Monitor] Doubtable - BlockChain Status'
    bad: '[Monitor] Bad - BlockChain Status'
    error: '[Monitor] Error - BlockChain Status'
test_url: <the url monitor run test on each time>
```

2. At command line:

```
crontab -e
```
paste below lines and modify the schedule if you like then use **:x** to save
```
0 * * * *  export PATH=<parent path of the result of command `which node`, e.g.: /usr/bin or /root/.nvm/versions/node/v8.9.4/bin>:$PATH; cd <the absolute path of current project>; npm run monitor > /dev/null 2>&1

5 0 * * *  export PATH=<parent path of the result of command `which node`, e.g.: /usr/bin or /root/.nvm/versions/node/v8.9.4/bin>:$PATH; cd <the absolute path of current project>; npm run monitor-sum >> /tmp/monitor.log 2>&1
```

*comment: Ensure your machine has a package installed and running that provides a port 25 for sending email. And __postfix__ is the recommended package.  Otherwise, you need to update __monitor.yaml__ with your email smtp server host and port(user and password are not supported although).*


# Swagger build

<p><a href="/SWAGGER.md" title="Swagger Development Instructions">Swagger Development Instructions</a></p>

run the follow command, then you can view the doc in the /build folder

```
rm -rf build
swagger-codegen generate -i audience_chain_api.yml -l html2 -o build -c swagger_config.json
```
