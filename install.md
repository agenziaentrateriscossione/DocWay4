## Installazione Docway4 su piattaforma Linux
Prerequisiti software Server

##### Distribuzioni Linux Supportate

Docway4 è stato testato ed è utilizzato con diverse distribuzioni:

    Redhat Enterprise (dalla versione 7)
    Ubuntu Server Edition 14.04
    Centos(dalla versione 7)

Sono consigliati ad ora dal servizio assistenza 3di:

    Centos 7 64bit
    Ubuntu Server 14.04 LTS 64bit

Librerie di sistema richieste

Le librerie di sistema necessarie al corretto funzionamento dei componenti che compongono ExtraWay. Solitamente collocate in /lib e /usr/lib:

libdl.so.2
libz.so.1
libstdc++.so.6
libm.so.6
libgcc_s.so.1
libpthread.so.0
libc.so.6 (minimo glibc 2.5)
libxml2.so.2
libxslt.so.1
libzip.so.1

**ATTENZIONE**: **Gli eseguibili del motore eXtraWay non hanno ancora una versione dispobibile a 64bit**. E' necessario pertanto installare le librerie di compatibilita ia32 sulle macchine a 64 bit della maggior parte delle distribuzioni. Inoltre esistono alcuni casi riportati 1) sul quale è necessario installare manualmente le librerie elencate in versione 32bit. Se non è possibile effettuare questi passaggi o le librerie indicate non sono presenti, la specifica distribuzione non è supportata da Docway 3.10.2.

**Per installare le librerie su sistemi Ubuntu usare il seguente comando:
**
```bash
sudo apt-get install libgcc1:i386 libzip2:i386 libc6:i386 libxml2:i386 libxslt1.1:i386 libcurl3:i386 libncurses5:i386 libreadline6:i386 libstdc++6:i386
```

Per installare le librerie su sistemi RedHat/CentOS usare il seguente comando:

```bash
yum install libgcc.i686 libzip.i686 glibc.i686 libxml2.i686 libxslt.i686 libcurl.i686 ncurses-libs.i686 readline.i686 libstdc++.i686
```

##### Pdftotext
Per l'indicizzazione degli allegati in formato pdf è necessario installare l'utilità pdftotext.

In molte distribuzioni non è compresa nell'installazione di base: in alcune distribuzioni è presente all'interno del pacchetto xpdf (centos 4, redhat enterprise) o nel pacchetto poppler-utils (gentoo, ubuntu, debian, centos 5).
##### Imagemagick
Per l'indicizzazione e la conversione degli allegati in formato grafico è necessario installare l'utilità imagemagick.

In alcune distribuzioni non è compresa nell'installazione di base: tuttavia il pacchetto omonimo solitamente è presente tra quelli installabili.
CLASSPATH per Libreoffice > 5.1

Nel caso in cui si installi la versione di Libreoffice più recente e quindi da 5.1 in poi, bisognerà modificare il CLASSPATH all'interno del file extraway-fcs.conf in questa maniera:

    CLASSPATH=/usr/lib/libreoffice/program:$java_classes_home/../classes:$openoffice_ure/share/java/'*':$openoffice_basis/program/classes/'*':$java_classes_home/'*'
    
##### Client
Macchina client con collegamento di rete diretto al server, si sconsiglia l'utilizzo di indirizzi mappati con tecnologia NAT.
##### Browser supportati
L'elenco completo è disponibile alla seguente pagina : [Compatibilità con i browser](http://wiki.3di.it/doku.php?id=documentazione_3di:docway4:browser_compat&#compatibilita_browser_per_il_plugin_iwx "Compatibilità con i browser")
### Installazione e configurazione
#### Preparazione dell'installazione
##### Componenti che verranno installati

    Apache Tomcat 7
    Sun Java Runtime Environment 1.7.0
    LibreOffice 4/5
    Docway4

##### Preparazione della macchina server

E' consigliato mantenere l'installazione dell'applicativo in un dispositivo di memorizzazione separato rispetto a quello che ospita il sistema operativo.
Il pacchetto di installazione fornito è già configurato per essere installato sotto il direttorio /opt
Montare il dispositivo di memorizzazione scelto sotto /opt e inserire un riga apposita in /etc/fstab

