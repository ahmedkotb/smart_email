# consumer.rb
require "rubygems"
require "mq"

AMQP.start do
  queue = MQ.queue('hello.world.queue')
  
  queue.subscribe do |word|
    puts word
  end
  
end
