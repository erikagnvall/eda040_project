###########################################################################
## This is a Makefile used to create the files Makefile.am               ##
## and configure.in in the individual test case directories.             ##
##                                                                       ##
## Basically, it inserts the correct name of the main source             ##
## file (which is also assumed to be the name of the program             ##
## being built and configures auxilliary native code.                    ##
##                                                                       ##
## usage:  cd testcase_dir                                               ##
##         cp ../Makefile.skel Makefile                                  ##
##         edit Makefile:                                                ##
##            1. set MAIN_NAME to the name of your main java file        ##
##            2. enter the rest of your java files in SOURCE_DEPENDS     ##
##            3. If you have any extra native source, set AUX_NATIVE_SRC ##
##            4. Verify that tmplate_dir is correct                      ##
##         make                                                          ##
##                                                                       ##
###########################################################################


###########################################################################
## Change this to the name of the main file of your project           #####
###########################################################################

MAIN_NAME=src/ConnectionSetup.java # TODO change to the name of our main class.

###########################################################################
## Source dependencies required for correct rebuild                      ##
###########################################################################

SOURCE_DEPENDS=data/ImageMonitor.java data/In.java data/Out.java network/ImageCapture.java network/Image.java network/ServerProtocol.java

###########################################################################
## Any extra native source files required for the project                ##
###########################################################################

AUX_NATIVE_SRC=


###########################################################################
## The path to {Makefile.am,configure.in}.skel  (should be OK)           ##
###########################################################################

ifdef J2C_HOME
tmplate_dir=$(J2C_HOME)/J2CTranslator/test
else
tmplate_dir=..
endif

############################################################################
## No changes should be needed below this line #############################
############################################################################

include $(tmplate_dir)/make.skel.rules
