class Fetcher

	@queue = :monitoring_queue
 
	def self.do_idle account
		imap = Net::IMAP.new('imap.gmail.com',993,true,nil,false)
		imap.login(account.username,account.password)
		imap.select 'INBOX'
		imap.idle { |resp|
      puts "Mailbox now has #{resp.data} messages"
      fetch_new_emails account
		}
	end

  def self.send_post_request(url, body)
    uri = URI.parse(url)
    http = Net::HTTP.new(uri.host, uri.port)
    request = Net::HTTP::Post.new(uri.request_uri)
    request.body = body
    request['Content-Type'] = 'application/xml'
    return http.request(request)
  end

	def self.fetch_new_emails account
		gmail = Gmail.new(account.username, account.password)
    puts "last visited: #{account.last_visited}"
		gmail.inbox.emails(:after => account.last_visited).each do |email|			
			puts "Email subject = #{email.message.subject}"
      puts "Email uid = #{email.uid}"
      xml = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
      xml += '<incomingEmailMessage>'
      xml += '<emailId>' + email.uid.to_s + '</emailId>'
      xml += '<username>' + account.username + '</username>'
      xml += '</incomingEmailMessage>'
      url = 'http://localhost:8080/smart_email/rest/service/provider/classify'
      response = send_post_request(url, xml)
      puts "Response = #{response}"
		end
		gmail.logout

		account.last_visited = DateTime.now + 2.hours
		puts "Updated last visited #{account.last_visited}"
		account.save		
	end

	def self.form_mime_email(email)
    mime = "Message-ID: "+email.message_id + "\r\n"
    mime += "Date: " + email.date + "\r\n"
    mime += "From: " + email.from + "\r\n"
    mime += "To: " + email.to + "\r\n"
    mime += "Subject: " + email.subject + "\r\n"
    mime += "Mime-Version: " + email.mime_version + "\r\n"
    mime += "Content-Type: " + email.content_type + "\r\n"
    mime += "Content-Transfer-Encoding:  " + "\r\n"
    mime += "X-From:  " + "\r\n"
    mime += "X-To:  " + "\r\n"
    mime += "X-cc:  " + "\r\n"
    mime += "X-bcc:  " + "\r\n"
    mime += email.body + "\r\n"
    return mime
  end

	def self.perform
    users = User.all
    users.each do |user|
      accounts = user.accounts.all
      accounts.each do |account|
#        Thread.new do
          do_idle account
#        end
      end
    end
	end
end
