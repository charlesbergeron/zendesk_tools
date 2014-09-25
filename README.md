Zendesk Help Center tools
=============

This tool is used to extract data from Zendesk's Help Center, and put it in a csv file.

Currently supported data extractions :
 - List of all tickets with their attached documents.
 - Articles, Sections and Categories translations (The locales currently extracted are en-us, fr-ca and es.)
 - Sections Access policies


How to use :
java -jar zendesk_tools.jar -u= -p= -d= -curl= -get= -o=

Description of parameters	
	-u Zendesk user name
	-p= Password
	-d= Company domain on zendesk (ex. https://thevalueisputhere.zendesk.com/api/v2/)
	-curl= Full path to cUrl executable
	-get= Which data set will be extracted
		Possible values are
			access
			tickets
			translations
	-o= Full path to the file where the data will be extract extracted to


Exemple of usage:
java -jar zendesk_tools.jar -u="xyz@company.com" -p="password" -d="company_domain_on_zendesk" -curl="C:\curl\curl.exe" -get="translations" -o="c:\tmp\output.csv"
