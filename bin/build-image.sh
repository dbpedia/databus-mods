#!/usr/bin/env bash

cd "$(dirname $0)/../" || (echo no such dir && exit 1)

if [ -d databus-mods/databus-mods-$1 ]
then
  cd databus-mods/databus-mods-$1
  mvn spring-boot:build-image
fi