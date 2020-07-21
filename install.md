# Descrizione del progetto di Riuso

3D Informatica vanta oltre trent’anni di esperienza al servizio di Pubbliche Amministrazioni, locali e centrali, complesse e articolate, alle quali viene offerto un supporto totale rispetto a tutti i processi di progettazione, sviluppo, mantenimento e miglioramento di sistemi informativi.

Nel rispetto delle novità introdotte dal piano triennale per l’informatica nella Pubblica Amministrazione e delle linee guida su acquisizione e riuso di software per le Pubbliche Amministrazioni, e grazie al supporto di ADER (Agenzia Delle Entrate/Riscossione) l'azienda ha avviato come incaricata il progetto di riuso di alcuni moduli software, sviluppati per conto di ADER ed imperniati attorno al sistema di gestione documentale e protocollo informatico DocWay.

## Installazione delle dipendenze necessarie all'utilizzo della suite documentale DocWay4
___
### - Installazione su piattaforma Linux di ExtraWay Platform

Scaricare il pacchetto eXtraWay Platform per Linux 

>ftp://ftp.3di.it/extra/platform/eXtraWay-platform-latest-linux.tar.gz

#### Requisiti Hardware

Le specifiche della macchina server dipendono principalmente dal numero di utenti che utilizzerà l'applicativo e dal tipo di utilizzo.
In linea di massima le prestazioni di eXtraWay Platform dipendono dalla velocità dei dispositivi di memorizzazione, dalla velocità della rete e, per la gestione di allegati non testuali, dalla memoria RAM.

##### Requisiti Minimi

* Processore Intel Xeon o compatibile
* 4 GB di RAM
* Disco rigido dedicato con almeno 100 GB (per un archivio medio con allegati)

##### Consigliati

Per un utilizzo medio: circa 30 utenti collegati contemporaneamente, un milione documenti.

* Processore Intel Xeon multicore o compatibile;
* 4 GB di RAM;
* Dispositivo di storage ad alta affidabilità (Raid) o moduli esterni (Nas, Sas);
* Almeno 300 GB dedicati alla gestione dell'archivio;
* Scheda di rete Gigabit o superiore;
* Alimentazione tramite gruppo di continuità;

#### Requisiti software

##### Server

##### Distribuzioni Linux Supportate

eXtraway Platform è stato testato ed è utilizzato con diverse distribuzioni:

