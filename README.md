## WSPro Report

This project has been developed by Özcan ÇAĞIRICI for bootcamp innovation week

### Getting Started

These instructions will get you a copy of the project up and running on your local machine 
for development and testing purposes. 

### Configuration
Some of properties must be set before using this application.
##### application name
```
application.name = WsPro Compliance-Productivity Report
```
You can use default application name.


##### smtp configuration
```
mail.auth.username = xxx@aurea.com
mail.auth.password = xxxxxxxxxx
mail.replyTo = username@aurea.com
mail.cc.list=username@aurea.com,
```
You can use your email and password. Note that, If you use gmail 2-step verification, 
you must create an [application password](https://myaccount.google.com/apppasswords) 
for this application.

Select Other (Custom name) and set this application.name value then generate.
![how to app password create](images/gmailAppPassword.png)

mail.replyTo also mandatory. You can set your email adress.

mail.cc.list optinal. You can leave empty. If you set more than one, please write
them seperated by comma.

##### other smtp configuration
```
mail.smtp.host = smtp.gmail.com
mail.smtp.port = 587
mail.smtp.auth = true
mail.smtp.starttls.enable = true
```
Above settings already setted for gmail smtp. If you choose another smtp server,
you have to set that server's settings.

##### test smtp configuration
```
mail.test = true
```
This option set true means, don't send email any ICs, only send an email to me.
If you want to see result while changing any template, you can set true this parameter.

#### template configuration
```
template.folder = /templates/
template.daily = DailyReport
template.weekly = WeeklyReport
```
Set relative path for template folder that contains template files.
You can use default values. You can also change template file or their contents.
Note that, Application sends some values to template file. Their name are important. 
You should not change constant's name and their type.

##### Google SpreadSheet configuration
```
sheet.compliance.source = 1bI6YOhGianCq6koOMfWwHY9xeMwX6nCPxJghBhSN4uA
sheet.compliance.data.name = Index
sheet.compliance.data.startColumn = A2
sheet.compliance.data.endColumn = AN

sheet.compliance.email.name = MASTER_SHEET
sheet.compliance.email.startColumn = A2
sheet.compliance.email.endColumn = F

sheet.gradebook.source = 1TNc1P8Yolas7s27VgBIks5fFP77LGMbscKxuuVP5YME
sheet.gradebook.data.name = Gradebook
sheet.gradebook.data.startColumn = A3
sheet.gradebook.data.endColumn = N
```
Note that, The application uses two different google sheet. 

The compliance sheet contains compliance data and ICs's mail adresses. You can change any other
google sheet but If you do this, you have to change source parameter to new compliance sheet id.
These values very important for the application. If source id's or name/column index will have
an error, application doesn't run or create wrong reports.

The application also uses gradebook google sheet. It containse weekly grade of Bootcamper.

below links refer google sheets which is used this application. 

Note that you should grant access to view those google sheets.

#### Google API Authorization
The first time you run or debug  the application, it will prompt you to authorize access:
```
Please open the following address in your browser:
  https://accounts.google.com/o/oauth2/auth?client_id.........
```
if your browser will not open automatically, copy and paste link to browser and authorize.

![how to app password create](images/gmailAppPassword.png)

![how to app password create](images/gmailAppPassword.png)

## Running and Debuging
### Usage for Daily Report
```
* mvn spring-boot:run dailyReport manager="<manager-name>"
or
* java -jar ws-pro-report-<version>.jar dailyReport manager="manager-name"
```
above command reports last work day, generates report and send email to active ICs work email.
#### Optinal parameters for Daily Report
```
* date = "<yyyy-MM-dd>" 
```
##### usage of date param
```
* mvn spring-boot:run dailyReport manager="<manager-name>" date="2018-12-10"
or
* java -jar ws-pro-report-<version>.jar dailyReport manager="manager-name" date="2018-12-10"
```
above command reports given date, generates report and send email to active IC's work email.
```
* dryRun : generates report but don't send email to ICs. Save report to disk.
```
##### usage of dryRun param
```
* mvn spring-boot:run dailyReport manager="<manager-name>" dryRun
or
* java -jar ws-pro-report-<version>.jar dailyReport manager="manager-name" dryRun
```
above command reports given date, generates report and save report to disk. Don't send any email.

### Usage for Weekly Report
```
* mvn spring-boot:run weeklyReport
or
* java -jar ws-pro-report-<version>.jar weeklyReport
```
above command reports last week, generates report and send email to active ICs work email.
#### Optinal parameters for Weekly Report
```
* thisweek 
```
thisweek param, creates weekly report from fist day of current week to today.
##### usage of thisweek param
```
* mvn spring-boot:run weeklyReport thisweek
or
* java -jar ws-pro-report-<version>.jar weeklyReport thisweek
```
above command report from this week monday to today and send email to active ICs work email.
```
* date = "<yyyy-MM-dd>"
```
##### usage of date param
```
* mvn spring-boot:run weeklyReport date="2018-12-10"
or
* java -jar ws-pro-report-<version>.jar weeklyReport date="2018-12-10"
```
above command report from given date to today and send email to active ICs work email.
```
* dryRun : generates report but don't send email to ICs. Save report to disk.
```
##### usage of dryRun param
```
* mvn spring-boot:run weeklyReport dryRun
or
* java -jar ws-pro-report-<version>.jar weeklyReport dryRun
```
above command reports given last week, generates report and save report to disk. Don't send any email.
```
* mvn spring-boot:run weeklyReport thisweek dryRun
or
* java -jar ws-pro-report-<version>.jar weeklyReport thisweek dryRun
```
above command reports this week, generates report and save report to disk. Don't send any email.

## Google SpreadSheet data filling
You can fill data to spreadsheet manually. But It has a time-base trigger like cron-job. It runs daily and weekly based.
Daily base trigger runs every  04:00 AM, weekly base trigger runs every Monday 04:00 AM.

## Authors

* **Özcan ÇAĞIRICI** -  [github.com/ozcancagirici](https://github.com/ozcancagirici)
