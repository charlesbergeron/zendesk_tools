Zendesk tools
=============

This tool is used to extract data from Zendesk's Helpcenter, and put it in a csv file.

The main focus was to get the list of all Articles, Sections and Categories, along with any missing translation for each item.

The locales currently extracted are en-us, fr-ca and es.


Exemple of usage:
java -jar zendesk_tools.jar -u="xyz@company.com" -p="password" -d="company_domain_on_zendesk" -curl="C:\curl\curl.exe" -get="translations" -o="c:\tmp\output.csv"
