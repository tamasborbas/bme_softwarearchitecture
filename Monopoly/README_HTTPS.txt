Ez alapj�n az oldal alapj�n key gener�l�sa (a $Tomcat el�r�si �t val�j�ban a Java_Home k�nyvt�ra):
http://www.mkyong.com/tomcat/how-to-configure-tomcat-to-support-ssl-or-https/

server.xml-be ki kell kommentezni a https-es sablont �s az utols� k�t sort hozz� kell �rni (megfelel� el�r�si utat �s jelsz�t behelyettes�tve):
<Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
               maxThreads="150" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS" 
               keystoreFile="d:\keystore"
               keystorePass="password"/>