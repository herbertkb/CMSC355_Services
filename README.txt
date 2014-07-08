README - Services

Version:	0.4
Author:		Keith Herbert
Date:		7 July 2014

A service oriented approach to providing dictionary lookup or other word
processing services to any number of clients.

Clients connect to an Enterprise Service Bus. Within the bus, a Service
Manager component accepts the connection from the client and dispatches a new
Session Manager thread to handle that clients needs. The Session Manager calls
an external Service Broker component to find the service requested by the
client, connects to that service and passes information between the client and
that service. 


Usage

EnterpiseServiceBus.jar, ServiceBroker.jar, and services.txt  must be in the same directory. 

First, start the Enterprise Service Bus with a port to listen for incoming
clients.
$ java -jar EnterpriseServiceBus.jar 8000
Tue Jul 08 16:15:37 EDT 2014 ServiceManager listening on port 8000.


The services provided by the Enterprise Service Bus should also be started
before any clients connect. They may be started from different directories,
different computers, or even different networks, so long as their hostname and
port match their service code in services.txt. 

For out example, we will use the following services.txt file:

FRENCH_DICT	localhost	8003
RUSSIAN_DICT	localhost	8001


$java -jar Dictionary.jar russian_words.txt 8001
Tue Jul 08 16:21:55 EDT 2014 Processing russian_words.txt.
Tue Jul 08 16:21:55 EDT 2014 russian_words.txt processed.
Tue Jul 08 16:21:55 EDT 2014 Dictionary server listening on port 8001.

$ java -jar Dictionary.jar french_words.txt
8003
Tue Jul 08 16:22:46 EDT 2014 Processing french_words.txt.
Tue Jul 08 16:22:46 EDT 2014 french_words.txt processed.
Tue Jul 08 16:22:46 EDT 2014 Dictionary server listening on port 8003.

The system is now ready for clients to connect.
Our client program is TranslateClient. It must be started with the service
code of the service requested along with the IP address and port of the Enterprise Service Bus.

Here, we will start three clients: two for the Russian dictionary service and
one for the French.

$ java -jar TranslateClient.jar RUSSIAN_DICT 127.0.0.1 8000
$ java -jar TranslateClient.jar RUSSIAN_DICT 127.0.0.1 8000
$ java -jar TranslateClient.jar FRENCH_DICT 127.0.0.1 8000

We can see this in the logs for the Enterprise Service Bus and the two
Dictionary servers.

EnterpriseServiceBus:
Tue Jul 08 16:28:54 EDT 2014 Client 1 (localhost) connected.
Tue Jul 08 16:28:54 EDT 2014 Client1 requests RUSSIAN_DICT
Tue Jul 08 16:28:54 EDT 2014 RUSSIAN_DICT at localhost port 8001
Tue Jul 08 16:46:22 EDT 2014 Client 2 (localhost) connected.
Tue Jul 08 16:46:22 EDT 2014 Client2 requests RUSSIAN_DICT
Tue Jul 08 16:46:22 EDT 2014 RUSSIAN_DICT at localhost port 8001
Tue Jul 08 16:47:04 EDT 2014 Client 3  (localhost) connected.
Tue Jul 08 16:47:04 EDT 2014 Client3 requests FRENCH_DICT
Tue Jul 08 16:47:04 EDT 2014 FRENCH_DICT at localhost port 8003


Russian Dictionary:
Tue Jul 08 16:28:54 EDT 2014 Client1 /127.0.0.1 connected.
Tue Jul 08 16:46:22 EDT 2014 Client2 /127.0.0.1 connected.

French Dictionary:
Tue Jul 08 16:47:04 EDT 2014 Client1 /127.0.0.1 connected.


The TranslateClient presents a prompt for a word, forwards the word to
the server and returns output from the server.

Enter a word or !!! to quit: boy
мальчик
Enter a word or !!! to quit: girl
девушка
Enter a word or !!! to quit: fnord
No translation for FNORD
Enter a word or !!! to quit: 

We can see the interaction of several clients on the ESB and dictionary
servers.

