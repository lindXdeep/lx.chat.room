.DEFAULT_GOAL := all

ifeq ($(MVN),)
    MVN  := mvn
endif

client-compile: 
	@ $(MVN) -pl client package

server-compile: 
	@ $(MVN) -pl server package

all: client-compile server-compile
