# Slave Crawler : 

# How I developed Micro-service that is so light weight and runs on 800 MB RAM : 

1.	Avoiding objects – Specially Class Objects – Objects are heavy weight occupying 48 bytes.
2.	Use Global Instance Static Classes – that is common and can be shared across instances. 
3.	Byte[] and Str[] instead of Strings and Collections.
4.	Parallel arrays where – a[key] and a[value] takes less space and more memory efficient than Collections map and List.
5.	Map Collections used with final static – with fixed size .
Eg: Map<byte[],Integer> container = new HashMap<byte[],Integer>(1), defines Map of size 1.
6.	Creates Arrays of fixed size only on demand , array of byte takes less bytes than String.
7.	StringBuilder() instead of String has drastically reduced the memory space. 
8.	Use of short instead of int , volatile instead of final .
9.	Security – Classes are final as they cannot be extended or override or overloaded.
10.	AtomicBoolean , AtomicInteger – Helps maintain dedicated variable in  a multi threaded environment.
11.	Singleton Pattern for Thread safe class access. 
12.	Static classes and methods stored in Permanent Generation or metaspace as it reduces the GC Pauses.
13.	Use of <exclusions> </exclusions> in maven to prevent unused classes to be loaded in JVM Runtime . Please refer to AmazonAurora/pom.xml for the exclusions.
14.	-XX:MaxGCPauseMillis=80 (in milliseconds)  - This JVM parameter will make your JVM more responsive and highly available for crawling as it reduces the time taken for GC Pauses.
15.	Crash Proof – Fault Tolerance – Persisting and saving the serialized file to /home/ec2-user for reloading the fail incase the JVM abruptly shutsdown.

# What my Crawler Can do : 
1.	Language Detection – English , German , Japanese, … etc. upto 54 languages of home page.
2.	Email Address , Phone number and Address Extraction (city, Zipcode and State) . 
3.	Stop words removal, Text Normalization and outgoing links extraction. 
4.	Outgoing Links – Programmed for 8 links outgoing inner links from the home page to the neighbouring pages (1 hops). 
5.	Keyword Analysis of the Body text of a URL HTML page – Key => Words , Value => Counts (frequency of words in a html document). 
6.	Duplicate Finder – returns true of it finds duplicates of text across the 9 URLS of a ROOT webpage. MD5 Hash Algorithm is used for cross checking the duplicates. 
7.	Link Discover – Automatically Discover Links that has contact us or about us page and extracts the phone number and email address.
8.	User Agent Rotator – Rotates the User Agent – with different User Agent Strings.
9.	Facebook <og:> open graph tag extractor – title , description ,url etc.

# Security Measures: 
1.	Rate Limiting – Slave crawler – AmazonAurora (project) , 30 Seconds to crawl the 1 + 8 child URLs. Within which if we try to submit the URLS it doesn’t accept it. Prevention of DDos Attack strategy.
2.	No More than 9 HTTP Links can be crawled from the same end server – to be crawler using different user agent strings.
3.	CrawlContract.java – has the politeness delay between each request as 1500 milliseconds.
4.	Use of final across class which cannot be extended or modified.
5.	ThreadSafe Immutability – Atomic Integer , Singleton classes to avoid repeated URL submission errors.

# Fault Tolerance-
HOTRESTARTMANAGER.JAVA PERSISTS THE URLS TO OPERATING SYSTEM FILE MANAGER , WHEN CRAWLER BREAKS IT RESUMES TO START CRAWLING FROM THE FILE /HOME/EC2-USER/ PATH.


# In Local Setup:

Slave Web Crawler – 
1.	Git clone https://github.com/krithivasanchandran/amazonaurora.git
2. 	Mvn clean install
3.	In ide – Right Click on AuroraMainApp.java -> Run as Java Application.

#Test using this URL : 
http://localhost:8080/startCrawl?url=http://flipkart.com

You should be able to see the logs in the console.

# AWS Cloud Setup

If there are changes to endpoint Aurora MySQL RDS -> crawldb.c9nylpylkekg.us-east-1.rds.amazonaws.com other than this, then you have to follow the 
below steps: 
1. git clone https://github.com/krithivasanchandran/amazonaurora.git
2. If a different endpoint please update the endpoint here - 
https://github.com/krithivasanchandran/amazonaurora/blob/master/src/main/java/AuroraMySql/DatabaseManager/DaoManager.java#L12
3. mvn clean install
4. Copy amazonaurora-1.0.jar from local to S3 bucket -> https://s3.console.aws.amazon.com/s3/buckets/aurorachallenge/?region=us-east-1&tab=overview
5. You need to run the MasterWebCrawler - repo - thats it. 

# Please reach out to me : c.krithivasan@gmail.com - if you have stuck with deploy issues. deployment scripts aws is in AuroraMasterNode repo.Thanks
