# databus-online-stats
Implementation of a Databus web service to check whether all download links are working


## Example Call
```
#create new request
curl -X PUT "http://localhost/online/index.php?file=https://databus.dbpedia.org/dbpedia/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=hi.ttl.bz2&sha256sum=cf52dda5ef16f823702aba3f41db14e4f2d1f758e88070158eed331eeb609ec5&downloadURL=https://downloads.dbpedia.org/repo/lts/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=hi.ttl.bz2"
 
 
 ```

```
sudo amm check_if_online.scala /var/www/html/online/repo
```
