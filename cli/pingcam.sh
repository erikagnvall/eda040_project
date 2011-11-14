#!/bin/bash
for i in {1..10}; do ping -c 1 "argus-$i.student.lth.se"; done
