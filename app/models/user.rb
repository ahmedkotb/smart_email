class User < ActiveRecord::Base

has_many :accounts, :dependent => :destroy
end
