all:

d8m-validate-package:
	cd databus-mods-lib/databus-mods-validate/
	mvn clean package
	cd -

d8m-validate-completion:
	bin/validate generate-completion > /etc/bash_completion.d/mod-validate

d8m-validate: d8m-validate-package d8m-validate-completion

build-with-docker:
	docker run -it --rm -v `pwd`:`pwd` -w `pwd` maven:3.8.4-openjdk-11 bash -c 'mvn clean package -Duser.home="build"'
