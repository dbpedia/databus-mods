#!/usr/bin/env bash

resource=$1
source=$2

putResponse=$(curl -s -X PUT --data-urlencode "source=$source" -w %{http_code} $resource)

if [ $putResponse -eq "202" ]; then
	echo -e ">PUT $resource\n>\   source=$source <202" 1>&2
	GET_COUNT=0
	until false; do
		GET_COUNT=$((GET_COUNT + 1))
		getResponse=$(curl -s -o /dev/null -w %{http_code} $resource)
    		echo -ne "\r>GET $resource [retry=$GET_COUNT] <$getResponse" 1>&2
    		if [ $getResponse -eq "200" ]; then
			echo
			curl -s $resource
			exit 0
		fi
		sleep 1s
	done
else
	echo -e ">PUT\n$getResponse" 1>&2
	exit 1
fi
