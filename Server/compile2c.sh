#!/bin/bash
# Cross-compiles the server software for the camera, upload and starts it. 
# Usage: ./compile2c.sh [host1 [host2 [--no-compile || -n]] [< passwordfile]
DEST=c
HOST1="argus-7"
HOST2="argus-8"

stdin="$(ls -l /dev/fd/0)"
stdin="${stdin/*-> /}"
ftype="$(stat --printf=%F $stdin)"

if [[ "$ftype" == 'character special file' ]]; then
	stty -echo 
	read -p "Passwort für die komputermaschine, bitte: " -r PW
	stty echo 
	echo
elif [[ "$ftype" == 'regular file' ]]; then
	read -r PW
else
	echo Unknown: $stdin
fi
if [ -n "$1" ]; then 
	HOST1=$1
else 
	echo "Using default host1, ${HOST1}"
fi
if [ -n "$2" ]; then 
	HOST2=$2
else 
	echo "Using default host2, ${HOST2}"
fi


if !([ -n "$3" ] && ([[ "$3" == "--no-compile" ]] || [[ "$3" == "-n" ]])); then
	/usr/local/cs/bin/initcs.sh
	rm -rf $DEST
	mkdir $DEST
	cp -R src/ $DEST
	cd $DEST
	/usr/local/cs/rtp/lab/build_camera.sh
	if [ "$?" -ne "0" ]; then
		cat build.err
		exit 1
	fi
	cd ..
fi

PW=$PW ../cli/ftp.pl $HOST1 $HOST2
PW=$PW ../cli/run.pl $HOST1 $HOST2
