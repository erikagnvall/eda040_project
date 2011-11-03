#!/bin/sh
# Builds the software for the campera and uploads it.
DEST=build

rm -rf $DEST
mkdir $DEST
cd $DEST
#../configure --with-thread=pthread --with-gc=nonmoving --host=cris-axis-linux # TODO obtain a configure-script from http://torvalds.cs.lth.se/moin/Courses/EDA040/Compiling
