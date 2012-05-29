namespace :smart_email do
  desc "Monitoring gmail accounts"
  task :monitor_email => :environment do
      Fetcher.perform
    end
end
