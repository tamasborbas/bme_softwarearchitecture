Ez alapján az oldal alapján key generálása (a $Tomcat elérési út valójában a Java_Home könyvtára):
http://www.mkyong.com/tomcat/how-to-configure-tomcat-to-support-ssl-or-https/

server.xml-be ki kell kommentezni a https-es sablont és az utolsó két sort hozzá kell írni (megfelelõ elérési utat és jelszót behelyettesítve):
<Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
               maxThreads="150" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS" 
               keystoreFile="d:\keystore"
               keystorePass="password"/>