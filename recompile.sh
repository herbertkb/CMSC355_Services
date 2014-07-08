#!/bin/bash

# ESB
javac EnterpriseServiceBus/*.java
jar vmcf MANIFEST.MF EnterpriseServiceBus.jar EnterpriseServiceBus/*.class 

