# CATALOG of codename 'monkey'
This catalog tells about what is ideal workspace initialization.

Note that 'workspace' (development environment) includes following items:

* OS configuration
* Software and Middle-ware installation
* Software and Middle-ware configuration
* Workspace of IDE
* Data in RDB and others

## 0. Motivation
Workspace initialization is one of the key point to make lazy developer happy.

How you feel if you need 1 or more days to set up development environment?
How you feel if you cannot believe that your PC is not ready to develop?
It should be troublesome. It affects your motivation and productivity.

And workspace initialisation is also important to improve quality of product.
If each developers uses different workspace, some of them cannot provide correct setting so our product cannot be stable.
But sometimes newbie cannot find good documents, ignores warning from IDE, and cannot get help from senior.
They will change setting in incorrect way.

This problem is quite common for developers, you can see some products to solve at WWW.
For instance, GitHub created [BOXEN](http://boxen.github.com/) to make their work comfortable. Pivotal Labs also created [sprout](https://github.com/pivotal-sprout/sprout).
Tools for automated configuration ([Puppet](http://puppetlabs.com/puppet/what-is-puppet), [Chef](http://www.opscode.com/chef/) and [Ansible](http://www.ansibleworks.com/) etc.) are also good and famous solution.

But currently enterprise Java community has no well-integrated tool.
[Play! frameworks](http://www.playframework.com/) provides integrated command-line tool based on sbt, and Seasar2 also provides
an [IDE plugin](http://dolteng.sandbox.seasar.org/), but their solutions depend on framework. They cannot be perfect solution for us because we are using Spring, J2EE or something like that -- not only Play! and Seasar2.

So, we need new solution to solve our problem to help lazy developer.

## 1. Concept
We have 2 motivation to develop monkey, it provides ideal development environment.

### unify development environment
* for project manager,
    * It is important to __provide totally same development environment to each developer__.
    * So version of tools and libraries should be unified and fixed.
    * But it is also important to __keep each library and tool updated__, because some tools might be updated to fix security issue.
    * And manager doesn't want to say 'update your tools' and 'check your version of tool everyday'. It is troublesome and cannot be perfect solution.
    * So, we need solution to __unify version of tools, find security update for tools, and distribute updates automatically to each development environment__.
    * We also need to find __who changes version of tools incorrect way__. Imagine that some developer uses SVN 1.7 but your SVN repository is still 1.6... I guess that you want to alert them.
* for newbie,
    * Newbie might __try incorrect way to remove error and warning__, because they use Google instead of asking their senior.
    * This type of trouble is little difficult to find, and it makes quality bad.
    * By this system, newbie can set up their workspace like his/her senior.

### set up development environment faster
* for newbie,
    * We should __provide automated method to make their start fast__.
        * Newbie should cost too much time to set up, and it is unnecessary to provide value to customer.
        * [What is the on-boarding process for new engineers at Quora?](http://www.quora.com/Quora-company/What-is-the-on-boarding-process-for-new-engineers-at-Quora), [Japanese version](http://wazanova.jp/post/66072822397/quora)
    * And some newbie has no experience as coder or programmer, it also can be the reason of their low performance.
    * So, it is better to __reduce complex &amp; troublesome routine-work from their process to start business__.
* for developer,
    * Development initialization should be __easy and fast__.
        * Because developer must do it when they get new environment.
            * Replacing PC, Making new environment on VM, Starting work on new branch etc.

### easier setting up development environment
* for newbie,
    * We should provide easier way to install and configure different middleware/softwares needed.
        * Newbie may not know about the softwares used in a project and configuring them will be hard and confusing.
        * We provide simple step by step method (GUI or command line)

## 2. Needs
* install software if product needs
    * software: JDK, ruby, node, npm, Git, Ant, Maven, Vagrant, IDE etc.
    * middleware: HTTP server, database, servlet container, VirtualBox etc.
* setup software
    * create user and scheme for database
    * adding servers and database connection in software
    * create profile of WAS
        * shared library
        * JDBC data source
        * JVM argument (`-Duser.timezone=Asia/Tokyo -Duser.country=JP -Duser.language=ja` etc.)
        * JVM heap size
    * create configuration of HTTP server
    * import data to database
    * set up `~/.m2/settings.xml`
    * set up `~/.ssh/config`
    * add keystore for JDK to make maven download from repository
* install dependencies non-globally by npm (`npm install`)
* download sources of dependency ([`mvn dependency:sources`](http://maven.apache.org/plugins/maven-dependency-plugin/sources-mojo.html))
* support multiple OS (Win7, Ubuntu 14.04 LTS etc.)
* install middle ware (Jetty, Tomcat, WAS, Glassfish, Oracle Database, IBM DB2 etc.)
    * in some case, we need to download package or license file from intranet
* setup IDE configuration
    * install plugin
    * heap size of JVM (-Xmx, -Xms and others)
    * ease debugging (JMX configuration etc.)
    * user name (to ease writing JavaDoc)
    * install [lombok](http://projectlombok.org/)
* setup IDE workspace
    * checkout sources from remote Git/SVN repository
    * the project checked out will depend on the team the developer is working with.
    * create projects (Maven project)
* setup IDE workspace preferences
    * vendor (Oracle, IBM) and version (1.6.xx or 1.7.xx) of JDK
    * rule to verify
    * rule of FindBugs, checkstyle and PMD
    * encode of IDE workspace (UTF-8)
    * size of indent, how to handle TAB (replace with spaces, or not)
    * servers to deploy
* distribute configuration
    * How to provide configuration? 
        * ex) `monkey --file https://raw.github.com/user/product/master/.monkey.json`
        * ex) `monkey` just uses a file at current directory (but how user gets it?)
        * ex) sharing file initially by some link on wiki but after that if there is any update in file it will be done using version control so user gets it when fetching from remote repository
* user doesn't need so complex preparation to use monkey
    * because our motivation is reducing developer's routine work
    * monkey CLI might ask user to know where is 
* help user to start development
    * create shortcuts at task bar, desktop and so on.
    * create bookmarks at task bar, desktop and major browsers.
    * create ssh config, git config and git hooks
    * list up TODO to start development
    * mount NAS to PC with is protected by password authentication
    * install plugin to browser
    * configure locale and language of browser
* checking if developer's current settings are correct according to the configuration file, if not then change as needed
* A simple step by step GUI for easier and faster set up (easier for customers to use)
* update of sources from repository during course of development
* update of configuration of middleware like WAS during course of development

### optional needs

Now we don't need, but it is better to care when we design.

* multiple IDE support
    * Eclipse, NetBeans and others
* multiple RDB support
    * IBM DB2, Oracle Database, Postgres, MySQL, MariaDB etc.
    * it is good to support modifying `tnsnames.ora` for SQLPlus
* multiple office support
    * It might be better to change location of NAS depending on user's location
    * Let us know each NAS has same file
* multiple role support
    * Designers&apos; workspace might be different with developers&apos;
* help developer to keep environment fresh
    * let product manager knows how uses incorrect workspace
* parallel execution
    * install costs much network I/O and storage I/O, it is better to use multiple core to enhance speed
    * each task have dependency, we need topological sort to decide order
* easy upgrade
    * it should be easy to upgrade monkey itself
* others
    * install and configure printer driver


## 3. Why we use 'monkey' as codename?
To know it, you need to learn [ˆê–éé](http://ja.wikipedia.org/wiki/%E4%B8%80%E5%A4%9C%E5%9F%8E) (castle which is built in one night), a kind of legend in Japan.
This legend says that [Hideyoshi TOYOTOMI](http://en.wikipedia.org/wiki/Toyotomi_Hideyoshi) made a castle very quickly. And he was called little monkey.