* [Redhat Enterprise](http://www.redhat.com/rhel/) (dalla versione 3)
* [Fedora](http://fedoraproject.org/)
* [Ubuntu Server Edition](http://www.ubuntu.com/products/WhatIsUbuntu/serveredition)
* [Debian](http://www.debian.org)
* [Centos](http://www.centos.org/)
* [Gentoo](http://www.gentoo.org)

Sono consigliati ad ora dal servizio assistenza 3di:
* Centos 7 64bit
* Dedian 10 64bit

###### Librerie di sistema richieste

Le librerie di sistema necessarie al corretto funzionamento dei componenti che compongono ExtraWay. Solitamente collocate in /lib e /usr/lib:

* libdl.so.2
* libz.so.1
* libstdc++.so.6
* libm.so.6
* libgcc_s.so.1
* libpthread.so.0
* libc.so.6 (minimo glibc 2.5)
* libxml2.so.2
* libxslt.so.1
* libzip.so.1 o libzip.so.2
* libcurl.so.4

>ATTENZIONE: Gli eseguibili del motore eXtraWay non hanno ancora una versione dispobibile a 64bit. È necessario pertanto installare le librerie di compatibilita ia32 sulle macchine a 64 bit della maggior parte delle distribuzioni. Inoltre esistono alcuni casi riportati ((Al momento RHEL 5.6)) sul quale è necessario installare manualmente le librerie elencate in versione 32bit. Se non è possibile effettuare questi passaggi o le librerie indicate non sono presenti, la specifica distribuzione non è supportata.

##### Per installare le librerie su sistemi Ubuntu usare il seguente comando:

    sudo apt-get install libgcc1:i386 libzip2:i386 libc6:i386 libxml2:i386 libxslt1.1:i386 libcurl3:i386 libncurses5:i386 libreadline6:i386 libstdc++6:i386

##### Per installare le librerie su sistemi Debian recenti (Debian 10) usare il seguente comando da root:

    <code bash>  
    dpkg --add-architecture i386


    apt-get update

    apt-get install libgcc1:i386 libzip4:i386 libc6:i386 libxml2:i386 libxslt1.1:i386 libcurl4:i386 libncurses5:i386 libreadline7:i386 libstdc++6:i386 libxslt1.1:i386 libzip4:i386
    </code>

##### Per installare le librerie su sistemi RedHat/CentOS usare il seguente comando:

    yum install libgcc.i686 libzip.i686 glibc.i686 libxml2.i686 libxslt.i686 libcurl.i686 ncurses-libs.i686 readline.i686 libstdc++.i686

##### Pdftotext

Per l'indicizzazione degli allegati in formato pdf è necessario installare l'utilità pdftotext.

In molte distribuzioni non è compresa nell'installazione di base: in alcune distribuzioni è presente all'interno del pacchetto *xpdf* (centos 4, redhat enterprise) o nel pacchetto *poppler-utils* (gentoo, ubuntu, debian, centos 5).

##### Imagemagick

Per l'indicizzazione e la conversione degli allegati in formato grafico è necessario installare l'utilità imagemagick.

In alcune distribuzioni non è compresa nell'installazione di base: tuttavia il pacchetto omonimo solitamente è presente tra quelli installabili.


##### Per installare le librerie su sistemi Debian usare il seguente comando:

    <code bash>apt-get install poppler-utils imagemagick</code>

### Installazione e configurazione

#### Preparazione dell'installazione

##### Componenti che verranno installati

* Apache Tomcat 7.x
* Sun Java Runtime Environment 1.8.x
* LibreOffice 5.x
* ExtraWay Platform

##### Preparazione della macchina server

È consigliato mantenere l'installazione dell'applicativo in un dispositivo di memorizzazione separato rispetto a quello che ospita il sistema operativo.

Il pacchetto di installazione fornito da 3DI è già configurato per essere installato sotto il direttorio /opt

Montare il dispositivo di memorizzazione scelto sotto /opt e inserire un riga apposita in /etc/fstab

__Creare un utente con nome "extraway". Questo sarà l'utente con cui verranno eseguiti tutti i processi relativi ad eXtraWay Platform__

    <code>
    useradd -m extraway
    </code>

##### Copia dei files

Copiare il pacchetto di installazione di Extraway nella cartella /opt.
Nel caso non sia possibile utilizzare il sistema di pacchetti integrato per l'installazione di libreoffice. È possibile dal [sito libreoffice](http://www.libreoffice.org) ottenere l'elenco dei repository personalizzati oppure scaricare il pacchetto generico. È possibile anche scaricare una versione generica (ftp://ftp.3di.it/extra/libreoffice/LibO_3.3.1_Linux_x86_install-rpm_en-US.tar.gz) dal nostro sito ftp.

##### Abilitare permessi di scrittura sul tomcat-users.xml

Di base il file tomcat-users.xml è aperto in sola lettura, per abilitare il permesso di scrittura è necessario inserire il parametro "readonly=false" nel server.xml di Tomcat:

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

* Inserire il parametro "readonly=false" all'interno dell'attributo "Realm" (UserDatabase)" nel file /opt/apache-tomcat-7.X/conf/server.xml

##### Impostazione parametri del kernel per eXtraWay in /etc/sysctl.conf

Dalla release 24, eXtraWay necessita di ulteriore memoria condivisa a disposizione, rifiutandosi di partire nel caso questa non sia a disposizione.

Per impostare correttamente il kernel, aggiungere al file /etc/sysctl.conf (se non esiste, crearlo) le seguenti righe:

    <code>  
    # Impostazioni per eXtraWay  
    #kernel.core_pattern=/opt/cores/core.%e.%p.%h.%t  
    kernel.shmmax=268435456  
    </code>

ed eseguire

    sysctl -p

per applicare la modifica al sistema.

#### Installazione dei pacchetti

##### ExtraWay Platform

Estrarre il pacchetto extraway nella cartella /opt.

Es.
    /opt# tar xvjf extraway_platform_*.tar.bz2

Per garantire il funzionamento della piattaforma senza che ci sia necessità di credenziali amministrative (root) è necessario assegnare la cartella /opt all'utente extraway.

Es.
    /opt# chown -R extraway:extraway /opt

In alternativa è possibile assegnare ad extraway le singole cartelle in opt, tuttavia ciò potrebbe richiedere accesso amministrativo per effettuare aggiornamenti futuri.

##### Accorgimenti per sistemi con versione di glibc antecedente alla 2.7

In alcuni sistemi è necessario installare la versione compatibile degli eseguibili eXtraWay. Questa versione è compilata con glibc 2.5.

Per installarli copiare i file contenuti in /opt/it-3di/extraway/xw/platform-dependent/bin-Linux/i586 in /opt/it-3di/extraway/xw/bin

##### Registrazione del motore del database (eXtraWay)

Per poter utilizzare appieno eXtraWay è necessario effettuare la registrazione.

### Automatismi

Per ultima cosa bisogna procedere a configurare il sistema operativo per interagire con i componenti in modo automatico.
All'interno della platform sono disponibili le routine systemd

>NOTA: gli script all'interno di questa sezione sono tutti configurabili nel caso ci sia necessità di cambiare i percorsi di installazione.
___
### Uso di Antivirus nelle installazioni eXtraWay

###### [[Torna su]](https://github.com/agenziaentrateriscossione/riuso#descrizione-del-progetto-di-riuso) - [[Torna a *Istruzioni per le dipendenze eXtraWay e DocWay*]](https://github.com/agenziaentrateriscossione/riuso#istruzioni-per-le-dipendenze-extraway-e-docway)

La presenza di un antivirus nelle installazioni della piattaforma eXtraWay può comportare due distinti ordini di problemi: di natura prestazionale e di natura funzionale.

#### Aspetto Prestazionale
I software antivirus più diffusi eseguono scansioni "Real-time" sul sistema operativo e sui processi. Può accadere quindi che scansioni e blocchi troppo invasivi rallentino o fermino del tutto i componenti Tomcat (applicativo) o eXtraWay (motore del database).

#### Aspetto Funzionale
È prassi comune che gli antivirus riconoscano come comportamento rischioso, potenzialmente maligno, uno dei comportamenti del server eXtraWay. Per ovviare a quest'inconveniente è necessario agire sulle impostazioni dell'antivirus.

#### Esclusioni
Per poter utilizzare comunque il software antivirus è necessario impostare una o più liste di esclusione: esistono principalmente 2 tipi di lista a seconda del software utilizzato. Esse si riferiscono ai files/cartelle delle quali non si richiede che venga compiuta verifica e l'elenco degli eseguibili che possono essere considerati affidabili.

##### Interventi di tipo prestazionale
È evidente come l'attività di un antivirus non possa essere priva di impatti sulle prestazioni dell'intero sistema. In particolare, gran parte degli antivirus operano in tempo reale ed intercettano, per così dire, tutte le operazioni di lettura e scrittura che hanno luogo sui dischi. Ciò comporta un naturale rallentamento di tali processi.

Per non incorrere in limitazioni prestazionali, ed in generale considerando la natura dei dati scritti da eXtraWay, gli antivirus possono (e dovrebbero) essere configurati per ignorare l'attività afferente alle cartelle che ospitano i dati, gli indici e le registrazioni di servizio((Quali, ad esempio, le cartelle dei logs etc. etc.))

###### Lista esclusioni per cartelle

È necessario impostare come esclusione la cartella del database, la cartella degli eseguibili e le cartelle dei file temporanei:

|Cartella|Contenuto|
|--|--|
|\3di.it\extraway\xw\db  |Cartella database eXtraWay  |
|\3di.it\extraway\xw\bin  |Cartella eseguibili eXtraWay  |
|\3di.it\extraway\xw\logs  |Cartella logs eXtraWay  |
|\3di.it\extraway\xw\xreg  |Dove presente, cartella di servizio per il registro di  eXtraWay  |
|\3di.it\extraway\xw\lazy  |Dove presente, cartella di servizio per le attività *near on line* di  eXtraWay  |
|\Programmi\Apache Software Foundation\Tomcat 7.0\bin  |Cartella eseguibili Apache Tomcat  |
|\Programmi\Apache Software Foundation\Tomcat 7.0\work  |Cartella cache Tomcat  |
|*%temp*%\hwtemp  |Cartella file temporanei di eXtraWay  |

La cartella *%temp%* solitamente corrisponde a \windows\temp ma dipende direttamente dall'impostazione della variabile di sistema Windows.

##### Interventi di tipo Funzionale
Come precedentemente indicato un comportamento del modulo eXtraWay Server viene considerato malevolo dalla maggioranza degli antivirus.

L'architettura di eXtraWay Server prevede che esista un'istanza del modulo in ascolto per nuove connessioni. Quando un'applicazione client richiede una nuova connessione il server in ascolto, detto *Master*, duplica se stesso producendo una copia figlia, detta *Slave*, che di fatto instaura la connessione con l'applicazione client e ne esegue le richieste.

 Il processo così generato eredita dal processo principale alcune risorse condivise.

Un simile comportamento viene considerato pericoloso in quanto rappresenta uno dei più comuni sistemi che hanno i virus per avviare processi in grado di produrre danni e/o appropriarsi di importanti informazioni presenti nel sistema.

Per ovviare a questo bisogna necessariamente istruire il sistema antivirus perché consideri eXtraWay Server come processo affidabile, quindi *trusted*, e non ne impedisca il corretto comportamento.

###### Lista esclusioni per eseguibili

È necessario impostare come esclusione l'eseguibile di eXtraWay e l'eseguibile di Tomcat

|Modulo|
|--|
|\3di.it\extraway\xw\bin\xw.exe|
|\Programmi\Apache Software Foundation\Tomcat 6.0\bin\tomcat6.exe|

I percorsi in entrambe le liste possono variare a seconda delle installazioni, tuttavia è possibile recuperare l'esatta posizione controllando nelle proprietà dei servizi di Windows.

##### Configurazioni antivirus già testate con eXtraWay

Nel corso del tempo sono state verificate diverse installazioni nelle quali è stato possibile compiere con successo gli interventi descritti nei capitoli precedenti.

Dal momento che ogni software antivirus ha propria configurazione e che essi evolvono naturalmente nel tempo, non viene descritto in questa sede il procedimento da seguire rimandando il dettaglio alla documentazione degli stessi.

#### Antivirus efficacemente verificati:

* Norton Antivirus
* NOD32 (Versione esistente al 2010)
* Kaspersky Anti-Virus

Va altresì detto che qualora un antivirus imponga limiti funzionali ma senza dare la possibilità di compilare liste di esclusione, esso risulta di fatto incompatibile con le installazioni eXtraWay.

Allo stato attuale risultano

#### Antivirus incompatibili:

* Panda Antivirus Titanum 2004

-------

>**N.B.:** Gli elenchi riportati sono da considerarsi __meramente indicativi e non esaustivi__. 3D Informatica __non può considerarsi responsabile__ qualora nuove versioni di antivirus verificati risultassero incompatibili ne può garantire che nuove versioni degli antivirus noti come incompatibili risultino di fatto utilizzabili. Chi fosse intenzionato ad acquisire un software antivirus per proteggere le proprie installazioni eXtraWay dovrà, __sotto la propria responsabilità__, raccogliere informazioni sufficienti per garantirsi la possibilità di sottoporre a tale software liste di esclusioni quanto meno per gli aspetti funzionali.
___
### - Installazione su piattaforma Linux di DocWay 4

###### Distribuzioni Linux Supportate

Docway4 è stato testato ed è utilizzato con diverse distribuzioni:

* [Redhat Enterprise](http://www.redhat.com/rhel/) (dalla versione 7)
* [Ubuntu Server Edition 14.04](http://www.ubuntu.com/products/WhatIsUbuntu/serveredition)
* [Centos](http://www.centos.org/) (dalla versione 7)

Sono consigliati ad ora dal servizio assistenza 3di:

* Centos 7 64bit
* Ubuntu Server 14.04 LTS 64bit

###### Librerie di sistema richieste

Le librerie di sistema necessarie al corretto funzionamento dei componenti che compongono ExtraWay. Solitamente collocate in /lib e /usr/lib:

* libdl.so.2
* libz.so.1
* libstdc++.so.6
* libm.so.6
* libgcc_s.so.1
* libpthread.so.0
* libc.so.6 (minimo glibc 2.5)
* libxml2.so.2
* libxslt.so.1
* libzip.so.1

>ATTENZIONE: Gli eseguibili del motore eXtraWay non hanno ancora una versione dispobibile a 64bit. È necessario pertanto installare le librerie di compatibilita ia32 sulle macchine a 64 bit della maggior parte delle distribuzioni. Inoltre esistono alcuni casi riportati ((Al momento RHEL 5.6)) sul quale è necessario installare manualmente le librerie elencate in versione 32bit. Se non è possibile effettuare questi passaggi o le librerie indicate non sono presenti, la specifica distribuzione non è supportata da Docway 3.10.2.

###### Per installare le librerie su sistemi Ubuntu usare il seguente comando:

    sudo apt-get install libgcc1:i386 libzip2:i386 libc6:i386 libxml2:i386 libxslt1.1:i386 libcurl3:i386 libncurses5:i386 libreadline6:i386 libstdc++6:i386

###### Per installare le librerie su sistemi RedHat/CentOS usare il seguente comando:

    yum install libgcc.i686 libzip.i686 glibc.i686 libxml2.i686 libxslt.i686 libcurl.i686 ncurses-libs.i686 readline.i686 libstdc++.i686

###### Pdftotext

Per l'indicizzazione degli allegati in formato pdf è necessario installare l'utilità pdftotext.

In molte distribuzioni non è compresa nell'installazione di base: in alcune distribuzioni è presente all'interno del pacchetto **xpdf** (centos 4, redhat enterprise) o nel pacchetto **poppler-utils** (gentoo, ubuntu, debian, centos 5).

###### Imagemagick

Per l'indicizzazione e la conversione degli allegati in formato grafico è necessario installare l'utilità imagemagick.

In alcune distribuzioni non è compresa nell'installazione di base: tuttavia il pacchetto omonimo solitamente è presente tra quelli installabili.

###### CLASSPATH per Libreoffice > 5.1

Nel caso in cui si installi la versione di Libreoffice più recente e quindi da 5.1 in poi, bisognerà modificare il CLASSPATH all'interno del file extraway-fcs.conf in questa maniera:

**CLASSPATH=/usr/lib/libreoffice/program:$java_classes_home/../classes:$openoffice_ure/share/java/'*':$openoffice_basis/program/classes/'*':$java_classes_home/'*'**

##### Client

Macchina client con collegamento di rete diretto al server, si sconsiglia l'utilizzo di indirizzi mappati con tecnologia NAT.

##### Browser supportati

L'elenco completo è disponibile [qui](https://wiki.3di.it/doku.php?id=documentazione_3di:docway4:browser_compat)

### Installazione e configurazione

#### Preparazione dell'installazione

##### Componenti che verranno installati

* Apache Tomcat 7
* Sun Java Runtime Environment 1.7.0
* LibreOffice 4/5
* Docway4

##### Preparazione della macchina server

È consigliato mantenere l'installazione dell'applicativo in un dispositivo di memorizzazione separato rispetto a quello che ospita il sistema operativo.

Il pacchetto di installazione fornito da 3DI è già configurato per essere installato sotto il direttorio /opt

Montare il dispositivo di memorizzazione scelto sotto /opt e inserire un riga apposita in /etc/fstab

__Creare un utente con nome "extraway". Questo sarà l'utente con cui verranno eseguiti tutti i processi relativi ad Docway4__

    <code bash>adduser extraway</code>

##### Copia dei files

Copiare il pacchetto di installazione di Docway4 nella cartella /opt.

Nel caso non sia possibile utilizzare il sistema di pacchetti integrato per l'installazione di libreoffice, è possibile dal [sito libreoffice](http://www.libreoffice.org) ottenere l'elenco dei repository personalizzati oppure scaricare il pacchetto generico. È possibile anche scaricare una versione generica (ftp://ftp.3di.it/extra/libreoffice/LibO_3.3.1_Linux_x86_install-rpm_en-US.tar.gz) dal nostro sito ftp.

NB: per alcune distribuzioni (per es. CentOS) è necessario installare anche il pacchetto libreoffice-headless per poter utilizzare libreoffice anche in assenza di un'istanza del server X.

NB: Creare un link simbolico di 3di.it in it-3di

###### Abilitare permessi di scrittura sul tomcat-users.xml

Di base il file tomcat-users.xml è aperto in sola lettura, per abilitare il permesso di scrittura è necessario inserire il parametro "readonly=false" nel server.xml di Tomcat

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

* Inserire il parametro "readonly=false" all'interno dell'attributo "Realm" (UserDatabase)" nel file /opt/apache-tomcat-7.../conf/server.xml

###### Eccezioni su file jar

Se si utilizzano i 3diws modificare come segue la riga nel catalina.properties

    <code>org.apache.catalina.startup.ContextConfig.jarsToSkip=bc*.jar,cryptix*.jar</code>

##### Installazione dei pacchetti

###### Docway

Estrarre il pacchetto docway nella cartella /opt.

Es.
    /opt# tar xvjf docway_3.8.12*.tar.bz2

Per garantire il funzionamento dell'applicativo e poter effettuare operazioni di assistenza diretta da parte dei tecnici 3DI senza necessità di utilizzare credenziali amministrative (root) è necessario assegnare la cartella /opt all'utente extraway.

Es.
    /opt# chown -R extraway:extraway /opt

**In alternativa è possibile assegnare ad extraway le singole cartelle in opt, tuttavia ciò potrebbe richiedere accesso amministrativo per effettuare aggiornamenti futuri.**

##### Accorgimenti per sistemi con versione di glibc antecedente alla 2.7

In alcuni sistemi è necessario installare la versione compatibile degli eseguibili eXtraWay. Questa versione è compilata con glibc 2.5.

Per installarli copiare i file contenuti in /opt/3di.it/extraway/xw/platform-dependent/bin-Linux/i586 in /opt/it-3di/extraway/xw/bin

##### Registrazione del motore del database (Extraway)

Per poter utilizzare appieno eXtraWay è necessario ottenere le licenze dal nostro settore commerciale.

###### Automatismi

Per ultima cosa bisogna procedere a configurare il sistema operativo per interagire con i componenti in modo automatico.
Nella platform sono presenti le routine systemd.

##### Script di avvio

Gli script per caricare automaticamente l'applicativo all'avvio della macchina (e per riavviare l'applicativo in caso di necessità) si trovano sotto il percorso /opt/3di.it/extra/init-files e sono i seguenti:

* tomcat7
* extraway (motore database)
* docway-fca (File Conversion Agent)
* docway-fcs (File Conversion Service)
* docway-msa (Mail Storage Agent)

Tutti accettano i comandi start, stop e restart.

>NOTA: gli script all'interno di questa sezione sono tutti configurabili nel caso ci sia necessità di cambiare i percorsi di installazione.

##### Controlli finali

Dopo aver verificato che le applicazioni si avviino correttamente controllare in prima battuta i log di:

* Tomcat
* DocWay4
* DocWay4-service
* 3DIWS (Se presenti)
* ExtraWayWorkFlowOS (Se presenti)

Se non sono presenti errori tentare un login su DocWay e verificare la presenza di eventuali errori nei log:

* DocWay4
* DocWay4-service

È buona norma comunicare al referente del progetto l'avvenuta installazione o aggiornamento degli applicativi.
___
###### [[Torna su]](https://github.com/agenziaentrateriscossione/riuso#descrizione-del-progetto-di-riuso) - [[Torna a *Istruzioni per le dipendenze eXtraWay e DocWay*]](https://github.com/agenziaentrateriscossione/riuso#istruzioni-per-le-dipendenze-extraway-e-docway)
___
## [FCA](https://github.com/agenziaentrateriscossione/docway-fca)/[FCS](https://github.com/agenziaentrateriscossione/docway-fcs)

###### [[Torna su]](https://github.com/agenziaentrateriscossione/riuso#descrizione-del-progetto-di-riuso)

FCA (File Conversion Agent) e FCS (File Conversion Service) consistono in due processi che permettono l'**estrazione del testo da files** e la **conversione di files in un differente formato** (es. da DOC a PDF).

Lo scenario di utilizzo può variare a seconda del carico di lavoro:
- In ambienti di ridotte dimensioni (carico di lavoro non elevato) entrambi i processi possono essere installati (e configurati) sullo stesso server;
- In ambienti con un elevato carico di lavoro (in termini di numero di richieste) è possibile scalare l'attività di estrazione testo e conversione su più server. In questo scenario verrà installato **una e una sola istanza di FCA** (che si occuperà di recuperari i lavori da portare a termine) e **N istanze di FCS** (su differenti server) che si occuperanno di elaborare i file e registrare il risultato dell'attività richiesta.

### Flusso di esecuzione

La logica secondo la quale viene svolta tale attività è descritta di seguito:
- FCA si occupa di recuperare tutti i lavori pendenti (richieste di estrazione e/o conversione) e di gestire la coda di lavori pendenti e la comunicazione con il pool di FCS. All'avvio della comunicazione con ogni istanza di FCS viene inviato un set di informazioni inerenti la configurazione dell'ambiente (timeout da applicare all'elaborazione, eventuali estensioni di file da ignorare, etc.);
- Ogni FCS riceve un lavoro da eseguire da parte di FCA, esegue l'attività richiesta e registra il risultato (caricamento dei file prodotti dalla conversione e/o del testo estratto);
- Al termine dell'elaborazione FCS comunica a FCA l'esito dell'attività in modo da poter ricevere il lavoro successivo.

### Descrizione dei progetti

I progetti di FCA e FCS (progetti _JAVA_) sono stati suddivisi in 2 librerie che corrispondo a:
- Logiche generiche utilizzatibili in differenti ambiti (progetti abstract);
- Implementazioni specifiche per uno scenario (nel nostro caso DocWay).

In base alla struttura appena descritta, è quindi possibile utilizzare le librerie abstract per poter gestire scenari differenti da quello DocWay (è sufficiente realizzare le implementazioni richieste dai progetti abstract). Per maggiori dettagli sull'attività si rimanda alla documentazione specifica dei progetti.

#### FCA

[**it.tredi.abstract-fca**](https://github.com/agenziaentrateriscossione/abstract-fca): Configurazione del POOL di FCS con gestione del recupero e assegnazione dei lavori ai differenti processi di FCS (su server distinti).

**it.tredi.docway-fca**: Implementazione per DocWay di FCA, ovvero recupero dei documenti di DocWay da processare (documenti contenenti allegati per i quali è richiesta la conversione e/o estrazione del testo).


#### FCS


[**it.tredi.abstract-fcs**](https://github.com/agenziaentrateriscossione/abstract-fcs): Elaborazione vera e propria dei files. Logiche di conversione e estrazione testo dai file (integrazione con le varie dipendenze software).

**it.tredi.docway-fcs**: Implementazione per DocWay di FCS (aggiornamento dell'esito dei lavori, registrazione dei file convertiti, indicizzazione del testo contenuto negli allegati del documento, etc.).


### Requisiti

Requisiti per l'esecuzione di conversioni e estrazione di testo da parte di FCS:
- OpenOffice
- ImageMagick
- Tesseract
___
## [Console Audit](https://github.com/agenziaentrateriscossione/auditConsole)

Web Application grazie alla quale è possibile consultare i dati di audit registrati per uno o più applicativi. L'interfaccia web realizzata permette (previa autenticazione e autorizzazione) diversi filtri di ricerca sui risultati registrati tramite AUDIT:
- Filtro su archivio (nome del database)
- Filtro su tipologia di operazione (modifica, login, etc.) - Non ci sono vincoli sulle tipologie di operazioni utilizzabili
- Filtro su utente (operatore esecutore dell'attività)
- Filtro su data e ora
- Filtro su tipologia di record
- Filtro su identificativo del record

Esempi classici di ricerche che possono essere svolte agevolmente tramite console sono le seguenti:
- Elenco di tutte le attività svolte su uno specifico record;
- Elenco di tutte le attività svolte da un utente in un determinato periodo di tempo;
- Elenco di tutte le attività di una specifica tipologia;
- etc.

I dati di AUDIT sul database mongodb vengono registrati attraverso un'apposita librieria realizzata per DocWay. È comunque possibile implementare una propria versione della registrazione del record di AUDIT (per un qualsiasi applicativo) e utilizzare la console di AUDIT per la consultazione dei dati.

Altri attività previste altre alla classica ricerca sono:
- Esportazione CSV dei dati di AUDIT;
- Validazione del record di AUDIT tramite specifico checksum (record di audit non alterato su archivio MongoDB).

### Prerequisiti

- JAVA
- Application Server (es. Tomcat)
- MongoDB

### Formato del Record

Di seguito è descritto il formato del record di AUDIT registrato su archivio MongoDB. Per utilzzare la console su una qualsiasi altra applicazione è sufficiente registrare sull'archivio MongoDB le attività degli utenti secondo il formato indicato.

```json
{
    "_id" : ObjectId("5b18e187f41b7e14cd037603"),
    "archivio" : "xdocwaydoc",
    "nrecord" : "00239115",
    "tipoRecord" : "doc",
    "user" : {
        "username" : "sstagni",
        "codUser" : "PI000008",
        "ipAddress" : "127.0.0.1"
    },
    "tipoAzione" : "MODIFICA_RECORD",
    "data" : ISODate("2018-06-07T07:40:55.728Z"),
    "changes" : [
        {
            "field" : "doc.extra.raccIndice.@stato",
            "before" : "lavorazione",
            "after" : "completato"
        }
    ]
}
```

| Campo | Descrizione |
|--|--|
| _id | Identificaivo del record su MongoDB (record di Audit) |
| archivio | Nome del database utilizzato dall'applicazione sottoposta a AUDIT (supporto ad applicazioni multi-database) |
| nrecord | Identificato del record dell'applicativo |
| tipoRecord | Identifica la tipologia di record al quale l'audit fa riferimento |
| user | Informazioni relative all'utente che ha svolto l'attività (username, identificativo, etc.) |
| tipoAzione | Tipologia di azione svolta (tipicamente dipendente dall'applicazione sottoposta a AUDIT) |
| data | Data e Ora di svolgimento dell'azione da parte dell'utente |
| changes | Elenco di modifiche apportate al record (per ogni campo viene indicato il valore precedente alla modifica e quello successivo) |
___
## [MSA](https://github.com/agenziaentrateriscossione/docway-msa)

###### [[Torna su]](https://github.com/agenziaentrateriscossione/riuso#descrizione-del-progetto-di-riuso)

MSA (Mail Storage Agent) è un servizio Java multi-processo che si occupa delle seguenti operazioni:
* archiviazione delle email PEC e non (le mail vengono trasformate e salvate in documenti in DocWay XML);
* scambio di documenti tra sistemi DocWay XML differenti mediante la posta elettronica certificata (interoperabilità);
* Completa gestione del processo di interfacciamento con lo SdI per le fatture elettroniche.
MSA lavora esaminando periodicamente delle caselle di posta (certificate o meno) e dispone di una sua specifica console di amministrazione e controllo.

Sebbene il presente modulo sia rilasciato nella modalità open source, è possibile estendere questo servizio unicamente nello scenario DocWay e eXtraWay.

Di seguito le funzionalità offerte:
- Architettura software, modulare, espandibile e facilmente personalizzabile tramite l'implementazione di apposite interfacce per:
    + personalizzare il comportamento di archiviazione di caselle di posta elettronica;
    + leggere le configurazioni delle caselle di posta (e eventualmente estenderle) su sistemi differenti da ACL;
    + archiviare le email su sistemi differenti da DocWay.
- Worker concorrenti in grado di effettuare l'archiviazione in parallelo di più caselle di posta abbattendo i tempi di archiviazione (in particolare nel caso di numerose caselle di posta elettronica da gestire).
- Produzione su MongoDB di rapporti di Audit per ogni sessione di archiviazione di ogni singola casella di posta elettronica. In caso di errore verrà memorizzato l'intero EML per agevolare le operazioni di monitoraggio, controllo errori e eventuale risoluzione di problemi.
- Console WEB di monitoraggio per individuare agevolmente le email che sono andate in errore e per effettuare nuovamente l'elaborazione.

### Prerequisiti:
1. _Java8_
2. MongoDB (vers. 3.6.3)
___
