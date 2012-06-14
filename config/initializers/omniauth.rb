# Do not forget to restart your server after changing this file
#Rails.application.config.middleware.use OmniAuth::Builder do  
  # you need a store for OpenID
 # require 'openid/store/filesystem'
  #provider :openid, :store => OpenID::Store::Filesystem.new('/tmp'), :name => 'openid'
  #provider :openid, :store => OpenID::Store::Filesystem.new('/tmp'), :name => 'google', :identifier => 'https://www.google.com/accounts/o8/id'
#end

#OmniAuth.config.full_host = "http://localhost:3000"

Rails.application.config.middleware.use OmniAuth::Builder do
  #provider :google, 'smart-email.herokuapp.com', '9nBqknwJqXo1jRFGYvEC6iyU'
require 'openid/store/filesystem'

  #provider :openid, nil, :name => 'google', :identifier => 'https://www.google.com/accounts/o8/id'
provider :openid,:store => OpenID::Store::Filesystem.new('/tmp'), :name => 'google', :identifier => 'https://www.google.com/accounts/o8/id', :require => 'omniauth-openid'
end