Creare un utente con nome “extraway”. Questo sarà l'utente con cui verranno eseguiti tutti i processi relativi ad Docway4

    adduser extraway
    
##### Copia dei files

Copiare il pacchetto di installazione di Docway4 nella cartella /opt. Nel caso non sia possibile utilizzare il sistema di pacchetti integrato per l'installazione di libreoffice. E' possibile dal sito libreoffice ottenere l'elenco dei repository personalizzati oppure scaricare il pacchetto generico. E' possibile anche scaricare una versione generica dal nostro sito ftp.

**NB: per alcune distribuzioni (per es. CentOS) è necessario installare anche il pacchetto libreoffice-headless per poter utilizzare libreoffice anche in assenza di un'istanza del server X**.

**NB: Creare un link simbolico di 3di.it in it-3di
**
##### Abilitare permessi di scrittura sul tomcat-users.xml

Di base il file tomcat-users.xml è aperto in sola lettura, per abilitare il permesso di scrittura è necessario inserire il parametro “readonly=false” nel server.xml di Tomcat
```
  <GlobalNamingResources>
  <!-- Editable user database that can also be used by
       UserDatabaseRealm to authenticate users
  -->
  <Resource name="UserDatabase" auth="Container"
            type="org.apache.catalina.UserDatabase"
            description="User database that can be updated and saved"
            factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
            pathname="conf/tomcat-users.xml" readonly="false" />
</GlobalNamingResources>
```
Inserire il parametro “readonly=false” all'interno dell'attributo “Realm” (UserDatabase)“ nel file /opt/apache-tomcat-7…/conf/server.xml
##### Eccezioni su file jar

Se si utilizzano i 3diws modificare come segue la riga nel catalina.properties

    org.apache.catalina.startup.ContextConfig.jarsToSkip=bc*.jar,cryptix*.jar
    
##### Installazione dei pacchetti
#### Docway

Estrarre il pacchetto docway nella cartella /opt.

    /opt# tar xvjf docway_4.6.0*.tar.bz2
    
Per garantire il funzionamento dell'applicativo e poter effettuare operazioni di assistenza diretta da parte dei tecnici 3DI senza necessità di utilizzare credenziali amministrative (root) è necessario assegnare la cartella /opt all'utente extraway.

    /opt# chown -R extraway:extraway /opt
    
In alternativa è possibile assegnare ad extraway le singole cartelle in opt, tuttavia ciò potrebbe richiedere accesso amministrativo per effettuare aggiornamenti futuri.
LibreOffice

Nel caso si sia scelta la procedura di installazione manuale estrarre il pacchetto LibreOffice. Ad esempio:

    /opt# tar xzf LibO_3.3.1_Linux_x86_install-rpm_en-US.tar.gz
    
Eseguire il comando di installazione di LibreOffice (install.sh) dalla cartella estratta e seguire le indicazioni. Solitamente non è necessario cambiare nessun parametro dall'installazione di default.

Nel caso invece si pensi di utilizzare la versione della propria distribuzione è necessario controllare che i percorsi della cartella base di libreoffice e dei componenti basis e URE siano corretti nel file /opt/it-3di/platform/fcs/conf/extraway-fcs.conf

##### Accorgimenti per sistemi con versione di glibc antecedente alla 2.7

In alcuni sistemi è necessario installare la versione compatibile degli eseguibili eXtraWay. Questa versione è compilata con glibc 2.5.

Per installarli copiare i file contenuti in /opt/it-3di/extraway/xw/platform-dependent/bin-Linux/i586 in /opt/it-3di/extraway/xw/bin
##### Registrazione del motore del database (Extraway)

Per poter utilizzare Docway4 è necessario ottenere le licenze dal nostro settore commerciale.
Una volta ottenute le licenze eseguire il seguente comando dalla cartella /opt:

/opt~$ ./demo.sh registration

Dopo aver inserito il numero di licenze attivate, apparirà un numero.
Es. (per 10 licenze)

Entrato
Inizializzato
Analisi Parametri
Impostare il numero di postazioni da Abilitare
(-1=annulla, Invio=100):10
Inserire la chiave di abilitazione per 10-76469290

