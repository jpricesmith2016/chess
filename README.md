# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Server Diagram / Sequence Diagram

[Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfJCIK-P8gK0eh8KVEB-rgeWKkwes+h-DsXzQo8wHiVQSIwAgQnihignCQSRJgKSMCyqqGpuWozAAGaoAgHACmgEDMNgEDYL2cgoAxXlvoYSa+s6XYueKfk5C4ymGAAvHlMA9n2MAAD6lQJjloFlYAuOpOwwAVRW9v2iVpSB1T+gAkmgVAmkgHA1VGMZxoUbUSfCybIKmMDpvhoxjDmqh5vM0FFiW9R6OuKKEv5DZefR27JVSt78gyTJTrlc40vu97CjAYoSm6MpymW7xKpgXkqsGGputqur6rlRgQGoRURTAVpop9MinYuNl2cVrUdrZ76pRN9Q9X1yAcG6w0oLGCnQ6BmHTdhs1OAAjARS0rQWYzrdA9Q+NMl7QEgABe8X7RDTZfT5G6wPE+jat4DgwPVALQ8ODopcjdnPdodWmRpMAAISFRLCVy6jyPdb1-UcE0ys7HjBPxuNiZTSUYBppT1P8rTa3FozCos3qbOc7sVwHU2SWCtL9IwIecXPvE56XteAcCku92PgGEdbn7st6aWGXVf+CDMLlawUVR6CYCnHXozABnvcRpHfLniH1iRqENsTsuiTheFKUR8y18ZYxV9RHdoYdnjeH4-heCg6AxHEiQj2PLm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDN3+cN7pVn+gfhSF-DLr2UJs-OTfp41aSScnTdI7nWAofh-B1doNdvJ3jHeoj0nwJ3kLKeUZ9obfXVALHoFB5IFDBoFXwvMYavxlpfLszU+xE3arLReMBMYG3gbvAoptRq4ImiTa2tsqYLRpvmJ2G1XYUQ9lzb2PMGJRyLijLBodXx4Jfv-M6RgUDcGPJeDE-DtB-wXNHSoy4ZBiKZIYaR8gjrthTvUGeD8M6ARPpNNGNQXhXAblbGauF5ojDok2AezF-AonXP4bA4oNT8TRDAAA4kqDQ88bKSU8evLe9glT70vHnQoR8HiwlPuEn+BcDELzSvUZAORvE5i-pRH+T9BFoOEYHRkH9JFn1kbdQBD1xQgJfNocBxo4lISgfzN0cD+QKSQTAAKKCtbcPwck7BSMk7+NLEQ7GJCFLkMJhbDCjdSY23JnQ7MDtGGFmdqWZmrCoAc3YdDQ6PTMH1DUcADRu50EFKZOklAPIpGgOAKUgBCj7rAJgCEnM-09Tx2qfIHKbcbynOjvsrxSoZQNE6Mcj8BjtFogyKoACzyfEgoSTEwxuswJjBeWoAsDRxjoq6tIAsFNwjBECCCTY8RdQoDdJyPY3xkigDVJSyCixvjooAHJKiZRcTopjOrIqbjASxWZljotUJi7FSpcX4sJcS5YpLyUMqMjShAdL5WrTGMypUbK5iQk5TYhidih4cAAOxuCcCgJwMQIzBDgFxAAbPACchgLkwCKLMpJxcpIdGCaEt2WSkJZlZUqblVDwVIvqGfNYAa5iIq-G63h9Rg7oguZkiJEaNVKk8t5H6MAaodOCqFdpUUYpKvRFrQZRikSRvyoVRGlDeE8L1ljAaQ1oz4woVM3lszbZWMWks1aKzmFbRgDtdy3NDrPzyXI9+n8Sm-PyfIoUQDKkfPiFoMBr1IF8yzc0sZiDwrIOZt02G-zy1XxrRbQcQyMb61Ga0shLazZjRRdQma6YFk9tzMs+mqymY+rYV7HZTY9knr4Tc45wH6iePQJQOKcK5i4rA9rLBjI0DQcTUCmpGIcXSFTXMTVKA1goc2PAeI-ILk5M7P7I98aHVJouZHI9d0DlrkBXMARFHk4QvtUeFA0LYUXPgxfS9JchXirxfUAlRKYBBstpUPlArRgibg2JmAEnAhSdMP3JiQ9LBiIckR2ISAEhgB032CARGABSEBxQscMP4WlIA1QuutrGySTRmQyR6OisJ38-WjGwEqnTUA4AQAclAHDKBcXSemcfUNdSfPoHC7itY-ngCBeC6F6N1lwMwAAFZWbQEm8NsGIvYfFgFyg6XoAZugb5FAu0ci5rEfmvd4toqxRLbWi94GsONWrS1MqFUUtpZC9AXr-TOs6wISMptdX3ITPNk+mZNDybdoYX2r9A6QxDtm3tDhY7clRyncU+p6A7kyweYuiUodV3AFqRuzNMDt23rQO0zpB6pYMd6Z2bsLUJvJSE9NjgO60DzcfWY2Tnb5n2w-ethmazf2bM9qOwDn2AWHIQ4d555ylSYdE2dxcF2KkSjozU16WHGlZv49IV7XSPt-J4QjX757Js8qvY2oHz3Qe1umeYsmABWFu9De10zhxBisLW3ucGR1w1H2WSfqM3TAv7DOr5AwAAJldSxVkbsB1bjeZ-9oxzxAxmksGGJCXP23Pv54LxZMORffsrKbmAtZ6x7d9rkpKYvWOKwxEN7XoXiQqsasunk3zDJiHHZjvwq6UC0fQ-IfH87FHMmwLHmzL15Tk7LdEr89Qfy4igLxzOmWedG7AlFjty35PWMbHqrTAQvCpfHoZyeUBm+IGDLAYA2B-OEDyIgpz5gXOlmXqvdem9ejGG0oKLRojxEoBTqXnSiH6jSGUeiJKGOqPz5URciMGBNlqAxEnxj8BuB4Az4nA7O+OAb4pQMA-FhUCqBP7OuRZ-O+X4VuoqPt-7+hxP5H6v6n7lJf6wDo7jpz7gHF76JIoj4mIz685zI166qYBAA)