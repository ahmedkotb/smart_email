# producer.rb
require "rubygems"
require "mq"

AMQP.start do
  queue = MQ.queue('hello.world.queue')
  
  i = 0
  EM::add_periodic_timer(1) do
    queue.publish "hello world #{i+=1}"
  end
  
end
