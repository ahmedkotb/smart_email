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

	def self.fetch_new_emails account

		gmail = Gmail.new(account.username, account.password)
    puts "last visited: #{account.last_visited}"
		gmail.inbox.emails(:after => account.last_visited).each do |email|
			puts email.message.subject
		end
		gmail.logout

		account.last_visited = DateTime.now
		account.save		
	end

	def self.perform

			users = User.all
			users.each do |user|
				accounts = user.accounts.all

				accounts.each do |account|
					Thread.new do
 						do_idle account
					end

				end
				
			end
	end

end
