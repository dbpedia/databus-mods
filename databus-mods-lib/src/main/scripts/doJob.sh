#!/usr/bin/bash

resource=$1
source=$2

putResponse=$(curl -s -X PUT --data-urlencode "source=$source" -w %{http_code} $resource)

if [ $putResponse -eq "202" ]; then
	echo -e ">PUT\n<202" 1>&2
	until false; do
		getResponse=$(curl -s -o /dev/null -w %{http_code} $resource)
    		echo -e ">GET\n<$getResponse" 1>&2
    		if [ $getResponse -eq "200" ]; then
			curl -s $resource
			exit 0
		fi
		sleep 1s
	done
else
	echo -e ">PUT\n$getResponse" 1>&2
	exit 1
fi
