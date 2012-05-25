class Monitor

	@queue = :monitoring_queue

	def self.perform

			users = User.all
			users.each do |user|
				accounts = user.accounts.all

				accounts.each do |account|
					gmail = Gmail.new(account.username, account.password)

					gmail.inbox.emails(:after => Date.parse("2011-04-20")).each do |email|
						puts email.message.subject
					end

					gmail.logout

				end
				
			end
	end

end
