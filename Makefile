all:

build-with-docker:
	docker run -it --rm -v `pwd`:`pwd` -w `pwd` maven:3.8.4-openjdk-8 bash -c 'mvn clean package -Duser.home="build"'
