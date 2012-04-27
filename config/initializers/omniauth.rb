# Do not forget to restart your server after changing this file
Rails.application.config.middleware.use OmniAuth::Builder do  
  # you need a store for OpenID
  require 'openid/store/filesystem'
  provider :openid, :store => OpenID::Store::Filesystem.new('/tmp'), :name => 'openid'
  provider :openid, :store => OpenID::Store::Filesystem.new('/tmp'), :name => 'google', :identifier => 'https://www.google.com/accounts/o8/id'
end