Comunicare all'assistenza 3di il numero ottenuto e inserire la chiave di abilitazione e il numero seriale che verranno forniti in cambio.
Successivamente inserire anche il nome e la società relativo al contratto di licenza.
Automatismi

Per utima cosa bisogna procedere a configurare il sistema operativo per interagire con i componenti in modo automatico.
##### Script di avvio
Gli script per caricare automaticamente l'applicativo all'avvio della macchina (e per riavviarlo l'applicativo in caso di necessità) si trovano sotto il percorso /opt/3di.it/extra/init-files e sono i seguenti:

    tomcat7
    extraway (motore database)
    extraway-fca (File Conversion Agent)
    extraway-fcs (File Conversion Service)
    extraway-msa (Mail Storage Agent)

Tutti accettano i comandi start, stop e restart.

NOTA: gli script all'interno di questa sezione sono tutti configurabili nel caso ci sia necessità di cambiare i percorsi di installazione.
Installazione automatica con script 3di

Sono disponibili già per alcune distribuzioni degli script per inserirli automaticamente all'interno del sistema rc.d:

    Debian, Ubuntu (utilizza update-rc.d)
    Redhat, Fedora e Centos (da copiare in /etc/init.d e attivare con chkconfig)
    Gentoo (da copiare in /etc/init.d e attivare con rc-update)
    script generico (crea direttamente i link in rc.d)


ATTENZIONE: l'esecuzione di alcuni di questi script richiede diritti amministrativi
Installazione Manuale

E' possibile anche installare questi file manualmente. E' necessario creare dei link ai file in /opt/3di.it/extra/init-files/ in /etc/init.d e creare successivamente creare da questi dei link nei diversi runlevel:
Es.

~# ln -s /opt/it-3di/extra/init-files/extraway /etc/init.d/
~# ln -s /etc/init.d/extraway /etc/rc0.d/K20extraway
~# ln -s /etc/init.d/extraway /etc/rc1.d/K20extraway
~# ln -s /etc/init.d/extraway /etc/rc6.d/K20extraway
~# ln -s /etc/init.d/extraway /etc/rc2.d/S79extraway
~# ln -s /etc/init.d/extraway /etc/rc3.d/K79extraway
~# ln -s /etc/init.d/extraway /etc/rc4.d/K79extraway

Si consiglia di spegnere tomcat leggermente prima, di seguito l'ordine di partenza e di spegnimento di tutti i servizi:

    tomcat7: start 80 | stop 19
    extraway: start 79 | stop 20
    extraway-fca: start 79 | stop 20
    extraway-fcs: start 79 | stop 20
    extraway-msa: start 79 | stop 20

Registro di Protocollo

L'esportazione del RIP può essere attivata aggiungendo al crontab dell'utente extraway il comando /opt/3di.it/docway3/rip/bin/rip
Es.

~$ crontab -e
35 22 * * * /opt/it-3di/docway3/rip/bin/rip </dev/null

##### Backup

E' altamente consigliato mantenere un backup dell'intera cartella it-3di (anche incrementale volendo).
Per fare ciò è necessario fermare i servizi extraway e tomcat6 prima di eseguire lo script di backup vero e proprio.

Es. script backup da inserire nel crontab di root:

/etc/init.d/tomcat7 stop
/etc/init.d/extraway-msa stop
/etc/init.d/extraway-fcs stop
/etc/init.d/extraway-fca stop
/etc/init.d/extraway stop
<comando backup>
/etc/init.d/extraway start
/etc/init.d/extraway-fca start
/etc/init.d/extraway-fcs start
/etc/init.d/extraway-msa start
/etc/init.d/tomcat7 start

##### Controlli finali

Dopo aver verificato che le applicazioni si avviino correttamente controllare in prima battuta i log di:

    Tomcat
    DocWay4
    DocWay4-service
    3DWS (Se presenti)
    ExtraWayWorkFlowOS (Se presenti)

Se non sono presenti errori tentare un login su DocWay e verificare la presenza di eventuali errori nei log:

    DocWay4
    DocWay4-service

E' buona norma comunicare al referente del progetto l'avvenuta installazione o aggiornamento degli applicativi.
Patch installate

Per Docway4 vengono rilasciate della patch e seguono una numerazione a se stante. Per individuare la versione di patch installata leggere il file Patch_level.txt presente nella root dell'applicativo.
