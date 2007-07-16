# The contents of this file are subject to the terms of the Common Development
# and Distribution License (the License). You may not use this file except in
# compliance with the License.
#
# You can obtain a copy of the License at http://www.netbeans.org/cddl.html
# or http://www.netbeans.org/cddl.txt.
#
# When distributing Covered Code, include this CDDL Header Notice in each file
# and include the License file at http://www.netbeans.org/cddl.txt.
# If applicable, add the following below the CDDL Header, with the fields
# enclosed by brackets [] replaced by your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# The Original Software is NetBeans. The Initial Developer of the Original
# Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
# Microsystems, Inc. All Rights Reserved.

require 'uri'
require 'net/http'
require 'rexml/document'

class Faqpage
  def initialize (title, url)
    @title = title
    @url = url
  end
  
  def to_s
    return @title + ' (' + @url + ')'
  end
  
  def ==(other)
    @url = other.url
  end
  
  def hash
    @url.hash
  end
  
  def name
    /.*(DevFaq.*)/.match(@url)[1]
  end
  
  def title
    @title
  end
end

base = 'http://wiki.netbeans.org'
faqsite = base + "/wiki/view/NetBeansDeveloperFAQ"
content = Net::HTTP.get(URI.parse(faqsite))
doc = REXML::Document.new content
puts "Loaded content from #{faqsite}"
matches = {}
titleexp = /.*?>(.*?)<.*/
faqexp = /.*DevFaq.*/
allcontent = '<html><head><title>NetBeans Master Developer FAQ</title></head><body><h1>NetBeans Master Developer FAQ</h1><ul>'
allcontent += "Generated " + Time.now.gmtime.to_s + "<p>"
REXML::XPath.each(doc,'//div//li/a[@class="wikipage"]') do |match|
  path = match.attribute('href')
  if (faqexp.match(match.to_s))
    spath = base + path.to_s
    #skip two huge and unwieldy FAQ items - the app client one should
    #probably not be in the FAQ, but in the tutorials section of the web site
    if ('http://wiki.netbeans.org/wiki/view/DevFaqWindowsInternals' != spath &&
        'http://wiki.netbeans.org/wiki/view/DevFaqAppClientOnNbPlatformTut' != spath  &&
        'http://wiki.netbeans.org/wiki/view/DevFaqTutorialsAPIJa' != spath &&
        'http://wiki.netbeans.org/wiki/view/DevFaqTutorialsAPI' != spath) 
      title = titleexp.match(match.to_s)[1]
      page = Faqpage.new(title, spath)
      matches[spath] = page
      puts "Found #{page}"
    end
  end
end

matches.each { |url, faqpage|
   allcontent += '<li><a href="#' + faqpage.name() + '">' + faqpage.title() + "</a></li>\n"
}

allcontent += "</ul>\n"
matches.keys.sort.each { |url| 
  puts "FETCHING #{url}"
  content = Net::HTTP.get(URI.parse(url))
  doc = REXML::Document.new content
  pagecontentDoc = REXML::XPath.first(doc, '//div[@id="pagecontent"]')
  pagecontent = pagecontentDoc.to_s
  contentdoc = REXML::XPath.each(pagecontentDoc, '//a[@class="wikipage"]') do |match|
    linkAttr = match.attribute('href')
    if (linkAttr) 
      puts "Check link #{linkAttr}"
      link = linkAttr.to_s
      if (link && /\/wiki\/view.*?/.match(link)) 
        link = base + link
        if (matches[link])
          link = "#" + matches[link].name()
        end
        puts "Substitute #{linkAttr} with #{link}"
        pagecontent.gsub!(linkAttr.to_s, link)
        #Faq entries are inconsistent about title form, normalize
        pagecontent.gsub!('<h1', '<h2')
        pagecontent.gsub!('<h3', '<h2')
        pagecontent.gsub!('<h4', '<h2')
        pagecontent.gsub!('</h1', '</h2')
        pagecontent.gsub!('</h3', '</h2')
        pagecontent.gsub!('</h4', '</h2')
        
      end
    end
  end
  allcontent += '<a name="' + matches[url].name() + '">'
  allcontent += pagecontent
}
allcontent += '</body></html>'

out=($*[0] or '/tmp/faq.html')
puts "WRITING #{out}"
open(out, 'w') { |file| file.puts(allcontent) }