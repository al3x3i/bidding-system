# bidding-system

## Overview
Spring boot application to simulate bidding process

## Guidelines
1. Clone this repository
2. Go to the repository folder ('/bidding-system') and run the following commands:
2.1 Prepare the test environment
```
$ ./bidding-test/test-setup.sh 
```
2.2 Run spring boot application
```
$ ./gradlew bootRun
```
3. Run the test
```
$ cd /bidding-test
$ ./run-test.sh 
```
3.1 Run multiple tests
```
$ for i in {1..100}; do ./run-test.sh; done
```
3.2. Run multiple tests in parallel
```
$ for i in {1..100}; do ./run-test.sh & done
```
3.1 A message should appear after the test: `"Your application seems to behave correctly!"`
4. Stop the test environment and spring boot application:
```
$ docker stop $(docker ps -aq)
$ fuser -k -n tcp 8080
```
