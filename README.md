##name

rpfl is not an abbreviation and stands for nothing in particular, although proposals are welcome. it is just how people in some parts of upper franconia, germany pronounce the word 'erdapfel' ( earth-apple -> potato ).

##rational

all major package systems have a vendor signature verification implemented, to prevent malware installation. however, these systems still asks you to trust a single party, that is the signer of the package, and accept a binary blob to be installed. That means, once a malignant third party gets hold of the signers private key, it can install arbitrary code on your systemthrough a man in the middle attack. rpfl circumvents these problems by 'distributed verification'. typically, rpfl will download verification data from multiple sources, to avoid reliance on a single "trusted third party". rpfl is a generic package verification api, that may be integrated in all kinds of package management systems like maven, yum, apt, you name it. It consists of a server part that offers an endpoint that will take a list of repository urls and return verification data for these packages. 

##terms

###repository url

a repository url may be something like 

ftp://ftp.pbone.net/mirror/download.fedora.redhat.com/pub/fedora/linux/development/rawhide/armhfp/os/Packages/l/l10n-kickstarts-0.23.1-1.fc23.noarch.rpm

that is, an url pointing to a package ('l10n-kickstarts-0.23.1-1.fc23.noarch.rpm') that is hosted on a repository ('ftp.pbone.net').

###verification data

verification data is what the rpfl-server returns for every repository url given, it consists of:

- a 384-bit SHA3 hash of the urls content
- the size of the package
- the verification-strength (did the server just download the package, or did it even recompile it?)

##configuration

the following properties in /src/resource/rpfl.properties may be edited

property | meaning | example
------------ | ------------- | -------------
localBuildDirectory | (Optional) absolute path to a local directory for builds | /home/buildmaster/debian/
repositoryUrl | the repository url|ftp://ftp.pbone.net/mirror
allowedFileExtensions | comma separated list of allowed file extensions | rpm, deb
transport | the transport to be used, possible values are 'xml' and 'protosnap'

##technology

###transport

the rpfl server can communicate via xml for readability during development, or a binary protocol named protosnap, which contains an eddsa 
signature and is typically about twice as compact as xml. Xml does not contain signatures and should not be used in production environments,
it is merely for testing.

#### xml
![Localhost Test](/postman.png)

#### protosnap

protosnap is protocol buffers as defined in [the Protocol Buffer definition](/src/resources/Messages.proto) compressed with snappy-framed. 
The first 64 bytes of every response is an eddsa signature of the following payload.

##state

The rpfl-server is somewhat feature complete but needs more tests (src/main/java).
The rpfld linux client daemon exists as a blueprint, you may look at the code
in [the cpp folder](src/main/cpp), but it does not compile and is more of a collection of ideas.
No plugins are written yet, please contact me if you are interested.