Enterpise Service Bus
Tue Jul 08 16:57:19 EDT 2014Client1 input: boy
Tue Jul 08 16:57:19 EDT 2014RUSSIAN_DICT output: мальчик
Tue Jul 08 16:57:19 EDT 2014 Client1 requests RUSSIAN_DICT
Tue Jul 08 16:57:39 EDT 2014Client1 input: girl
Tue Jul 08 16:57:39 EDT 2014RUSSIAN_DICT output: девушка
Tue Jul 08 16:57:39 EDT 2014 Client1 requests RUSSIAN_DICT
Tue Jul 08 16:57:47 EDT 2014Client1 input: fnord
Tue Jul 08 16:57:47 EDT 2014RUSSIAN_DICT output: No translation for FNORD
Tue Jul 08 16:57:47 EDT 2014 Client1 requests RUSSIAN_DICT
Tue Jul 08 17:05:18 EDT 2014Client2 input: arm
Tue Jul 08 17:05:18 EDT 2014RUSSIAN_DICT output: рука
Tue Jul 08 17:05:18 EDT 2014 Client2 requests RUSSIAN_DICT
Tue Jul 08 17:05:21 EDT 2014Client2 input: book
Tue Jul 08 17:05:21 EDT 2014RUSSIAN_DICT output: книга
Tue Jul 08 17:05:21 EDT 2014 Client2 requests RUSSIAN_DICT
Tue Jul 08 17:05:24 EDT 2014Client2 input: backpack
Tue Jul 08 17:05:24 EDT 2014RUSSIAN_DICT output: рюкзак
Tue Jul 08 17:05:24 EDT 2014 Client2 requests RUSSIAN_DICT
Tue Jul 08 17:05:39 EDT 2014Client4 input: boy
Tue Jul 08 17:05:39 EDT 2014FRENCH_DICT output: garçon
Tue Jul 08 17:05:39 EDT 2014 Client4 requests FRENCH_DICT
Tue Jul 08 17:05:41 EDT 2014Client4 input: girl
Tue Jul 08 17:05:41 EDT 2014FRENCH_DICT output: fille
Tue Jul 08 17:05:41 EDT 2014 Client4 requests FRENCH_DICT

Russian Server:
Tue Jul 08 16:57:19 EDT 2014 Client 1 query: boy
Tue Jul 08 16:57:19 EDT 2014 Client 1 translation: мальчик
Tue Jul 08 16:57:39 EDT 2014 Client 1 query: girl
Tue Jul 08 16:57:39 EDT 2014 Client 1 translation: девушка
Tue Jul 08 16:57:47 EDT 2014 Client 1 query: fnord
Tue Jul 08 16:57:47 EDT 2014 Client 1: No translation for FNORD
Tue Jul 08 17:05:18 EDT 2014 Client 2 query: arm
Tue Jul 08 17:05:18 EDT 2014 Client 2 translation: рука
Tue Jul 08 17:05:21 EDT 2014 Client 2 query: book
Tue Jul 08 17:05:21 EDT 2014 Client 2 translation: книга
Tue Jul 08 17:05:24 EDT 2014 Client 2 query: backpack
Tue Jul 08 17:05:24 EDT 2014 Client 2 translation: рюкзак

French Server
Tue Jul 08 17:05:39 EDT 2014 Client 1 query: boy
Tue Jul 08 17:05:39 EDT 2014 Client 1 translation: garçon
Tue Jul 08 17:05:41 EDT 2014 Client 1 query: girl
Tue Jul 08 17:05:41 EDT 2014 Client 1 translation: fille


When we are finished, we enter the escape sequence to close the clients. This
shows up as a null transaction for the dictionary servers. The Enterprise
Service Bus shows that the client has disconnected. 

Tue Jul 08 17:09:08 EDT 2014Client1 input: null
Tue Jul 08 17:09:08 EDT 2014RUSSIAN_DICT output: No translation for NULL
Tue Jul 08 17:09:08 EDT 2014 Client1 requests null
Tue Jul 08 17:09:08 EDT 2014 Client1 disconnected.
Tue Jul 08 17:09:18 EDT 2014Client2 input: null
Tue Jul 08 17:09:18 EDT 2014RUSSIAN_DICT output: No translation for NULL
Tue Jul 08 17:09:18 EDT 2014 Client2 requests null
Tue Jul 08 17:09:18 EDT 2014 Client2 disconnected.
Tue Jul 08 17:09:24 EDT 2014Client4 input: null
Tue Jul 08 17:09:24 EDT 2014FRENCH_DICT output: No translation for NULL
Tue Jul 08 17:09:24 EDT 2014 Client4 requests null
Tue Jul 08 17:09:24 EDT 2014 Client4 disconnected.
 





