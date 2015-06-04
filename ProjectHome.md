<h2>The Java API for the Encyclopedia Metallum.</h2>

I build this API for programmers which want to make great programs with the data of <a href='http://www.metal-archives.com/'>Encyclopedia Metallum<a>.<br>
<br>
The source code is free as it could be but still in development.<br>
<b>The source code is available under the Beerware License</b>:<br>
<pre><code><br>
<br>
/*<br>
* ----------------------------------------------------------------------------<br>
* "THE BEER-WARE LICENSE" (Revision 42):<br>
* Phillip Wirth&lt;loooki@gulli.com&gt;  wrote this file. As long as you retain this notice you<br>
* can do whatever you want with this stuff. If we meet some day, and you think<br>
* this stuff is worth it, you can buy me a beer in return Phillip Wirth.<br>
* ----------------------------------------------------------------------------<br>
*/<br>
</code></pre>


Simple example for searching for the Band "Slayer":<br>
<pre><code><br>
<br>
public static void main(String[] args) {<br>
final BandSearchService service = new BandSearchService();<br>
final BandSearchQuery query = new BandSearchQuery();<br>
//		Here we give some parameters to the query<br>
//		In our case the name of the band and<br>
//		the second parameter here is exact match, because we are sure that there is a Band named Slayer<br>
query.setBandName("Slayer", true);<br>
//		now we say "search", like pressing the search button<br>
try {<br>
final List&lt;Band&gt; resultList = service.performSearch(query);<br>
for (final Band band : resultList) {<br>
System.out.println("Bandname: " + band.getName());<br>
System.out.println("Bandgenre: " + band.getGenre());<br>
System.out.println("Bandstatus: " + band.getStatus().asString());<br>
System.out.println("---");<br>
}<br>
} catch (MetallumException e) {<br>
e.printStackTrace();<br>
}<br>
}<br>
</code></pre>

<h3>Checkout</h3>
I would advise you to check the project out and build it for yourself till there is no maven repository. Make sure that your IDE does treat the files at UTF-8 ones otherwise the unit tests will fail.<br>
<br>
<br>
<h3>Maven Repo</h3>
If you lvoe maven as much as I do may you can help:<br>
I have no idea how to push this project into a maven repository and actually don't have the time to get into it. So if you want to do that just write a mail.<br>
<br>
<h3>dependencies</h3>
<ul>
<li>http-client - <a href='http://hc.apache.org/httpclient-3.x/'>http://hc.apache.org/httpclient-3.x/</a></li>
<li>json</li>
<li>jsoup - <a href='http://jsoup.org/'>http://jsoup.org/</a></li>
<li>xstream- <a href='http://xstream.codehaus.org/'>http://xstream.codehaus.org/</a></li>
<li>httpclient-cache - <a href='http://hc.apache.org/httpclient-3.x/'>http://hc.apache.org/httpclient-3.x/</a></li>
<li>log4j - <a href='http://logging.apache.org/log4j/2.x/'>http://logging.apache.org/log4j/2.x/</a></li>
</ul>


If you'd like to donate some money instead of beer use paypal:<br>
<a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=BYMWLLFLKU66N'><img src='https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif'><a>